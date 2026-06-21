// Token de partage extrait de l'URL /live/<token>
const TOKEN = window.location.pathname.split("/").filter(Boolean).pop();
const MAP_STYLE = "https://tiles.openfreemap.org/styles/liberty";

let map = null;
let routeDrawn = false;
let lastT = 0;
const traveled = []; // [lng, lat] de la trace réelle
const charts = {};

function fmtKm(m) {
  if (m == null) return "—";
  return (m / 1000).toLocaleString("fr-FR", { minimumFractionDigits: 1, maximumFractionDigits: 1 });
}
function fmtTime(secs) {
  if (secs == null || secs < 0) return "—";
  const h = Math.floor(secs / 3600);
  const m = Math.floor((secs % 3600) / 60);
  const s = Math.floor(secs % 60);
  return h > 0 ? `${h}h${String(m).padStart(2, "0")}` : `${m}:${String(s).padStart(2, "0")}`;
}
function fmtEta(epochMs) {
  if (!epochMs || epochMs < 0) return "—";
  return new Date(epochMs).toLocaleTimeString("fr-FR", { hour: "2-digit", minute: "2-digit" });
}
function num(v, digits = 0) {
  if (v == null) return "—";
  return Number(v).toLocaleString("fr-FR", { maximumFractionDigits: digits });
}

function initMap(geojson) {
  map = new maplibregl.Map({
    container: "map",
    style: MAP_STYLE,
    center: [2.35, 48.85],
    zoom: 11,
  });
  map.on("load", () => {
    if (geojson && geojson.coordinates && geojson.coordinates.length) {
      map.addSource("route", { type: "geojson", data: { type: "Feature", geometry: geojson } });
      map.addLayer({
        id: "route", type: "line", source: "route",
        paint: { "line-color": "#3b82f6", "line-width": 4, "line-opacity": 0.8 },
      });
      const b = new maplibregl.LngLatBounds();
      geojson.coordinates.forEach((c) => b.extend(c));
      map.fitBounds(b, { padding: 40 });
      routeDrawn = true;
    }
    map.addSource("traveled", { type: "geojson", data: traveledFeature() });
    map.addLayer({
      id: "traveled", type: "line", source: "traveled",
      paint: { "line-color": "#fc4c02", "line-width": 4 },
    });
    map.addSource("pos", { type: "geojson", data: posFeature(null) });
    map.addLayer({
      id: "pos", type: "circle", source: "pos",
      paint: { "circle-radius": 8, "circle-color": "#fc4c02", "circle-stroke-color": "#fff", "circle-stroke-width": 2 },
    });
  });
}

function traveledFeature() {
  return { type: "Feature", geometry: { type: "LineString", coordinates: traveled } };
}
function posFeature(lngLat) {
  return {
    type: "FeatureCollection",
    features: lngLat ? [{ type: "Feature", geometry: { type: "Point", coordinates: lngLat } }] : [],
  };
}

function makeChart(id, label, color) {
  const ctx = document.getElementById(id);
  charts[id] = new Chart(ctx, {
    type: "line",
    data: { datasets: [{ label, data: [], borderColor: color, backgroundColor: color + "33", pointRadius: 0, tension: 0.3, borderWidth: 2, fill: true }] },
    options: {
      animation: false,
      parsing: false,
      scales: {
        x: { type: "linear", ticks: { color: "#8a8a90", callback: (v) => fmtTime(v) }, grid: { color: "#26262b" } },
        y: { ticks: { color: "#8a8a90" }, grid: { color: "#26262b" } },
      },
      plugins: { legend: { display: false } },
    },
  });
}

function pushPoint(id, x, y) {
  if (y == null) return;
  const ds = charts[id].data.datasets[0].data;
  ds.push({ x, y });
}

function applySample(s) {
  // Tuiles "dernière valeur"
  document.getElementById("v-speed").textContent = num(s.speed_kph, 1);
  document.getElementById("v-hr").textContent = num(s.hr);
  document.getElementById("v-cad").textContent = num(s.cadence);
  document.getElementById("v-pow").textContent = num(s.power);
  document.getElementById("v-dist").textContent = fmtKm(s.dist_total_m);
  document.getElementById("v-asc").textContent = num(s.ascent_m);

  // Progression / ETA
  if (s.fraction_done != null) {
    const pct = Math.round(s.fraction_done * 100);
    document.getElementById("progress-fill").style.width = pct + "%";
    document.getElementById("progress-pct").textContent = pct + " %";
  }
  if (s.dist_along_m != null) {
    document.getElementById("dist-done").textContent = fmtKm(s.dist_along_m) + " km";
  }
  if (s.dist_remaining_m != null) {
    document.getElementById("dist-remaining").textContent = fmtKm(s.dist_remaining_m) + " km restants";
  }
  document.getElementById("eta").textContent = fmtEta(s.eta_epoch_ms);
  if (s.eta_epoch_ms && s.eta_epoch_ms > 0) {
    document.getElementById("time-remaining").textContent = "dans " + fmtTime((s.eta_epoch_ms - Date.now()) / 1000);
  }

  // Graphes (axe x = temps écoulé en secondes)
  const x = s.elapsed_s != null ? s.elapsed_s : (s.t / 1000);
  pushPoint("chart-hr", x, s.hr);
  pushPoint("chart-speed", x, s.speed_kph);
  pushPoint("chart-cadence", x, s.cadence);
  pushPoint("chart-power", x, s.power);
  pushPoint("chart-ascent", x, s.ascent_m);

  // Carte : position + trace
  if (s.lng != null && s.lat != null && map) {
    traveled.push([s.lng, s.lat]);
    const t = map.getSource("traveled");
    if (t) t.setData(traveledFeature());
    const p = map.getSource("pos");
    if (p) p.setData(posFeature([s.lng, s.lat]));
    if (!routeDrawn) map.easeTo({ center: [s.lng, s.lat], zoom: Math.max(map.getZoom(), 13) });
  }
  lastT = Math.max(lastT, s.t);
}

function redrawCharts() {
  Object.values(charts).forEach((c) => c.update("none"));
}

async function bootstrap() {
  makeChart("chart-hr", "bpm", "#ef4444");
  makeChart("chart-speed", "km/h", "#3b82f6");
  makeChart("chart-cadence", "rpm", "#22c55e");
  makeChart("chart-power", "W", "#eab308");
  makeChart("chart-ascent", "m", "#a855f7");

  const meta = await fetch(`/api/live/${TOKEN}`).then((r) => r.json());
  document.getElementById("title").textContent = meta.route_name || meta.athlete_name || "Suivi live";
  setStatus(meta.status);
  initMap(meta.route_geojson);

  // Historique initial pour remplir les graphes
  const samples = await fetch(`/api/live/${TOKEN}/samples?since=0`).then((r) => r.json());
  samples.forEach(applySample);
  redrawCharts();

  connectWs();
  // Repli : si le WebSocket tombe, on continue à poller toutes les 5 s.
  setInterval(poll, 5000);
}

function setStatus(status) {
  const el = document.getElementById("status");
  el.className = "status " + (status || "");
  el.textContent = status === "finished" ? "Course terminée" : "● En direct";
}

let ws = null;
function connectWs() {
  const proto = window.location.protocol === "https:" ? "wss" : "ws";
  ws = new WebSocket(`${proto}://${window.location.host}/ws/live/${TOKEN}`);
  ws.onmessage = (ev) => {
    const msg = JSON.parse(ev.data);
    if (msg.type === "sample") {
      applySample(msg.sample);
      redrawCharts();
    } else if (msg.type === "finished") {
      setStatus("finished");
    }
  };
  ws.onclose = () => setTimeout(connectWs, 3000);
}

async function poll() {
  try {
    const samples = await fetch(`/api/live/${TOKEN}/samples?since=${lastT}`).then((r) => r.json());
    if (samples.length) {
      samples.forEach(applySample);
      redrawCharts();
    }
  } catch (e) { /* ignore */ }
}

bootstrap();
