# Repository Guidelines

## Project Structure & Module Organization
This repository is a single-module Android app named `cleep-android`. App code lives in `app/src/main/kotlin/dev/cleep/app`, split by responsibility:
- `app/` for app shell, navigation, DI-style wiring, and API bootstrap
- `feature/*` for user-facing slices such as `auth`, `cleeps`, `projects`, and `settings`
- `core/*` for shared UI and design system code

Resources live under `app/src/main/res`, including localized strings in `values/` and `values-es/`, plus bundled fonts in `res/font/`. There are currently no checked-in `test/` or `androidTest/` source sets; add them under `app/src/test` and `app/src/androidTest` when introducing coverage.

## Build, Test, and Development Commands
Use the Gradle wrapper from the repo root:
- `./gradlew app:assembleDebug` builds the debug APK
- `./gradlew app:installDebug` installs the debug build on a connected device/emulator
- `./gradlew app:testDebugUnitTest` runs JVM unit tests
- `./gradlew app:connectedDebugAndroidTest` runs instrumentation and Compose UI tests
- `./gradlew app:lint` checks Android lint rules
- `./gradlew clean` deletes build outputs

The app reads `cleep.baseUrl` / `BASE_URL` and `cleep.googleServerClientId` / `GOOGLE_SERVER_CLIENT_ID`. Default backend URL is `http://localhost:3000`.

## Coding Style & Naming Conventions
Follow Kotlin official style (`kotlin.code.style=official`) with 4-space indentation. Keep package names lowercase, types and composables in `PascalCase`, functions and properties in `camelCase`, and screen/view-model files descriptive, for example `LoginScreen.kt` and `CleepsViewModel.kt`. Match the existing feature-first package layout and preserve the design language documented in `DESIGN.md`.

## Testing Guidelines
Prefer JUnit 4 for unit tests and AndroidX/Compose test APIs for device tests. Name unit tests `*Test.kt` and instrumentation tests `*AndroidTest.kt`. Add fast repository/view-model coverage first, then UI navigation or screen behavior where regressions are likely.

## Commit & Pull Request Guidelines
Recent history uses Conventional Commit prefixes such as `feat:`, `fix:`, `style:`, `refactor:`, and `chore:`. Keep subjects short and imperative, for example `feat: add project support to cleeps`. PRs should include a concise summary, test notes listing the Gradle tasks you ran, linked issues when applicable, and screenshots or recordings for visible UI changes.
