package com.example.taskmanager.service;

import com.example.taskmanager.dto.AnalyticsDTO;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "analytics", key = "'overall'")
    public AnalyticsDTO getOverallAnalytics() {
        List<Task> allTasks = taskRepository.findAll();

        long totalTasks = allTasks.size();
        long completedTasks = allTasks.stream()
                .filter(t -> t.getStatus() == Task.TaskStatus.DONE)
                .count();
        long inProgressTasks = allTasks.stream()
                .filter(t -> t.getStatus() == Task.TaskStatus.IN_PROGRESS)
                .count();
        long overdueTasks = taskRepository.findOverdueTasks(LocalDateTime.now()).size();

        double completionRate = totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0.0;

        Map<String, Long> tasksByStatus = allTasks.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getStatus().name(),
                        Collectors.counting()
                ));

        Map<String, Long> tasksByPriority = allTasks.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getPriority().name(),
                        Collectors.counting()
                ));

        Map<String, Long> tasksByProject = allTasks.stream()
                .filter(t -> t.getProject() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getProject().getName(),
                        Collectors.counting()
                ));

        Map<String, Long> tasksByUser = allTasks.stream()
                .filter(t -> t.getAssignedTo() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getAssignedTo().getUsername(),
                        Collectors.counting()
                ));

        double avgCompletionTime = calculateAverageCompletionTime(allTasks);

        return AnalyticsDTO.builder()
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .inProgressTasks(inProgressTasks)
                .overdueTasks((long) overdueTasks)
                .completionRate(completionRate)
                .tasksByStatus(tasksByStatus)
                .tasksByPriority(tasksByPriority)
                .tasksByProject(tasksByProject)
                .tasksByUser(tasksByUser)
                .averageCompletionTime(avgCompletionTime)
                .build();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "analytics", key = "'user-' + #userId")
    public AnalyticsDTO getUserAnalytics(Long userId) {
        List<Task> userTasks = taskRepository.findActiveTasksByUserId(userId);

        long totalTasks = userTasks.size();
        long completedTasks = userTasks.stream()
                .filter(t -> t.getStatus() == Task.TaskStatus.DONE)
                .count();
        long inProgressTasks = userTasks.stream()
                .filter(t -> t.getStatus() == Task.TaskStatus.IN_PROGRESS)
                .count();
        long overdueTasks = userTasks.stream()
                .filter(t -> t.getDueDate() != null &&
                        t.getDueDate().isBefore(LocalDateTime.now()) &&
                        t.getStatus() != Task.TaskStatus.DONE)
                .count();

        double completionRate = totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0.0;

        Map<String, Long> tasksByStatus = userTasks.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getStatus().name(),
                        Collectors.counting()
                ));

        Map<String, Long> tasksByPriority = userTasks.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getPriority().name(),
                        Collectors.counting()
                ));

        Map<String, Long> tasksByProject = userTasks.stream()
                .filter(t -> t.getProject() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getProject().getName(),
                        Collectors.counting()
                ));

        double avgCompletionTime = calculateAverageCompletionTime(userTasks);

        return AnalyticsDTO.builder()
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .inProgressTasks(inProgressTasks)
                .overdueTasks(overdueTasks)
                .completionRate(completionRate)
                .tasksByStatus(tasksByStatus)
                .tasksByPriority(tasksByPriority)
                .tasksByProject(tasksByProject)
                .tasksByUser(new HashMap<>())
                .averageCompletionTime(avgCompletionTime)
                .build();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "analytics", key = "'project-' + #projectId")
    public AnalyticsDTO getProjectAnalytics(Long projectId) {
        List<Task> projectTasks = taskRepository.findByProjectId(projectId);

        long totalTasks = projectTasks.size();
        long completedTasks = projectTasks.stream()
                .filter(t -> t.getStatus() == Task.TaskStatus.DONE)
                .count();
        long inProgressTasks = projectTasks.stream()
                .filter(t -> t.getStatus() == Task.TaskStatus.IN_PROGRESS)
                .count();
        long overdueTasks = projectTasks.stream()
                .filter(t -> t.getDueDate() != null &&
                        t.getDueDate().isBefore(LocalDateTime.now()) &&
                        t.getStatus() != Task.TaskStatus.DONE)
                .count();

        double completionRate = totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0.0;

        Map<String, Long> tasksByStatus = projectTasks.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getStatus().name(),
                        Collectors.counting()
                ));

        Map<String, Long> tasksByPriority = projectTasks.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getPriority().name(),
                        Collectors.counting()
                ));

        Map<String, Long> tasksByUser = projectTasks.stream()
                .filter(t -> t.getAssignedTo() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getAssignedTo().getUsername(),
                        Collectors.counting()
                ));

        double avgCompletionTime = calculateAverageCompletionTime(projectTasks);

        return AnalyticsDTO.builder()
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .inProgressTasks(inProgressTasks)
                .overdueTasks(overdueTasks)
                .completionRate(completionRate)
                .tasksByStatus(tasksByStatus)
                .tasksByPriority(tasksByPriority)
                .tasksByProject(new HashMap<>())
                .tasksByUser(tasksByUser)
                .averageCompletionTime(avgCompletionTime)
                .build();
    }

    private double calculateAverageCompletionTime(List<Task> tasks) {
        List<Task> completedTasks = tasks.stream()
                .filter(t -> t.getStatus() == Task.TaskStatus.DONE &&
                        t.getCompletedAt() != null &&
                        t.getCreatedAt() != null)
                .toList();

        if (completedTasks.isEmpty()) {
            return 0.0;
        }

        double totalHours = completedTasks.stream()
                .mapToDouble(t -> Duration.between(t.getCreatedAt(), t.getCompletedAt()).toHours())
                .sum();

        return totalHours / completedTasks.size();
    }
}
