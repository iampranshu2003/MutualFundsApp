# MutualFundsApp

MutualFundsApp is an Android app (Kotlin + Jetpack Compose) for exploring mutual funds, viewing NAV history, and managing custom watchlists.

## Features

- Explore funds across categories (`Index`, `Bluechip`, `Tax Saver`, `Large Cap`) using MFAPI search keywords.
- View fund details with:
  - AMC name
  - Scheme type
  - latest NAV
  - NAV history line chart with date ranges
- Add/remove a fund to multiple watchlist folders via bottom sheet.
- Manage watchlist folders and folder-level fund lists.
- Search funds with 300ms debounce.
- Light/Dark theme toggle.
- Local persistence with Room (watchlists) and DataStore cache (Explore screen).

## Tech Stack

- **UI:** Jetpack Compose, Material 3
- **Architecture:** MVVM + UseCases + Repository pattern
- **DI:** Hilt
- **Networking:** Retrofit + OkHttp + Gson
- **Persistence:** Room + DataStore
- **Charts:** Vico
- **Async:** Kotlin Coroutines + Flow
- **Tests:** JUnit4, MockK, Turbine

## Project Structure

- `app/src/main/java/com/example/mutualfundsapp/presentation` - screens, viewmodels, UI contracts
- `app/src/main/java/com/example/mutualfundsapp/domain` - models, repository contracts, use cases
- `app/src/main/java/com/example/mutualfundsapp/data` - API DTOs, repositories, Room, cache
- `app/src/main/java/com/example/mutualfundsapp/di` - Hilt modules

## Setup & Run

### Requirements

- Android Studio (latest stable recommended)
- JDK 17+ installed
- Android SDK with a recent API level and emulator/device

### Commands

```bash
./gradlew clean assembleDebug
```

Run unit tests:

```bash
./gradlew clean testDebugUnitTest
```

## API

- Base URL: `https://api.mfapi.in/`
- Search: `https://api.mfapi.in/mf/search?q={query}`
- Fund details + NAV history: `https://api.mfapi.in/mf/{scheme_code}`

## Notes

- `compileSdk` is set to 36 while AGP is 8.5.2; build works but Gradle may show an advisory warning.
- Explore categories are populated from keyword-based search due to MFAPI category endpoint limitations.
