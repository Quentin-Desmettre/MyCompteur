#!/usr/bin/env bash
# Simule une activité live sans téléphone, pour tester le backend.
# Usage: API=http://localhost:8000 KEY=changez-moi ./simulate.sh
set -euo pipefail

API="${API:-http://localhost:8000}"
KEY="${KEY:-changez-moi}"
TOKEN="${TOKEN:-demo$(date +%s)}"

echo "Token de partage : $TOKEN"
echo "Page publique    : $API/live/$TOKEN"

now() { echo $(( $(date +%s) * 1000 )); }

# Parcours : ligne le long de l'équateur (5 points), total ~2.2 km.
curl -fsS -X POST "$API/api/session/start" \
  -H "X-Ingest-Key: $KEY" -H "Content-Type: application/json" \
  -d "{\"share_token\":\"$TOKEN\",\"athlete_name\":\"Demo\",\"route_name\":\"Parcours démo\",\"total_route_m\":2226.0,\"started_at\":$(now),\"route_points\":[{\"lat\":0.0,\"lng\":0.0},{\"lat\":0.0,\"lng\":0.005},{\"lat\":0.0,\"lng\":0.010},{\"lat\":0.0,\"lng\":0.015},{\"lat\":0.0,\"lng\":0.020}]}" > /dev/null
echo "session/start OK"

START_MS=$(now)
for i in $(seq 0 19); do
  frac=$(awk "BEGIN{print $i/19}")
  lng=$(awk "BEGIN{print 0.020*$frac}")
  along=$(awk "BEGIN{print 2226.0*$frac}")
  remaining=$(awk "BEGIN{print 2226.0*(1-$frac)}")
  speed=$(awk "BEGIN{print 25 + ($i%5)}")
  hr=$(awk "BEGIN{print 130 + ($i%10)}")
  eta=$(awk "BEGIN{print $(now) + ($remaining/6.94)*1000}")
  elapsed=$(( i * 15 ))
  curl -fsS -X POST "$API/api/ingest" \
    -H "X-Ingest-Key: $KEY" -H "Content-Type: application/json" \
    -d "{\"share_token\":\"$TOKEN\",\"samples\":[{\"t\":$(now),\"lat\":0.0,\"lng\":$lng,\"speed_kph\":$speed,\"hr\":$hr,\"cadence\":85,\"power\":210,\"ascent_m\":$(( i * 2 )),\"dist_total_m\":$along,\"dist_along_m\":$along,\"dist_remaining_m\":$remaining,\"fraction_done\":$frac,\"eta_epoch_ms\":${eta%.*},\"elapsed_s\":$elapsed}]}" > /dev/null
  echo "ingest #$i (frac=$frac)"
  sleep 1
done

curl -fsS -X POST "$API/api/session/stop" \
  -H "X-Ingest-Key: $KEY" -H "Content-Type: application/json" \
  -d "{\"share_token\":\"$TOKEN\",\"ended_at\":$(now)}" > /dev/null
echo "session/stop OK"
echo "Ouvrez : $API/live/$TOKEN"
