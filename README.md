# Mutual Funds App

An Android app to discover mutual funds, inspect fund performance history, and organize funds into custom watchlist folders.

This project was built for the Mobile SDE Intern assignment and focuses on clean architecture, practical state handling, and a smooth user flow from discovery to tracking.

## What the app supports

- Explore screen with assignment categories: Index, Bluechip, Tax Saver (ELSS), and Large Cap.
- Up to 4 quick cards per category with a `View All` action.
- `View All` screen with lazy list rendering and infinite scroll pagination behavior.
- Dedicated search screen with 300ms debounce to avoid excessive API calls.
- Fund detail screen with:
  - Scheme details (name, type/category, NAV)
  - NAV history line chart
  - Time range filter (`1M`, `3M`, `6M`, `1Y`, `ALL`)
- Watchlist flow from product screen:
  - Bookmark icon reflects whether fund is in any watchlist
  - Bottom sheet to add/remove from multiple folders
  - Create new watchlist directly from bottom sheet
- Watchlist tab with folder list and folder-level fund counts.
- Empty, loading, and error UI states across key screens.
- Theme toggle (Light/Dark mode).
- Local persistence:
  - Room for watchlists and fund mapping
  - DataStore-backed local cache for Explore data

## Tech stack

- Kotlin
- Jetpack Compose + Material 3
- MVVM + UseCase + Repository layers
- Hilt (dependency injection)
- Retrofit + OkHttp + Gson
- Room (local database)
- DataStore (local cached responses)
- Kotlin Coroutines + Flow
- Vico (chart rendering)
- JUnit4 + MockK + Turbine (unit tests)

## API used

- Base URL: `https://api.mfapi.in/`
- Search endpoint: `https://api.mfapi.in/mf/search?q={query}`
- Fund detail endpoint: `https://api.mfapi.in/mf/{scheme_code}`

Note: Since MFAPI does not provide direct category APIs, category sections are populated using keyword-based search queries as suggested in the assignment brief.

## Project structure

- `app/src/main/java/com/example/mutualfundsapp/presentation` - Compose screens, contracts, view models, UI components
- `app/src/main/java/com/example/mutualfundsapp/domain` - models, repository interfaces, use cases
- `app/src/main/java/com/example/mutualfundsapp/data` - API services, DTOs, repositories, Room, cache
- `app/src/main/java/com/example/mutualfundsapp/di` - Hilt modules

## How to run

### Prerequisites

- Android Studio (latest stable preferred)
- JDK 17+
- Android SDK + emulator (or physical device)

### Steps

1. Clone the repository
2. Open it in Android Studio
3. Let Gradle sync complete
4. Run on emulator/device

Build debug APK from terminal:

```bash
./gradlew clean assembleDebug
```

Run unit tests:

```bash
./gradlew clean testDebugUnitTest
```

## Screenshots

<p align="center">
  <img src="https://raw.githubusercontent.com/iampranshu2003/MutualFundsApp/main/exploreScreen.jpg" alt="Explore Screen Dark" width="220" />
  <img src="https://raw.githubusercontent.com/iampranshu2003/MutualFundsApp/main/LighTheme.jpg" alt="Explore Screen Light" width="220" />
  <img src="https://raw.githubusercontent.com/iampranshu2003/MutualFundsApp/main/FundDetailScreen.jpg" alt="Fund Detail Screen" width="220" />
</p>

<p align="center">
  <img src="https://raw.githubusercontent.com/iampranshu2003/MutualFundsApp/main/addWatchList.jpg" alt="Add to Watchlist Bottom Sheet" width="220" />
  <img src="https://raw.githubusercontent.com/iampranshu2003/MutualFundsApp/main/bookmarkScreen.jpg" alt="Watchlist Screen" width="220" />
  <img src="https://raw.githubusercontent.com/iampranshu2003/MutualFundsApp/main/searchScreen.jpg" alt="Search Screen" width="220" />
</p>

## Demo and submission links

- Screen recording (Drive): `Add your public Drive link here`
- APK (Drive): `Add your public Drive link here`
- GitHub repository: [MutualFundsApp](https://github.com/iampranshu2003/MutualFundsApp)

## Interview-ready notes

- Architecture and module separation are intentionally kept clean for easier extension.
- Core assignment functionality is implemented first, with brownie-point features (debounce, theme support, tests) included.
- The codebase is organized so each screen has clear state, events, and UI contracts for maintainability.
