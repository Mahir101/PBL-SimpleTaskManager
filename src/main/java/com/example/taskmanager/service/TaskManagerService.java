package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskDto;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.repository.TaskManagerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskManagerService {
    private final TaskManagerRepository taskManagerRepository;

    public TaskManagerService(TaskManagerRepository taskManagerRepository) {
        this.taskManagerRepository = taskManagerRepository;
    }

    public List<Task> getAllTasks() {
        return taskManagerRepository.findAll();
    }

    public Task createNewTask(TaskDto taskDto) {
        Task task = new Task();
        task.setDescription(taskDto.getDescription());
        task.setTitle(taskDto.getTitle());
        task.setStatus(taskDto.getStatus());
        return taskManagerRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskManagerRepository.deleteById(id);
    }
}
