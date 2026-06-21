# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

MyCompteur is an Android cycling computer ("compteur" = bike speedometer). It records rides
using GPS + BLE sensors (heart rate, cadence, speed, power), shows a live map, stores sessions
locally, and can export FIT files and upload to Strava. UI text and many code comments are in French.

## Build & Run

This is a Gradle Android project (no test framework wired up beyond default JUnit/Espresso stubs).

```bash
./gradlew assembleDebug          # build debug APK -> app/build/outputs/apk/debug/
./gradlew installDebug           # build + install on connected device/emulator
./gradlew lint                   # Android lint
./gradlew test                   # JVM unit tests (none meaningful yet)
./gradlew connectedAndroidTest   # instrumented tests (require a device; none meaningful yet)
```

- Min SDK 26, target/compile SDK 35, Java/JVM 17.
- The Android SDK path lives in `local.properties` (`sdk.dir`); it is machine-specific and untracked.
- Dependency versions are centralized in `gradle/libs.versions.toml` (version catalog). Add or bump
  deps there, not inline in `app/build.gradle.kts`.

## Architecture

Clean-ish architecture with three layers under `app/src/main/java/com/example/compteur/`:

- **`domain/`** — pure Kotlin: `model/` data classes, `repository/` interfaces, `usecase/` thin
  wrappers that the ViewModels call. Use cases are the intended entry point from UI, not repos directly.
- **`data/`** — implementations: `db/` (Room), `repository/` (`*Impl` bound to domain interfaces),
  `api/` (Strava Retrofit), `gpx/` (GPX import parser). Repository impls map Room entities ↔ domain models.
- **`ui/`** — Jetpack Compose, one package per screen, each with a `*Screen.kt` + `*ViewModel.kt`
  (Hilt `@HiltViewModel`). `ui/components/` holds shared MapLibre composables; `ui/theme/` Material3 theme.

Cross-cutting:
- **`service/`** — the recording engine and sensor logic (see below).
- **`di/`** — Hilt modules, all `@InstallIn(SingletonComponent::class)`: `DatabaseModule` (Room + DAOs),
  `RepositoryModule` (`@Binds` interface→impl), `NetworkModule` (Retrofit/Moshi/OkHttp), `AppModule`
  (a singleton application `CoroutineScope`), and `ParserModule`.
- **`utils/`** — `FitExporter`, `StravaConstants`, `DownloadUtil`, `ServiceUtils` (BT/GPS enabled checks).

DI is Hilt throughout. `CompteurApplication` (`@HiltAndroidApp`) initializes MapLibre and the
notification channel. `MainActivity` (`@AndroidEntryPoint`) hosts a single Compose `NavHost` with a
bottom nav (Dashboard / Recording / History / Settings) plus detail routes.

### Recording flow (the core of the app)

`RecordingService` is a bound, foreground (`location` type) service that owns a ride in progress:

- Started/controlled via `Intent` actions (`ACTION_START/STOP/PAUSE/RESUME`) carrying `EXTRA_SESSION_ID`.
- Exposes live state as `StateFlow`s (location, recorded path, distance, ascent, elapsed time, pause).
- `RecordingViewModel` **binds** to the service via `ServiceConnection` to read those flows directly —
  state is not passed through a repository. UI reads service flows live; persistence happens in parallel.
- GPS comes from `FusedLocationProviderClient`; update interval/priority adapts: if a BLE speed sensor
  is present, GPS is throttled (5s / balanced power) to save battery, else 1s / high accuracy.
- `BatchWriter` buffers GPS + sensor rows and flushes to Room in batches (size 20 or every 20s) to
  avoid per-sample DB writes. `stop()` does a final flush. Session summary (distance, avg speed,
  ascent) is computed and written to `SessionEntity` on stop.

### BLE sensors (`service/BleManager.kt`)

`@Singleton`, uses **Kable** (coroutine-based BLE, no callbacks/RxJava). Connects to standard GATT
profiles and decodes raw bytes by hand into a single `BleSensorData` flow:
- Heart Rate (0x180D / 0x2A37), Cycling Speed & Cadence — CSC (0x1816 / 0x2A5B),
  Cycling Power (0x1818 / 0x2A63).
- Speed/cadence are derived from cumulative revolution + event-time deltas (with 16-bit/32-bit
  rollover handling); CSC flags auto-detect device capabilities. Wheel circumference is hardcoded 2.1 m.
- Synchronized devices are persisted (Room `SynchronizedDeviceEntity`) and auto-reconnected when
  recording starts.

### Strava integration

OAuth via custom-tab → deep link. `MainActivity.handleIntent` catches the redirect (both
`https://strava.compteur.app/callback` and legacy `compteur://strava` schemes) and calls
`StravaRepository.authenticate(code)`. Tokens persist in `SettingsRepository` (DataStore).
Note: `utils/StravaConstants.kt` currently contains hardcoded client credentials.

### Persistence

- **Room** (`compteur_database`, version 1, `exportSchema=false`, KSP schema export configured to
  `app/schemas`). Entities: `Session`, `GpsPoint`, `SensorData`, `Route`, `RoutePoint`,
  `SynchronizedDevice`. **No migrations defined** — bumping the schema requires adding a migration
  or wiping data.
- **DataStore Preferences** (`settings`) via `SettingsRepository` for user settings (map style, HR
  zone config, Strava tokens). HR zones support three models: FCM, Karvonen, lactate threshold
  (`HeartRateZoneService` + `HrCalculationMode`).

### Maps

MapLibre GL (vector, offline-capable). `OfflineMapManager` handles offline region downloads;
map style URLs come from the `MapStyle` enum in `SettingsRepository` (note the satellite style
URL has a `PLACEHOLDER` API key).
