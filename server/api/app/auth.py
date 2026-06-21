import os
from fastapi import Header, HTTPException


def require_ingest_key(x_ingest_key: str | None = Header(default=None)) -> None:
    """Vérifie la clé d'ingestion secrète envoyée par l'app dans l'en-tête X-Ingest-Key."""
    expected = os.environ.get("INGEST_KEY", "")
    if not expected:
        raise HTTPException(status_code=500, detail="INGEST_KEY non configurée côté serveur")
    if x_ingest_key != expected:
        raise HTTPException(status_code=401, detail="Clé d'ingestion invalide")
