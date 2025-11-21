package com.example.taskmanager.dto;

import com.example.taskmanager.entity.Task;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private Task.TaskStatus status;
    private Task.TaskPriority priority;
    private LocalDateTime dueDate;
    private LocalDateTime startDate;
    private LocalDateTime completedAt;
    private Double estimatedHours;
    private Double actualHours;
    private Long assignedToId;
    private String assignedToUsername;
    private Long createdById;
    private String createdByUsername;
    private Long projectId;
    private String projectName;
    private Set<String> tags;
    private Set<Long> dependencyIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
