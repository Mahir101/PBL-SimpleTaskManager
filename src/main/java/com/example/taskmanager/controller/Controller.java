package com.example.taskmanager.controller;

import com.example.taskmanager.dto.Task;
import com.example.taskmanager.service.TaskManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/tasks")
@RestController
public class Controller {
    private final TaskManagerService taskManagerService;

    public Controller(TaskManagerService taskManagerService) {
        this.taskManagerService = taskManagerService;
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskManagerService.getAllTasks();
    }
    @PostMapping
    public Task createNewTask(@RequestBody Task task) {
        return taskManagerService.createNewTask(task);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        Task task = taskManagerService.updateTask(id, updatedTask);
        if (task != null) return ResponseEntity.ok(task);
        return ResponseEntity.notFound().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long id) {
        if (taskManagerService.deleteTask(id)) {
            return ResponseEntity.ok().build();
        }
        return  ResponseEntity.notFound().build();
    }
}



