# To-Do List App

A simple and clean Android app for managing daily tasks. Users can add, edit, complete, and delete tasks, with all data saved locally on the device.

## Features

- Add, edit, and delete tasks
- Mark tasks as completed
- Set a due date and priority for each task
- Filter tasks by All / Active / Completed
- Swipe to delete with undo
- Works fully offline with local storage
- Light and dark theme support

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM + Clean Architecture
- **Dependency Injection:** Hilt
- **Async:** Coroutines + Flow
- **Local Database:** Room
- **Preferences:** DataStore (theme and filter settings)
- **Navigation:** Jetpack Navigation Compose

## Architecture

- **Presentation:** Compose screens observe `StateFlow` from ViewModels
- **Domain:** Use cases such as `AddTaskUseCase`, `GetTasksUseCase`, `ToggleTaskUseCase`
- **Data:** `TaskRepository` backed by a Room database

Data flow:

    UI (Compose) → ViewModel → Use Case → Repository → Room Database
                 ←  StateFlow ←          ←   Flow     ←

## Project Structure

    app/src/main/java/com/[yourname]/todolist/
    ├── core/              # Shared UI components, theme, utilities
    ├── di/                # Hilt modules
    ├── feature/
    │   └── task/
    │       ├── data/          # TaskEntity, TaskDao, TaskRepositoryImpl
    │       ├── domain/        # Task model, TaskRepository, use cases
    │       └── presentation/  # Screens, ViewModels, UI state
    └── navigation/        # Navigation graph

## Getting Started

### Requirements

- Android Studio [Ladybug or newer]
- JDK 17
- Min SDK 24

### Run the Project

1. Clone the repository:
```bash
   git clone https://github.com/vysengchhay/todolist-app.git
```
2. Open the project in Android Studio.
3. Sync Gradle and run on an emulator or device.

No API keys or extra setup needed.

## What I Learned

- Building reactive UI with Jetpack Compose and `StateFlow`
- Structuring an app with Clean Architecture
- Using Room with Flow for automatic UI updates
- Setting up dependency injection with Hilt

## Future Improvements

- Task reminders with notifications
- Cloud sync with a backend
- Task categories and search

## Author

**Vy Sengchhay**
- GitHub: [@vysengchhay](https://github.com/vysengchhay)