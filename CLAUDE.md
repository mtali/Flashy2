# CLAUDE.md

Guidance for working in this repository.

## What this is

Flashy is an Android flashlight app: a camera torch and a full-screen colored light, plus SOS
Morse and stroboscope modes. It is a single-module Jetpack Compose app written in Kotlin.

## Commands

```bash
./gradlew :app:assembleDebug     # build the debug APK
./gradlew testDebugUnitTest      # unit tests (MorseTimer, FlashEngine)
./gradlew spotlessApply          # auto-format Kotlin (ktlint via Spotless)
./gradlew spotlessCheck          # verify formatting
```

Always run `spotlessApply` then `spotlessCheck` before committing — formatting is enforced by CI
(`.github/`). Indentation is 2 spaces.

Toolchain: JDK 17, AGP 8.13.2, Gradle 8.13, Kotlin 2.2.10, `compileSdk`/`targetSdk` 36, `minSdk` 24.
SDK levels and the version live in `buildSrc/.../Configuration.kt`; dependencies in
`gradle/libs.versions.toml`.

## Architecture

MVVM, package-by-feature, under `com.mtali.flashy2`:

- `app/` — `App` (`@HiltAndroidApp`), `MainActivity` (`@AndroidEntryPoint`), and the root
  Navigation 3 graph (`navigation/FlashyNavDisplay`, `ui/FlashyApp`).
- `core/` — shared infrastructure: `torch/` (the `TorchController` abstraction over
  `CameraManager`), `morse/` (`MorseTimer`), `dispatchers/`, `datastore/` (settings persistence),
  `data/` (`SettingsRepository`), `base/` (`BaseViewModel`), `navigation/`, and `ui/`
  (`theme/` + custom `components/`).
- `domain/` — `FlashEngine`, the single source of truth for the torch.
- `features/` — one package per screen (`flashlight`, `settings`, `about`), each with a
  `Route` + stateless `Screen` + `ViewModel` (where needed) + a `navigation/` entry provider.

### Conventions

- DI is Hilt: `@Module @InstallIn(SingletonComponent::class)` with `@Binds` for
  interface→implementation and `@Provides` for factories. Inject dispatchers with the
  `@Dispatcher(...)` qualifier.
- ViewModels extend `BaseViewModel` and expose state built with
  `combine(...).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initial)`.
  Screens collect it with `collectAsStateWithLifecycle()`.
- Navigation 3: each feature declares a `@Serializable data object XNavKey : NavKey`, a
  `EntryProviderScope<NavKey>.xEntry(navigator)` extension, and is registered in `FlashyNavDisplay`.
  Navigate imperatively via `Navigator`.
- Settings are persisted in a Preferences DataStore via `PreferencesDataSource`, exposed as a
  `Flow<UserSettings>` through `SettingsRepository`.

### Key behavior

- `FlashEngine` runs each blinking mode (SOS, stroboscope) as a cancellable coroutine `Job`; the
  modes are mutually exclusive and the torch is turned off in the job's `finally` block. Prefer
  extending this over reintroducing threads or shared mutable flags.
- `MorseTimer` is pure and unit-tested; keep timing maths there, not in the engine or UI.
- The custom `CircularSlider` and `HsvColorPicker` replace third-party UI libraries — keep UI
  dependencies limited to Compose + Material 3.

## Git — branching & releases

Two long-lived branches: **`develop`** (integration — all development lands here) and **`main`**
(releases only). `main` is protected.

```
fix/* , feature/*  →  PR into develop   (squash-merge — collapses WIP into one tidy commit)
develop            →  PR into main      (MERGE COMMIT, never squash) + tag vX.Y.Z = a release
```

- **Every change** starts as a short-lived branch off `develop` (`fix/…`, `feature/…`), and is
  **squash-merged** back into `develop`.
- **Releases** merge `develop → main` with a **merge commit** (or fast-forward) — **never squash**.
  Squashing `develop → main` rewrites history into a new commit `develop` doesn't have, which makes
  `develop` diverge from `main` and forces a re-baseline. A merge commit keeps `develop` a permanent
  ancestor of `main` (`develop ⊆ main`), so they never diverge. Tag the release on `main`.
- After a release, no resync is needed; just branch the next change off `develop` as usual.

If `develop` ever does diverge from `main` (e.g. an accidental squash-merge of a release), re-baseline
it once: `git checkout develop && git reset --hard origin/main && git push --force-with-lease origin develop`.
