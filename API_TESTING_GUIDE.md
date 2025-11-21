# API Testing Guide

Complete guide for testing all API endpoints with example requests and responses.

## Setup

1. Start the application: `./gradlew bootRun`
2. Base URL: `http://localhost:8080`
3. Get a JWT token first (see Authentication section)

## Authentication Flow

### 1. Register a New User

```bash
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "firstName": "Test",
  "lastName": "User"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "userId": 5,
  "username": "testuser",
  "email": "test@example.com",
  "role": "USER"
}
```

### 2. Login

```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "username": "admin",
  "email": "admin@taskmanager.com",
  "role": "ADMIN"
}
```

### 3. Refresh Token

```bash
POST /api/auth/refresh
Content-Type: text/plain

eyJhbGciOiJIUzI1NiJ9...your-refresh-token
```

**Note:** For all subsequent requests, include the header:
```
Authorization: Bearer <your_access_token>
```

---

## Task Management

### 1. Get All Tasks

```bash
GET /api/tasks
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Design Homepage Mockup",
    "description": "Create high-fidelity mockup for the new homepage",
    "status": "IN_PROGRESS",
    "priority": "HIGH",
    "dueDate": "2025-12-15T00:00:00",
    "startDate": null,
    "completedAt": null,
    "estimatedHours": 8.0,
    "actualHours": null,
    "assignedToId": 3,
    "assignedToUsername": "john",
    "createdById": 2,
    "createdByUsername": "manager",
    "projectId": 1,
    "projectName": "Website Redesign",
    "tags": ["design", "frontend", "urgent"],
    "dependencyIds": [],
    "createdAt": "2025-11-22T10:00:00",
    "updatedAt": "2025-11-22T10:00:00"
  }
]
```

### 2. Get Task by ID

```bash
GET /api/tasks/1
Authorization: Bearer <token>
```

### 3. Create a New Task

```bash
POST /api/tasks
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Implement User Authentication",
  "description": "Add JWT-based authentication to the API",
  "status": "TODO",
  "priority": "URGENT",
  "dueDate": "2025-12-20T23:59:59",
  "estimatedHours": 16.0,
  "assignedToId": 3,
  "projectId": 1,
  "tags": ["backend", "security", "api"]
}
```

**Response (201 CREATED):**
```json
{
  "id": 6,
  "title": "Implement User Authentication",
  "status": "TODO",
  "priority": "URGENT",
  ...
}
```

### 4. Update a Task

```bash
PUT /api/tasks/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Design Homepage Mockup (Updated)",
  "description": "Create high-fidelity mockup - revised requirements",
  "status": "IN_PROGRESS",
  "priority": "URGENT",
  "dueDate": "2025-12-18T23:59:59"
}
```

### 5. Clone a Task

```bash
POST /api/tasks/1/clone
Authorization: Bearer <token>
```

**Response (201 CREATED):**
```json
{
  "id": 7,
  "title": "Design Homepage Mockup (Copy)",
  "status": "TODO",
  ...
}
```

### 6. Delete a Task (Admin/Manager Only)

```bash
DELETE /api/tasks/1
Authorization: Bearer <token>
```

**Response (204 NO CONTENT)**

### 7. Search Tasks with Filters

```bash
GET /api/tasks/search?keyword=design&status=IN_PROGRESS&priority=HIGH&projectId=1
Authorization: Bearer <token>
```

**Query Parameters:**
- `keyword` - Search in title and description
- `status` - TODO, IN_PROGRESS, IN_REVIEW, DONE, BLOCKED, CANCELLED
- `priority` - LOW, MEDIUM, HIGH, URGENT
- `projectId` - Filter by project

### 8. Get Tasks by User (Async - Virtual Threads)

```bash
GET /api/tasks/user/3
Authorization: Bearer <token>
```

**Note:** Check server logs to see virtual thread execution!

### 9. Get Overdue Tasks (Async - Virtual Threads)

```bash
GET /api/tasks/overdue
Authorization: Bearer <token>
```

### 10. Add Task Dependency

```bash
POST /api/tasks/5/dependencies/2
Authorization: Bearer <token>
```

**Meaning:** Task 5 depends on Task 2 (Task 2 must be completed first)

---

## Project Management

### 1. Get All Projects

```bash
GET /api/projects
Authorization: Bearer <token>
```

### 2. Get My Projects

```bash
GET /api/projects/my-projects
Authorization: Bearer <token>
```

### 3. Create a Project

```bash
POST /api/projects
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "E-Commerce Platform",
  "description": "Build a complete e-commerce solution",
  "status": "ACTIVE",
  "startDate": "2025-11-22T00:00:00",
  "endDate": "2026-03-31T23:59:59",
  "memberIds": [3, 4],
  "colorCode": "#2ecc71"
}
```

### 4. Update a Project

```bash
PUT /api/projects/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Website Redesign v2.0",
  "description": "Updated project scope",
  "status": "ACTIVE",
  "memberIds": [3, 4, 5]
}
```

### 5. Add Member to Project

```bash
POST /api/projects/1/members/5
Authorization: Bearer <token>
```

### 6. Remove Member from Project

```bash
DELETE /api/projects/1/members/5
Authorization: Bearer <token>
```

### 7. Delete Project (Admin/Manager Only)

```bash
DELETE /api/projects/1
Authorization: Bearer <token>
```

---

## Notifications

### 1. Get All My Notifications

```bash
GET /api/notifications
Authorization: Bearer <token>
```

**Response:**
```json
[
  {
    "id": 1,
    "user": {
      "id": 3,
      "username": "john"
    },
    "title": "New Task Assigned",
    "message": "You have been assigned to task: Design Homepage Mockup",
    "type": "TASK_ASSIGNED",
    "isRead": false,
    "relatedTaskId": 1,
    "relatedProjectId": null,
    "createdAt": "2025-11-22T10:05:00"
  }
]
```

### 2. Get Unread Notifications

```bash
GET /api/notifications/unread
Authorization: Bearer <token>
```

### 3. Mark Notification as Read

```bash
PUT /api/notifications/1/read
Authorization: Bearer <token>
```

### 4. Mark All as Read

```bash
PUT /api/notifications/read-all
Authorization: Bearer <token>
```

---

## Analytics & Reporting

### 1. Get Overall Analytics (Admin/Manager Only)

```bash
GET /api/analytics/overall
Authorization: Bearer <token>
```

**Response:**
```json
{
  "totalTasks": 25,
  "completedTasks": 10,
  "inProgressTasks": 8,
  "overdueTasks": 3,
  "completionRate": 40.0,
  "tasksByStatus": {
    "TODO": 5,
    "IN_PROGRESS": 8,
    "DONE": 10,
    "BLOCKED": 2
  },
  "tasksByPriority": {
    "LOW": 3,
    "MEDIUM": 10,
    "HIGH": 8,
    "URGENT": 4
  },
  "tasksByProject": {
    "Website Redesign": 12,
    "Mobile App Development": 13
  },
  "tasksByUser": {
    "john": 12,
    "jane": 13
  },
  "averageCompletionTime": 48.5
}
```

### 2. Get My Analytics

```bash
GET /api/analytics/my-analytics
Authorization: Bearer <token>
```

### 3. Get User Analytics (Admin/Manager Only)

```bash
GET /api/analytics/user/3
Authorization: Bearer <token>
```

### 4. Get Project Analytics

```bash
GET /api/analytics/project/1
Authorization: Bearer <token>
```

---

## Testing Scenarios

### Scenario 1: Complete Task Workflow

1. **Login as Manager**
   ```bash
   POST /api/auth/login
   {"username": "manager", "password": "manager123"}
   ```

2. **Create a Project**
   ```bash
   POST /api/projects
   {"name": "API Testing", "description": "Testing project"}
   ```

3. **Create a Task**
   ```bash
   POST /api/tasks
   {
     "title": "Test Task",
     "assignedToId": 3,
     "projectId": <project_id>
   }
   ```

4. **Login as User John**
   ```bash
   POST /api/auth/login
   {"username": "john", "password": "password123"}
   ```

5. **Check Notifications**
   ```bash
   GET /api/notifications/unread
   # Should see task assignment notification
   ```

6. **Update Task to In Progress**
   ```bash
   PUT /api/tasks/<task_id>
   {"status": "IN_PROGRESS"}
   ```

7. **Complete Task**
   ```bash
   PUT /api/tasks/<task_id>
   {"status": "DONE"}
   ```

8. **Login as Manager Again**
   ```bash
   POST /api/auth/login
   {"username": "manager", "password": "manager123"}
   ```

9. **Check Notifications**
   ```bash
   GET /api/notifications/unread
   # Should see task completion notification
   ```

10. **View Analytics**
    ```bash
    GET /api/analytics/project/<project_id>
    ```

### Scenario 2: Task Dependencies

1. **Create Task A**
   ```bash
   POST /api/tasks
   {"title": "Database Setup"}
   ```

2. **Create Task B**
   ```bash
   POST /api/tasks
   {"title": "API Development"}
   ```

3. **Add Dependency (B depends on A)**
   ```bash
   POST /api/tasks/<task_b_id>/dependencies/<task_a_id>
   ```

4. **Verify**
   ```bash
   GET /api/tasks/<task_b_id>
   # Should show dependency in dependencyIds
   ```

### Scenario 3: Virtual Threading Performance

1. **Make Multiple Async Calls Concurrently**
   ```bash
   # In separate terminals or use a load testing tool
   GET /api/tasks/user/3 &
   GET /api/tasks/user/4 &
   GET /api/tasks/overdue &
   ```

2. **Check Server Logs**
   ```
   Look for: "Running on thread: VirtualThread[#XX]"
   ```

---

## Error Cases

### 401 Unauthorized
```json
{
  "status": 401,
  "message": "Invalid username or password",
  "timestamp": "2025-11-22T10:00:00"
}
```

### 404 Not Found
```json
{
  "status": 404,
  "message": "Task not found with id: 999",
  "timestamp": "2025-11-22T10:00:00"
}
```

### 400 Validation Error
```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2025-11-22T10:00:00",
  "errors": {
    "title": "Title is required",
    "email": "Email should be valid"
  }
}
```

### 403 Forbidden
```json
{
  "status": 403,
  "message": "Access Denied",
  "timestamp": "2025-11-22T10:00:00"
}
```

---

## Postman Collection

You can import these examples into Postman:

1. Create a new environment with:
   - `base_url`: `http://localhost:8080`
   - `token`: (set after login)

2. Use `{{base_url}}` and `{{token}}` in your requests

3. Create a pre-request script for automatic token usage:
   ```javascript
   pm.request.headers.add({
     key: 'Authorization',
     value: 'Bearer ' + pm.environment.get('token')
   });
   ```

---

## Load Testing with ApacheBench

Test virtual threading performance:

```bash
# Test async endpoint
ab -n 1000 -c 100 -H "Authorization: Bearer <token>" \
  http://localhost:8080/api/tasks/user/3

# Test regular endpoint for comparison
ab -n 1000 -c 100 -H "Authorization: Bearer <token>" \
  http://localhost:8080/api/tasks
```

---

## Monitoring Cache Performance

1. **First Request** (Cache Miss)
   ```bash
   GET /api/tasks/1
   # Check logs: "Cache miss - fetching from database"
   ```

2. **Second Request** (Cache Hit)
   ```bash
   GET /api/tasks/1
   # Should be faster - served from Redis
   ```

3. **Update Task** (Cache Eviction)
   ```bash
   PUT /api/tasks/1
   # Cache is cleared
   ```

4. **Next Request** (Cache Miss Again)
   ```bash
   GET /api/tasks/1
   # Fetches from database, repopulates cache
   ```

---

## Tips

1. **Save Your Token**: Store the access token in a variable for reuse
2. **Check Logs**: Watch server console for virtual thread execution
3. **Use Swagger**: Interactive testing at `/swagger-ui.html`
4. **H2 Console**: View database at `/h2-console`
5. **Test Different Roles**: Login as admin, manager, and user to test permissions

Happy Testing! 🚀
