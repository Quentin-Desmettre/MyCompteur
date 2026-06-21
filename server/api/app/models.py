from pydantic import BaseModel


class GeoPoint(BaseModel):
    lat: float
    lng: float
    ele: float | None = None


class StartSession(BaseModel):
    share_token: str
    athlete_name: str | None = None
    route_name: str | None = None
    total_route_m: float = 0.0
    route_points: list[GeoPoint] = []
    started_at: int


class Sample(BaseModel):
    t: int
    lat: float | None = None
    lng: float | None = None
    speed_kph: float | None = None
    hr: int | None = None
    cadence: int | None = None
    power: int | None = None
    ascent_m: float | None = None
    dist_total_m: float | None = None
    dist_along_m: float | None = None
    dist_remaining_m: float | None = None
    fraction_done: float | None = None
    eta_epoch_ms: int | None = None
    elapsed_s: int | None = None


class Ingest(BaseModel):
    share_token: str
    samples: list[Sample] = []


class StopSession(BaseModel):
    share_token: str
    ended_at: int
