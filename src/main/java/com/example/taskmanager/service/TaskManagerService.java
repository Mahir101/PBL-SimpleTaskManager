package com.example.taskmanager.service;

import com.example.taskmanager.dto.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskManagerService {
    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task createNewTask(Task task) {
        Long id = idCounter.incrementAndGet();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    public Task updateTask(Long id, Task updatedTask) {
        Task existingTask = tasks.get(id);
        if (existingTask != null) {
            existingTask.setTitle(updatedTask.getTitle());
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setStatus(updatedTask.getStatus());
            tasks.put(id, existingTask);
            return existingTask;
        }
        return null;
    }

    public boolean deleteTask(Long id) {
        return tasks.remove(id) != null;
    }
}
