import asyncio
from fastapi import WebSocket


class ConnectionManager:
    """Diffuse les nouveaux échantillons aux navigateurs abonnés à un share_token."""

    def __init__(self) -> None:
        self._rooms: dict[str, set[WebSocket]] = {}
        self._lock = asyncio.Lock()

    async def connect(self, token: str, ws: WebSocket) -> None:
        await ws.accept()
        async with self._lock:
            self._rooms.setdefault(token, set()).add(ws)

    async def disconnect(self, token: str, ws: WebSocket) -> None:
        async with self._lock:
            room = self._rooms.get(token)
            if room:
                room.discard(ws)
                if not room:
                    self._rooms.pop(token, None)

    async def broadcast(self, token: str, message: dict) -> None:
        async with self._lock:
            targets = list(self._rooms.get(token, set()))
        dead: list[WebSocket] = []
        for ws in targets:
            try:
                await ws.send_json(message)
            except Exception:
                dead.append(ws)
        for ws in dead:
            await self.disconnect(token, ws)


manager = ConnectionManager()
