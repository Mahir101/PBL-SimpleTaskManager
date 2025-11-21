package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskDTO;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.service.EnhancedTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class TaskController {

    private final EnhancedTaskService taskService;

    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        TaskDTO createdTask = taskService.createTask(taskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        TaskDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @GetMapping
    @Operation(summary = "Get all tasks")
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<TaskDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @Valid @RequestBody TaskDTO taskDTO) {
        TaskDTO updatedTask = taskService.updateTask(id, taskDTO);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete a task")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/clone")
    @Operation(summary = "Clone an existing task")
    public ResponseEntity<TaskDTO> cloneTask(@PathVariable Long id) {
        TaskDTO clonedTask = taskService.cloneTask(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(clonedTask);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get tasks by user (async with virtual threads)")
    public CompletableFuture<ResponseEntity<List<TaskDTO>>> getTasksByUser(@PathVariable Long userId) {
        return taskService.getTasksByUserAsync(userId)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue tasks (async with virtual threads)")
    public CompletableFuture<ResponseEntity<List<TaskDTO>>> getOverdueTasks() {
        return taskService.getOverdueTasksAsync()
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/search")
    @Operation(summary = "Search tasks with filters")
    public ResponseEntity<List<TaskDTO>> searchTasks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Task.TaskStatus status,
            @RequestParam(required = false) Task.TaskPriority priority,
            @RequestParam(required = false) Long projectId
    ) {
        List<TaskDTO> tasks = taskService.searchTasks(keyword, status, priority, projectId);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/{taskId}/dependencies/{dependsOnId}")
    @Operation(summary = "Add task dependency")
    public ResponseEntity<TaskDTO> addDependency(
            @PathVariable Long taskId,
            @PathVariable Long dependsOnId
    ) {
        TaskDTO task = taskService.addDependency(taskId, dependsOnId);
        return ResponseEntity.ok(task);
    }
}
