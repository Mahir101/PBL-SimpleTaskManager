# Task Manager - Enterprise-Grade Task Management System

A robust and scalable task management backend application built with Spring Boot 3.5.3 and Java 21, featuring virtual threading, Redis caching, real-time notifications, and comprehensive analytics.

## Features

### Core Features
- **Task Management**: Create, update, delete, and clone tasks with rich metadata
- **Project Organization**: Organize tasks into projects with team collaboration
- **User Management**: Role-based access control (Admin, Manager, User)
- **Authentication**: JWT-based authentication with refresh tokens
- **Search & Filtering**: Advanced search with multiple filter criteria
- **Task Dependencies**: Define and manage task dependencies
- **Time Tracking**: Track estimated vs actual time spent on tasks

### Advanced Features
- **Virtual Threading (Java 21)**: Leverage Project Loom for high-performance async operations
- **Redis Caching**: High-speed data caching for improved performance
- **Real-time Notifications**: Email and in-app notifications for task updates
- **Scheduled Reminders**: Automatic notifications for due dates and overdue tasks
- **Analytics & Reporting**: Comprehensive dashboards with task metrics
- **RESTful API**: Well-documented API with Swagger/OpenAPI

## Technology Stack

- **Backend**: Java 21, Spring Boot 3.5.3
- **Database**: PostgreSQL (production), H2 (development)
- **Caching**: Redis
- **Security**: Spring Security, JWT
- **Documentation**: Swagger/OpenAPI 3.0
- **Build Tool**: Gradle

## Prerequisites

- Java 21 or higher
- Gradle 8.x
- PostgreSQL (for production)
- Redis (optional, for caching)

## Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
cd PBL-SimpleTaskManager
```

### 2. Configuration

#### Development Profile (H2 Database)
The application is configured to use H2 in-memory database by default in development mode.

```properties
# src/main/resources/application-dev.properties
spring.datasource.url=jdbc:h2:mem:taskdb
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

#### Production Profile (PostgreSQL)
Update `src/main/resources/application-prod.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanager
spring.datasource.username=your_username
spring.datasource.password=your_password
```

#### JWT Secret Configuration
**Important**: Change the JWT secret in production!

```properties
jwt.secret=YOUR_STRONG_SECRET_KEY_HERE
jwt.expiration=86400000
jwt.refresh-expiration=604800000
```

### 3. Run the Application

#### Development Mode
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

#### Production Mode
```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

### 4. Access the Application

- **API Base URL**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs
- **H2 Console** (dev only): http://localhost:8080/h2-console
- **Actuator Health**: http://localhost:8080/actuator/health

## Sample Users (Development Mode)

The application seeds sample data in development mode:

| Username | Password    | Role    |
|----------|-------------|---------|
| admin    | admin123    | ADMIN   |
| manager  | manager123  | MANAGER |
| john     | password123 | USER    |
| jane     | password123 | USER    |

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token
- `POST /api/auth/refresh` - Refresh access token

### Tasks
- `GET /api/tasks` - Get all tasks
- `POST /api/tasks` - Create new task
- `GET /api/tasks/{id}` - Get task by ID
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task (Admin/Manager only)
- `POST /api/tasks/{id}/clone` - Clone task
- `GET /api/tasks/search` - Search tasks with filters
- `GET /api/tasks/user/{userId}` - Get user's tasks (async)
- `GET /api/tasks/overdue` - Get overdue tasks (async)
- `POST /api/tasks/{taskId}/dependencies/{dependsOnId}` - Add dependency

### Projects
- `GET /api/projects` - Get all projects
- `POST /api/projects` - Create project
- `GET /api/projects/{id}` - Get project by ID
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project (Admin/Manager only)
- `GET /api/projects/my-projects` - Get current user's projects
- `POST /api/projects/{projectId}/members/{userId}` - Add member
- `DELETE /api/projects/{projectId}/members/{userId}` - Remove member

### Notifications
- `GET /api/notifications` - Get all notifications
- `GET /api/notifications/unread` - Get unread notifications
- `PUT /api/notifications/{id}/read` - Mark as read
- `PUT /api/notifications/read-all` - Mark all as read

### Analytics
- `GET /api/analytics/overall` - Overall analytics (Admin/Manager only)
- `GET /api/analytics/my-analytics` - Current user analytics
- `GET /api/analytics/user/{userId}` - User analytics (Admin/Manager only)
- `GET /api/analytics/project/{projectId}` - Project analytics

## Authentication

All endpoints (except `/api/auth/**`) require authentication. Include the JWT token in the Authorization header:

```
Authorization: Bearer <your_jwt_token>
```

### Example Login Request
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

## Virtual Threading

The application leverages Java 21's virtual threads for improved performance:

- **Task Operations**: Async task queries with virtual thread executor
- **Notifications**: Email sending on virtual threads
- **Scheduled Jobs**: Due date reminders using virtual threads

## Redis Configuration

Redis is used for caching frequently accessed data:

- Task cache (10-minute TTL)
- Analytics cache
- Session management

To disable Redis, comment out the Redis dependencies in `build.gradle`.

## Email Notifications

Configure SMTP settings in `application-prod.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

## Scheduled Tasks

- **Due Date Reminders**: Runs daily at 9 AM
- **Overdue Notifications**: Checks and notifies about overdue tasks

## Security Features

- JWT-based authentication
- Password encryption with BCrypt
- Role-based access control (RBAC)
- CORS configuration for frontend integration
- Request validation
- Global exception handling

## Development

### Build the Project
```bash
./gradlew build
```

### Run Tests
```bash
./gradlew test
```

### Generate JAR
```bash
./gradlew bootJar
```

## Production Deployment

1. Update `application-prod.properties` with production values
2. Set environment variables for sensitive data:
   ```bash
   export SPRING_DATASOURCE_URL=jdbc:postgresql://...
   export SPRING_DATASOURCE_USERNAME=...
   export SPRING_DATASOURCE_PASSWORD=...
   export JWT_SECRET=...
   ```
3. Build the JAR:
   ```bash
   ./gradlew bootJar
   ```
4. Run the application:
   ```bash
   java -jar build/libs/task-manager-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
   ```

## Architecture Highlights

### Virtual Threading
The application uses Java 21's virtual threads via Spring Boot 3.5.3 for scalable async operations:
```java
@Async("taskExecutor")
public CompletableFuture<List<TaskDTO>> getTasksByUserAsync(Long userId) {
    // Executes on virtual threads
}
```

### Redis Caching
Strategic caching for performance optimization:
```java
@Cacheable(value = "tasks", key = "#id")
public TaskDTO getTaskById(Long id) { ... }
```

### Real-time Notifications
Async notification delivery using virtual threads:
```java
@Async("notificationExecutor")
public void sendTaskAssignedNotificationAsync(Task task) { ... }
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

MIT License

## Support

For issues and questions, please create an issue in the repository.
