# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug       # Build debug APK
./gradlew assembleRelease     # Build release APK
./gradlew build               # Full build (all variants)
./gradlew test                # Run unit tests
./gradlew lint                # Run lint checks
./gradlew :app:testDebugUnitTest --tests "crystal.crystal.SomeTestClass" # Single test class
```

Java 17 is required. Gradle uses `-Xmx4g` and Kotlin daemon uses `-Xmx2g` (configured in `gradle.properties`). Configuration cache is disabled.

## What This App Does

Crystal is an Android business management app (in Spanish) for a glass/metalwork workshop. It handles:
- **Field measurements** for custom windows, doors, shower enclosures, and glass installations
- **Client & product management** with local-first sync to Firebase
- **Point of Sale** with ticket/receipt design and PDF generation
- **Design tools** with custom canvas drawing for Nova corrediza windows and glass panels
- **Inter-app chat** with a companion app called "Puntos" (cross-app Firestore messaging)
- **Wallet** for payment tracking and balance management

## Architecture

**Entry flow:** `Splash.kt` → `InicioActivity` (auth/bootstrap) → `MainActivity` (central hub, ~110KB, orchestrates all modules)

**Core patterns:**
- MVVM with `ViewModel` + `LiveData`
- **Local-first**: Room DB as source of truth, Firebase Firestore as sync backend via `WorkManager` workers (`SyncClientesWorker`, `SyncProductosWorker`)
- `ProyectoManager` singleton (SharedPreferences-backed) tracks the active project context and window counters across the session
- Firebase Auth with `BootstrapGate` caching auth state locally to minimize cold-start network hits
- ViewBinding throughout; no DataBinding

**Main packages and responsibilities:**

| Package | Responsibility |
|---|---|
| `registro` | Auth, device binding, PIN auth, user plans |
| `clientes` | Client CRUD with Room + Firestore sync, voice search |
| `productos` | Product catalog, pricing, stock, sync |
| `taller` | Measurement & design entry (windows, doors, glass, shower) |
| `taller/nova` | Nova corrediza window builder — `NovaCorrediza.kt` + `NovaUIHelper.kt` |
| `Diseno/nova` | Design view for Nova — `DisenoNovaActivity.kt` + `VistaDiseno.kt` |
| `Diseno/vitro` | Glass panel design |
| `casilla` | Project (casilla) lifecycle management and metadata storage |
| `medicion` | Measurement state machine with rules engine |
| `optimizadores` | Glass cutting optimization algorithms |
| `comprobantes` | Receipt/ticket design for POS |
| `pos` | Point of sale, budget manager, measurement importer |
| `red` | Chat/messaging — **currently being refactored** (see `docs/CHAT_CRYSTAL_PUNTOS_PLAN.md`) |
| `baul` | Archive/vault for saved measurements |
| `ocr` | ML Kit text recognition for voucher scanning |
| `pdf` | PDF export via iText7 |
| `wallet` | Payment balance tracking |

## Key Dependencies

- **Firebase**: Auth, Firestore, Storage, Functions, Analytics
- **Room 2.6.1** + KSP for local DB
- **WorkManager 2.9.0** for background sync
- **Coroutines** for all async work
- **Glide 4.16.0** for image loading
- **iText7 7.2.5** for PDF generation
- **ML Kit** text recognition (OCR)
- **ExoPlayer 2.19.1** for media
- **Lottie 6.4.0** for animations
- **ZXing** for barcode scanning
- **AndroidSVG** for SVG rendering
- **Material Design 1.12.0**

## Active Refactoring

The `red` package (chat) is undergoing a multi-phase architectural refactor documented in `docs/CHAT_CRYSTAL_PUNTOS_PLAN.md`. This creates a shared interoperability contract between Crystal and the Puntos companion app. New chat-related code should follow the patterns in `ChatIdentity.kt` and `ChatMigrationManager.kt` (both new files on `refactor-nova` branch). Key chat message types: `text`, `crystal_budget`, `materials_request`, `puntos_quote`, `file`.

The `Diseno` module (symbolic design) is also slated for refactoring — last commit before that work is tagged `932353b`.

## Sync Architecture

Entities (`Cliente`, `Producto`) use UUID-based identifiers for cross-device sync. Sync fields follow the pattern:
- `pendienteSincronizar: Boolean` — dirty flag set on local write
- `ultimaSincronizacion: Long` — timestamp of last successful sync

Workers read all `pendienteSincronizar = true` records, push to Firestore, then clear the flag. Real-time listeners are used for incoming changes.
