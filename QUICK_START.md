# Quick Start Guide

Get your task management system running in under 5 minutes!

## Prerequisites Check

Ensure you have:
- ✅ Java 21 installed: `java -version`
- ✅ Gradle 8.x (or use included wrapper)

## Step 1: Start the Application

```bash
# Navigate to project directory
cd PBL-SimpleTaskManager

# Run in development mode (uses H2 in-memory database)
./gradlew bootRun
```

Wait for the startup message:
```
Started SimpleTaskApplication in X.XXX seconds
```

## Step 2: Access the Application

### Swagger UI (Interactive API Documentation)
Open in browser: **http://localhost:8080/swagger-ui.html**

### H2 Database Console
Open in browser: **http://localhost:8080/h2-console**
- JDBC URL: `jdbc:h2:mem:taskdb`
- Username: `sa`
- Password: (leave empty)

## Step 3: Test Authentication

### Using Swagger UI

1. Navigate to **Authentication Controller**
2. Click on **POST /api/auth/login**
3. Click "Try it out"
4. Use these credentials:
   ```json
   {
     "username": "admin",
     "password": "admin123"
   }
   ```
5. Click "Execute"
6. Copy the `accessToken` from the response

### Authorize All Requests

1. Click the **"Authorize"** button at the top
2. Enter: `Bearer <your_access_token>`
3. Click "Authorize"
4. Now you can test all endpoints!

## Step 4: Try Key Features

### Create a Task
1. Go to **Tasks Controller** → **POST /api/tasks**
2. Click "Try it out"
3. Use this sample:
   ```json
   {
     "title": "My First Task",
     "description": "Testing the API",
     "status": "TODO",
     "priority": "HIGH",
     "dueDate": "2025-12-31T23:59:59"
   }
   ```
4. Click "Execute"

### Get All Tasks
1. Go to **GET /api/tasks**
2. Click "Try it out" → "Execute"
3. See your task in the list!

### Search Tasks (with Virtual Threading)
1. Go to **GET /api/tasks/user/{userId}**
2. Try userId: `1` (admin user)
3. Watch the logs - it runs on virtual threads!

### View Analytics
1. Go to **Analytics Controller** → **GET /api/analytics/my-analytics**
2. Click "Execute"
3. See your task statistics!

## Sample Users

| Username | Password    | Role    |
|----------|-------------|---------|
| admin    | admin123    | ADMIN   |
| manager  | manager123  | MANAGER |
| john     | password123 | USER    |
| jane     | password123 | USER    |

## Using cURL (Command Line)

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Save the token from response!

### Get Tasks
```bash
curl -X GET http://localhost:8080/api/tasks \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Create Task
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "New Task",
    "description": "Created via cURL",
    "status": "TODO",
    "priority": "MEDIUM"
  }'
```

## Testing Virtual Threading

Check your console logs when making async requests. You'll see:
```
Fetching tasks for user 1 on thread: VirtualThread[#42]/runnable@ForkJoinPool-1-worker-1
```

This confirms virtual threads are working!

## Testing Redis Cache

1. Make a GET request to `/api/tasks/{id}` - First request hits database
2. Make the same request again - Second request hits Redis cache (faster!)
3. Update the task - Cache is automatically evicted
4. Get again - Hits database, refreshes cache

## Common Commands

### Rebuild Project
```bash
./gradlew clean build
```

### Run Tests
```bash
./gradlew test
```

### Create JAR
```bash
./gradlew bootJar
# Output: build/libs/task-manager-0.0.1-SNAPSHOT.jar
```

### Run JAR
```bash
java -jar build/libs/task-manager-0.0.1-SNAPSHOT.jar
```

## Troubleshooting

### Port Already in Use
```bash
# Kill process on port 8080
lsof -ti:8080 | xargs kill -9
```

### Redis Connection Error
Redis is optional for development. If you see connection errors:
1. Install Redis: `brew install redis` (Mac) or download from redis.io
2. Start Redis: `redis-server`
3. Or disable Redis by commenting out in `build.gradle`

### JWT Token Expired
Login again to get a new token. Tokens expire after 24 hours.

## Next Steps

1. ✅ Explore all API endpoints in Swagger
2. ✅ Try different user roles (admin, manager, user)
3. ✅ Create projects and assign tasks
4. ✅ Test task dependencies
5. ✅ View analytics for different users
6. ✅ Check notifications endpoint
7. ✅ Experiment with search filters

## Production Setup

Ready for production? Check out:
- `README.md` - Comprehensive documentation
- `PROJECT_SUMMARY.md` - Technical details
- `application-prod.properties` - Production configuration

## Support

- Check logs in the console for error details
- View H2 database to see data structure
- Use Swagger UI for API reference
- Review source code for implementation details

Happy Task Managing! 🚀
