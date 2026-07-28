# ProFlow 

Fullstack project management API with Kanban-style task tracking and real-time PostgreSQL vs MongoDB performance comparison.

## Features

- User registration and login with JWT authentication
- Create and manage projects with team members
- Kanban task board (TODO / IN_PROGRESS / DONE)
- Every task operation runs simultaneously on PostgreSQL and MongoDB using parallel threads
- Performance metrics endpoint comparing both databases in real time
- Dockerized PostgreSQL and MongoDB

## Tech Stack

- Java 21
- Spring Boot 4
- Spring Security + JWT
- Spring Data JPA → PostgreSQL 16
- Spring Data MongoDB → MongoDB 7
- Docker & Docker Compose
- Lombok
- Maven

## Getting Started

### Prerequisites

- Java 21+
- Maven
- Docker

### Run the databases

```bash
docker compose up -d
```

### Run the application

```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Duser.timezone=UTC"
```

Or run from IntelliJ with VM option `-Duser.timezone=UTC`.

## API Endpoints

### Auth (public)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and get JWT token |

### Projects (requires login)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/projects` | Create a project |
| GET | `/api/projects` | List my projects |
| GET | `/api/projects/{id}` | Get project by ID |
| POST | `/api/projects/{id}/members/{userId}` | Add member to project |

### Tasks (requires login)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/projects/{id}/tasks` | Create a task |
| GET | `/api/projects/{id}/tasks` | List tasks by project |
| PUT | `/api/projects/{id}/tasks/{taskId}/status` | Update task status |
| DELETE | `/api/projects/{id}/tasks/{taskId}` | Delete a task |

### Metrics (requires login)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/metrics` | All performance logs |
| GET | `/api/metrics/{operation}` | Logs by operation |

## How the Performance Comparison Works

Each write and read operation is executed on both databases simultaneously using `CompletableFuture`:

```
PostgreSQL ──┐
             ├── parallel execution ──► PerformanceLog saved to MongoDB
MongoDB    ──┘
```

Results are stored and exposed via `/api/metrics`, showing real response times for each database per operation.

## Project Structure

```
src/main/java/com/proflow/proflow/
├── config/             # JWT, Security, JPA and Mongo config
├── controller/         # REST controllers
├── dto/                # Request and response objects
├── exception/          # Global error handling
├── model/
│   ├── postgres/       # JPA entities
│   └── mongo/          # MongoDB documents
├── repository/
│   ├── postgres/       # JPA repositories
│   └── mongo/          # MongoDB repositories
└── service/            # Business logic
```