FROM python:3.12-slim

WORKDIR /app

# Build context = dossier server/ (voir docker-compose.yml)
COPY api/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY api/app ./app
# Frontend statique servi par l'API (monté via StaticFiles dans main.py).
COPY web ./web

EXPOSE 8000

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
