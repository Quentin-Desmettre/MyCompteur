import os
import asyncpg

_pool: asyncpg.Pool | None = None

SCHEMA = """
CREATE TABLE IF NOT EXISTS sessions (
    id            SERIAL PRIMARY KEY,
    share_token   TEXT UNIQUE NOT NULL,
    athlete_name  TEXT,
    route_name    TEXT,
    total_route_m DOUBLE PRECISION NOT NULL DEFAULT 0,
    route_geojson JSONB,
    started_at    BIGINT NOT NULL,
    ended_at      BIGINT,
    status        TEXT NOT NULL DEFAULT 'live'
);

CREATE TABLE IF NOT EXISTS samples (
    id               BIGSERIAL PRIMARY KEY,
    session_id       INTEGER NOT NULL REFERENCES sessions(id) ON DELETE CASCADE,
    t                BIGINT NOT NULL,
    lat              DOUBLE PRECISION,
    lng              DOUBLE PRECISION,
    speed_kph        REAL,
    hr               INTEGER,
    cadence          INTEGER,
    power            INTEGER,
    ascent_m         REAL,
    dist_total_m     REAL,
    dist_along_m     DOUBLE PRECISION,
    dist_remaining_m DOUBLE PRECISION,
    fraction_done    DOUBLE PRECISION,
    eta_epoch_ms     BIGINT,
    elapsed_s        BIGINT
);

CREATE INDEX IF NOT EXISTS idx_samples_session_t ON samples(session_id, t);
"""


async def init_pool() -> asyncpg.Pool:
    global _pool
    dsn = os.environ.get("DATABASE_URL")
    if not dsn:
        host = os.environ.get("POSTGRES_HOST", "db")
        user = os.environ.get("POSTGRES_USER", "compteur")
        password = os.environ.get("POSTGRES_PASSWORD", "compteur")
        database = os.environ.get("POSTGRES_DB", "compteur")
        dsn = f"postgresql://{user}:{password}@{host}:5432/{database}"
    _pool = await asyncpg.create_pool(dsn, min_size=1, max_size=10)
    async with _pool.acquire() as conn:
        await conn.execute(SCHEMA)
    return _pool


async def close_pool() -> None:
    global _pool
    if _pool is not None:
        await _pool.close()
        _pool = None


def pool() -> asyncpg.Pool:
    if _pool is None:
        raise RuntimeError("Pool not initialized")
    return _pool
