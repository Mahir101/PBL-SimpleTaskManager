package com.example.taskmanager.dto;

import com.example.taskmanager.entity.Project;
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
public class ProjectDTO {
    private Long id;

    @NotBlank(message = "Project name is required")
    private String name;

    private String description;
    private Project.ProjectStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long ownerId;
    private String ownerUsername;
    private Set<Long> memberIds;
    private String colorCode;
    private Integer taskCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
