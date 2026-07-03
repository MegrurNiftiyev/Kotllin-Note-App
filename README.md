<h1 align="center">Note App</h1>

<p align="center">
  A native Android notes and todo companion app, built with Kotlin and Jetpack Compose, backed by a Node.js/MongoDB REST API.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose"/>
  <img src="https://img.shields.io/badge/Hilt-34A853?style=for-the-badge&logo=dagger&logoColor=white" alt="Hilt"/>
  <img src="https://img.shields.io/badge/Room-4285F4?style=for-the-badge&logo=sqlite&logoColor=white" alt="Room"/>
  <img src="https://img.shields.io/badge/Retrofit-48B983?style=for-the-badge&logo=square&logoColor=white" alt="Retrofit"/>
</p>

## Overview

Note App is a native Android client for creating, organizing, and syncing notes and todos. It follows a layered `core / data / domain / ui` architecture with a repository pattern, offline-first local storage via Room, and a remote REST API layer that talks to the [note-app-backend](https://github.com/MegrurNiftiyev/note-app-backend) service.

Authentication uses short-lived JWT access tokens with automatic refresh, and sensitive tokens are kept in encrypted local storage rather than plain SharedPreferences.

## Core Features

- Notes and todos with local-first storage and remote sync
- JWT authentication with automatic token refresh via an OkHttp `Authenticator`
- Encrypted local storage for sensitive data (tokens, user info)
- Multi-language support via locale extensions
- Light and dark theme support
- MVVM presentation layer with unidirectional state (`State` + `ViewModel` per screen)
- Dependency injection with Hilt across network, database, and repository layers

## Tech Stack

| Layer | Tools |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM, Clean Architecture (`core` / `data` / `domain` / `ui`) |
| Dependency Injection | Hilt |
| Local Storage | Room, EncryptedSharedPreferences |
| Networking | Retrofit / OkHttp (interceptors + authenticator) |
| Navigation | Jetpack Navigation (Compose) |
| Backend | Node.js, Express, MongoDB ([note-app-backend](https://github.com/MegrurNiftiyev/note-app-backend)) |

## Project Structure

```text
com/example/note_app_kotllin/
├── MainActivity.kt
├── NoteApplication.kt
├── core/
│   ├── constants/
│   │   ├── ApiUrls.kt
│   │   ├── AppDurations.kt
│   │   ├── BorderRadiuses.kt
│   │   ├── CacheKeys.kt
│   │   ├── Paddings.kt
│   │   └── Spaces.kt
│   ├── di/
│   │   ├── DatabaseModule.kt
│   │   ├── NetworkModule.kt
│   │   └── RepositoryModule.kt
│   ├── enums/
│   │   └── LanguageCodes.kt
│   ├── exceptions/
│   │   ├── AuthException.kt
│   │   ├── NetworkException.kt
│   │   ├── NoteException.kt
│   │   ├── TodoException.kt
│   │   └── UserException.kt
│   ├── extensions/
│   │   ├── LocaleExtensions.kt
│   │   └── StringExtensions.kt
│   ├── interceptors/
│   │   ├── AuthInterceptor.kt
│   │   └── TokenAuthenticator.kt
│   ├── managers/
│   │   ├── CacheManager.kt
│   │   └── EncryptedCacheManager.kt
│   ├── navigation/
│   │   ├── AppDestinations.kt
│   │   └── NavGraph.kt
│   ├── network/
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── util/
│       └── UiText.kt
├── data/
│   ├── datasoruces/
│   │   ├── local/
│   │   │   ├── NoteLocalDataSource.kt
│   │   │   ├── SettingsLocalDataSource.kt
│   │   │   ├── TodoLocalDataSource.kt
│   │   │   ├── UserLocalDataSource.kt
│   │   │   └── room/
│   │   │       ├── AppDatabase.kt
│   │   │       ├── daos/
│   │   │       │   ├── NoteDao.kt
│   │   │       │   ├── TodoDao.kt
│   │   │       │   └── UserDao.kt
│   │   │       └── entities/
│   │   │           ├── NoteEntity.kt
│   │   │           ├── TodoEntity.kt
│   │   │           └── UserEntity.kt
│   │   └── remote/
│   │       ├── datasources/
│   │       │   ├── AuthRemoteDataSource.kt
│   │       │   ├── NoteRemoteDataSource.kt
│   │       │   ├── TodoRemoteDataSource.kt
│   │       │   └── UserRemoteDataSource.kt
│   │       └── services/
│   │           ├── AuthApiService.kt
│   │           ├── NoteApiService.kt
│   │           ├── TodoApiService.kt
│   │           └── UserApiService.kt
│   ├── models/
│   │   ├── dto/
│   │   │   ├── AuthDto.kt
│   │   │   ├── NoteDto.kt
│   │   │   ├── TodoDto.kt
│   │   │   └── UserDto.kt
│   │   ├── request/
│   │   │   ├── LoginRequest.kt
│   │   │   ├── LogoutRequest.kt
│   │   │   ├── NoteRequest.kt
│   │   │   ├── RefreshRequest.kt
│   │   │   ├── RegisterRequest.kt
│   │   │   ├── TodoRequest.kt
│   │   │   └── UpdateUserRequest.kt
│   │   └── response/
│   │       ├── DeleteUserResponse.kt
│   │       ├── LoginResponse.kt
│   │       ├── NoteListResponse.kt
│   │       ├── NoteResponse.kt
│   │       ├── RefreshResponse.kt
│   │       ├── RegisterResponse.kt
│   │       ├── TodoListResponse.kt
│   │       ├── TodoResponse.kt
│   │       └── UserResponse.kt
│   └── repostories/
│       ├── AuthRepository.kt
│       ├── NotesRepository.kt
│       ├── SettingsRepository.kt
│       ├── TodoRepository.kt
│       └── UserRepository.kt
├── domain/
│   ├── models/
│   │   ├── Note.kt
│   │   ├── Todo.kt
│   │   └── User.kt
│   ├── repositories/
│   │   ├── IAuthRepository.kt
│   │   ├── INotesRepository.kt
│   │   ├── ISettingsRepository.kt
│   │   ├── ITodoRepository.kt
│   │   └── IUserRepository.kt
│   └── usecases/
└── ui/
    ├── components/
    │   ├── EmptyState.kt
    │   ├── alertdialogs/
    │   └── bottomsheets/
    └── screens/
        ├── auth/
        │   ├── components/
        │   │   ├── CustomTextField.kt
        │   │   └── RichText.kt
        │   ├── login/
        │   │   ├── LoginScreen.kt
        │   │   ├── LoginState.kt
        │   │   └── LoginViewModel.kt
        │   └── register/
        │       ├── RegisterScreen.kt
        │       ├── RegisterState.kt
        │       └── RegisterViewModel.kt
        ├── home/
        │   └── HomeScreen.kt
        ├── notedetail/
        │   ├── NoteDetailScreen.kt
        │   ├── NoteDetailState.kt
        │   └── NoteDetailViewModel.kt
        ├── notes/
        │   ├── NotesScreen.kt
        │   ├── NotesState.kt
        │   ├── NotesViewModel.kt
        │   └── components/
        │       └── NoteCard.kt
        ├── settings/
        │   ├── SettingsScreen.kt
        │   ├── SettingsState.kt
        │   ├── SettingsViewModel.kt
        │   └── components/
        │       ├── LanguageBottomSheet.kt
        │       ├── SettingsTile.kt
        │       └── UserImage.kt
        ├── splash/
        │   ├── SplashScreen.kt
        │   ├── SplashState.kt
        │   └── SplashViewModel.kt
        └── todo/
            ├── TodoScreen.kt
            ├── TodoState.kt
            ├── TodoViewModel.kt
            └── components/
                └── TodoCard.kt
```

## Architecture

The app follows Clean Architecture principles across three layers:

- **`core`** — cross-cutting concerns: DI modules, constants, exceptions, interceptors, cache managers, navigation, and theming.
- **`data`** — local (Room) and remote (Retrofit) data sources, DTOs, request/response models, and repository implementations.
- **`domain`** — plain Kotlin models and repository interfaces, independent of any framework.
- **`ui`** — Jetpack Compose screens and components, organized by feature, each with its own `Screen` / `State` / `ViewModel`.

Repositories implement the `domain` interfaces and decide whether to serve data from the local Room cache or fetch from the remote API, enabling offline-first behavior.

## Networking & Auth

- `AuthInterceptor` attaches the current access token to outgoing requests.
- `TokenAuthenticator` transparently refreshes the access token on a `401` and retries the original request.
- Refresh tokens are persisted through `EncryptedCacheManager`, never in plain `SharedPreferences`.
- API base URLs are centralized in `core/constants/ApiUrls.kt`.

This client is designed to work against the [note-app-backend](https://github.com/MegrurNiftiyev/note-app-backend) API, which issues JWT access tokens and delivers refresh tokens via the mobile JSON flow (`X-Client-Type: mobile`).

## Setup

1. Clone the repository:
```bash
git clone https://github.com/MegrurNiftiyev/kotllin-note-app
```

2. Open the project in Android Studio.

3. Point the app to your backend instance in `core/constants/ApiUrls.kt`, or configure it via a local `local.properties` / `.env`-style entry if your build is set up for that.

4. Sync Gradle and run on an emulator or device.

## Backend

This app is the client counterpart to the [note-app-backend](https://github.com/MegrurNiftiyev/note-app-backend) REST API (Node.js, Express, MongoDB). See that repository for endpoint documentation, auth token delivery details, and setup instructions.

## License

Internal project.