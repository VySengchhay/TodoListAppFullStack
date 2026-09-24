# To-Do List App – Backend

REST API for the To-Do List app, built with Spring Boot and PostgreSQL. It handles creating, reading, updating, and deleting tasks for the Android client.

## Tech Stack

- **Language:** Kotlin
- **Framework:** Spring Boot
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA (Hibernate)
- **Build Tool:** Gradle (Kotlin DSL)

## Architecture

    Controller → Service → Repository → PostgreSQL

- **Controller:** Handles HTTP requests and responses
- **Service:** Business logic
- **Repository:** Database access with Spring Data JPA

## Project Structure

    src/main/kotlin/com/[yourname]/todolist/
    ├── controller/    # REST endpoints
    ├── service/       # Business logic
    ├── repository/    # JPA repositories
    ├── entity/        # Database entities
    ├── dto/           # Request and response models
    └── exception/     # Global error handling

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/tasks` | Get all tasks |
| GET | `/api/tasks/{id}` | Get a task by ID |
| POST | `/api/tasks` | Create a new task |
| PUT | `/api/tasks/{id}` | Update a task |
| PATCH | `/api/tasks/{id}/toggle` | Mark a task as completed or active |
| DELETE | `/api/tasks/{id}` | Delete a task |

### Example Request

    POST /api/tasks
    Content-Type: application/json

    {
      "title": "Finish homework",
      "description": "Math exercises chapter 3",
      "dueDate": "2026-10-01",
      "priority": "HIGH"
    }

### Example Response

    {
      "id": 1,
      "title": "Finish homework",
      "description": "Math exercises chapter 3",
      "dueDate": "2026-10-01",
      "priority": "HIGH",
      "completed": false,
      "createdAt": "2026-09-24T10:30:00"
    }

## Getting Started

### Requirements

- JDK 17 or newer
- PostgreSQL 14 or newer

### Setup

1. Create the database:
```sql
   CREATE DATABASE todolist_db;
```
2. Copy the example config and add your database password:
```bash
   cp src/main/resources/application-local.yml.example src/main/resources/application-local.yml
```
3. Run the server:
```bash
   ./gradlew bootRun
```
4. The API runs at `http://localhost:8080`.

> From the Android emulator, use `http://10.0.2.2:8080` to reach this server.

## Android Client

The mobile app for this API is in [`../TodoListFrontEnd`](../TodoListFrontEnd/README.md).

## Future Improvements

- User authentication with JWT
- Pagination and search for tasks
- API documentation with Swagger

## Author

**Vy Sengchhay**
- GitHub: [@vysengchhay](https://github.com/vysengchhay)