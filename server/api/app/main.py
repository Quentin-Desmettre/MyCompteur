import json
import os
from contextlib import asynccontextmanager
from pathlib import Path

from fastapi import Depends, FastAPI, HTTPException, Query, WebSocket, WebSocketDisconnect
from fastapi.responses import FileResponse, JSONResponse
from fastapi.staticfiles import StaticFiles

from . import db
from .auth import require_ingest_key
from .models import Ingest, StartSession, StopSession
from .ws import manager

WEB_DIR = Path(__file__).resolve().parent.parent / "web"


@asynccontextmanager
async def lifespan(app: FastAPI):
    await db.init_pool()
    yield
    await db.close_pool()


app = FastAPI(title="MyCompteur Live", lifespan=lifespan)


@app.get("/healthz")
async def healthz():
    async with db.pool().acquire() as conn:
        await conn.execute("SELECT 1")
    return {"status": "ok"}


# --------- Ingestion (protégée par X-Ingest-Key) ---------

@app.post("/api/session/start", dependencies=[Depends(require_ingest_key)])
async def start_session(body: StartSession):
    geojson = {
        "type": "LineString",
        "coordinates": [[p.lng, p.lat] for p in body.route_points],
    }
    async with db.pool().acquire() as conn:
        await conn.execute(
            """
            INSERT INTO sessions (share_token, athlete_name, route_name, total_route_m, route_geojson, started_at, status)
            VALUES ($1, $2, $3, $4, $5, $6, 'live')
            ON CONFLICT (share_token) DO UPDATE SET
                athlete_name = EXCLUDED.athlete_name,
                route_name = EXCLUDED.route_name,
                total_route_m = EXCLUDED.total_route_m,
                route_geojson = EXCLUDED.route_geojson,
                started_at = EXCLUDED.started_at,
                ended_at = NULL,
                status = 'live'
            """,
            body.share_token,
            body.athlete_name,
            body.route_name,
            body.total_route_m,
            json.dumps(geojson),
            body.started_at,
        )
    return {"ok": True}


@app.post("/api/ingest", dependencies=[Depends(require_ingest_key)])
async def ingest(body: Ingest):
    if not body.samples:
        return {"ok": True, "inserted": 0}
    async with db.pool().acquire() as conn:
        session_id = await conn.fetchval(
            "SELECT id FROM sessions WHERE share_token = $1", body.share_token
        )
        if session_id is None:
            raise HTTPException(status_code=404, detail="Session inconnue")
        rows = [
            (
                session_id, s.t, s.lat, s.lng, s.speed_kph, s.hr, s.cadence, s.power,
                s.ascent_m, s.dist_total_m, s.dist_along_m, s.dist_remaining_m,
                s.fraction_done, s.eta_epoch_ms, s.elapsed_s,
            )
            for s in body.samples
        ]
        await conn.executemany(
            """
            INSERT INTO samples
            (session_id, t, lat, lng, speed_kph, hr, cadence, power, ascent_m,
             dist_total_m, dist_along_m, dist_remaining_m, fraction_done, eta_epoch_ms, elapsed_s)
            VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,$13,$14,$15)
            """,
            rows,
        )
    # Diffuse le dernier échantillon aux navigateurs connectés.
    await manager.broadcast(body.share_token, {"type": "sample", "sample": body.samples[-1].model_dump()})
    return {"ok": True, "inserted": len(body.samples)}


@app.post("/api/session/stop", dependencies=[Depends(require_ingest_key)])
async def stop_session(body: StopSession):
    async with db.pool().acquire() as conn:
        await conn.execute(
            "UPDATE sessions SET ended_at = $1, status = 'finished' WHERE share_token = $2",
            body.ended_at,
            body.share_token,
        )
    await manager.broadcast(body.share_token, {"type": "finished", "ended_at": body.ended_at})
    return {"ok": True}


# --------- Lecture publique (via share_token, lecture seule) ---------

async def _session_row(token: str):
    async with db.pool().acquire() as conn:
        row = await conn.fetchrow("SELECT * FROM sessions WHERE share_token = $1", token)
        if row is None:
            raise HTTPException(status_code=404, detail="Session introuvable")
        last = await conn.fetchrow(
            "SELECT * FROM samples WHERE session_id = $1 ORDER BY t DESC LIMIT 1", row["id"]
        )
        agg = await conn.fetchrow(
            """
            SELECT
                AVG(speed_kph) AS avg_speed, MAX(speed_kph) AS max_speed,
                AVG(hr) AS avg_hr, MAX(hr) AS max_hr,
                AVG(power) AS avg_power, MAX(power) AS max_power,
                AVG(cadence) AS avg_cadence
            FROM samples WHERE session_id = $1
            """,
            row["id"],
        )
    return row, last, agg


@app.get("/api/live/{token}")
async def live_meta(token: str):
    row, last, agg = await _session_row(token)
    geojson = row["route_geojson"]
    if isinstance(geojson, str):
        geojson = json.loads(geojson)
    return JSONResponse({
        "share_token": token,
        "athlete_name": row["athlete_name"],
        "route_name": row["route_name"],
        "total_route_m": row["total_route_m"],
        "route_geojson": geojson,
        "started_at": row["started_at"],
        "ended_at": row["ended_at"],
        "status": row["status"],
        "last": dict(last) if last else None,
        "aggregates": {k: (float(v) if v is not None else None) for k, v in dict(agg).items()},
    })


@app.get("/api/live/{token}/samples")
async def live_samples(token: str, since: int = Query(default=0)):
    async with db.pool().acquire() as conn:
        session_id = await conn.fetchval("SELECT id FROM sessions WHERE share_token = $1", token)
        if session_id is None:
            raise HTTPException(status_code=404, detail="Session introuvable")
        rows = await conn.fetch(
            "SELECT * FROM samples WHERE session_id = $1 AND t > $2 ORDER BY t ASC",
            session_id,
            since,
        )
    return JSONResponse([dict(r) for r in rows])


@app.websocket("/ws/live/{token}")
async def ws_live(websocket: WebSocket, token: str):
    await manager.connect(token, websocket)
    try:
        while True:
            # On ne lit pas de données du client ; ce ping maintient la connexion et détecte la fermeture.
            await websocket.receive_text()
    except WebSocketDisconnect:
        await manager.disconnect(token, websocket)
    except Exception:
        await manager.disconnect(token, websocket)


@app.get("/live/{token}")
async def live_page(token: str):
    return FileResponse(WEB_DIR / "live.html")


# Frontend statique (live.html, app.js, styles.css) servi par l'API.
app.mount("/static", StaticFiles(directory=str(WEB_DIR)), name="static")
