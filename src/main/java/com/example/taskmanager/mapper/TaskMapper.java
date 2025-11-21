package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.TaskDTO;
import com.example.taskmanager.entity.Task;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TaskMapper {

    public TaskDTO toDTO(Task task) {
        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .startDate(task.getStartDate())
                .completedAt(task.getCompletedAt())
                .estimatedHours(task.getEstimatedHours())
                .actualHours(task.getActualHours())
                .assignedToId(task.getAssignedTo() != null ? task.getAssignedTo().getId() : null)
                .assignedToUsername(task.getAssignedTo() != null ? task.getAssignedTo().getUsername() : null)
                .createdById(task.getCreatedBy() != null ? task.getCreatedBy().getId() : null)
                .createdByUsername(task.getCreatedBy() != null ? task.getCreatedBy().getUsername() : null)
                .projectId(task.getProject() != null ? task.getProject().getId() : null)
                .projectName(task.getProject() != null ? task.getProject().getName() : null)
                .tags(task.getTags())
                .dependencyIds(task.getDependencies().stream()
                        .map(Task::getId)
                        .collect(Collectors.toSet()))
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    public Task toEntity(TaskDTO dto) {
        Task task = new Task();
        task.setId(dto.getId());
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setPriority(dto.getPriority());
        task.setDueDate(dto.getDueDate());
        task.setStartDate(dto.getStartDate());
        task.setEstimatedHours(dto.getEstimatedHours());
        task.setTags(dto.getTags());
        return task;
    }
}
