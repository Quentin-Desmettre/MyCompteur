# MyCompteur — Suivi live public

Backend + site web qui affichent en direct la progression d'un athlète sur son parcours
(position, distance parcourue/restante, ETA) et tous les compteurs (BPM, vitesse, cadence,
puissance, dénivelé) avec graphe d'historique + dernière valeur.

## Architecture

- **db** — PostgreSQL 16 (sessions + échantillons de télémétrie).
- **api** — FastAPI/uvicorn : ingestion (protégée par `X-Ingest-Key`), lecture publique par
  `share_token`, WebSocket de diffusion temps réel, et sert le frontend statique.
- **caddy** — reverse-proxy avec TLS automatique (Let's Encrypt) sur votre domaine.

Le frontend (MapLibre GL JS + Chart.js) est servi par l'API ; pas de conteneur web séparé.

## Démarrage

```bash
cp .env.example .env
# éditez .env : INGEST_KEY (longue clé aléatoire), POSTGRES_PASSWORD, DOMAIN
docker compose up -d --build
```

- Santé : `GET http://localhost:8000/healthz`
- Page publique : `https://<DOMAIN>/live/<shareToken>` (ou `http://localhost:8000/live/<token>` en local)

Dans l'app Android (Réglages → Intégrations → Suivi live public) :
- activez le suivi,
- renseignez l'URL du serveur (`https://<DOMAIN>`),
- renseignez la même `INGEST_KEY`.

À chaque enregistrement sur un parcours, l'app génère un `shareToken`, pousse la géométrie du
parcours puis la télémétrie toutes les ~15 s, et affiche le lien de partage à copier/partager.

## Endpoints

| Méthode | Chemin | Auth | Rôle |
|---|---|---|---|
| POST | `/api/session/start` | `X-Ingest-Key` | Crée/met à jour la session + géométrie |
| POST | `/api/ingest` | `X-Ingest-Key` | Ajoute des échantillons + diffuse en WS |
| POST | `/api/session/stop` | `X-Ingest-Key` | Termine la session |
| GET | `/api/live/{token}` | public | Métadonnées + dernier échantillon + agrégats |
| GET | `/api/live/{token}/samples?since=t` | public | Série temporelle |
| WS | `/ws/live/{token}` | public | Flux temps réel vers le navigateur |
| GET | `/live/{token}` | public | Page de suivi |

## Test sans téléphone

Voir `simulate.sh` : envoie `session/start`, quelques `ingest`, puis ouvrez la page `/live/<token>`.
