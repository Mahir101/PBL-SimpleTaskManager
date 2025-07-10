package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskDto;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.service.TaskManagerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class Controller {
    private final TaskManagerService taskManagerService;

    public Controller(TaskManagerService taskManagerService) {
        this.taskManagerService = taskManagerService;
    }

    @GetMapping("/tasks")
    public List<Task> getAllTasks() {
        return taskManagerService.getAllTasks();
    }
    @PostMapping("/tasks")
    public Task createNewTask(@RequestBody TaskDto taskDto) {
        return taskManagerService.createNewTask(taskDto);
    }
    @DeleteMapping("/tasks/{id}")
    public void deleteTask(@PathVariable("id") Long id) {
        taskManagerService.deleteTask(id);
    }


}



