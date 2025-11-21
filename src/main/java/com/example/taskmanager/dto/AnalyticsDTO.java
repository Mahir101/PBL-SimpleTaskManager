package com.example.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDTO {
    private Long totalTasks;
    private Long completedTasks;
    private Long inProgressTasks;
    private Long overdueTasks;
    private Double completionRate;
    private Map<String, Long> tasksByStatus;
    private Map<String, Long> tasksByPriority;
    private Map<String, Long> tasksByProject;
    private Map<String, Long> tasksByUser;
    private Double averageCompletionTime;
}
