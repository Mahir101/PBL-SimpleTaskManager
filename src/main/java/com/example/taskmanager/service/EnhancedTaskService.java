package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskDTO;
import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.repository.ProjectRepository;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnhancedTaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskMapper taskMapper;
    private final NotificationService notificationService;

    @Transactional
    @CacheEvict(value = "tasks", allEntries = true)
    public TaskDTO createTask(TaskDTO taskDTO) {
        Task task = taskMapper.toEntity(taskDTO);

        User createdBy = getCurrentUser();
        task.setCreatedBy(createdBy);

        if (taskDTO.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(taskDTO.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("Assigned user not found"));
            task.setAssignedTo(assignedTo);
        }

        if (taskDTO.getProjectId() != null) {
            Project project = projectRepository.findById(taskDTO.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            task.setProject(project);
        }

        Task savedTask = taskRepository.save(task);

        if (savedTask.getAssignedTo() != null) {
            notificationService.sendTaskAssignedNotificationAsync(savedTask);
        }

        return taskMapper.toDTO(savedTask);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "tasks", key = "#id")
    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        return taskMapper.toDTO(task);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "tasks")
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "tasks", allEntries = true)
    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        Task.TaskStatus oldStatus = existingTask.getStatus();

        existingTask.setTitle(taskDTO.getTitle());
        existingTask.setDescription(taskDTO.getDescription());
        existingTask.setStatus(taskDTO.getStatus());
        existingTask.setPriority(taskDTO.getPriority());
        existingTask.setDueDate(taskDTO.getDueDate());
        existingTask.setStartDate(taskDTO.getStartDate());
        existingTask.setEstimatedHours(taskDTO.getEstimatedHours());
        existingTask.setTags(taskDTO.getTags());

        if (taskDTO.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(taskDTO.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("Assigned user not found"));

            if (existingTask.getAssignedTo() == null ||
                !existingTask.getAssignedTo().getId().equals(assignedTo.getId())) {
                existingTask.setAssignedTo(assignedTo);
                notificationService.sendTaskAssignedNotificationAsync(existingTask);
            }
        }

        if (taskDTO.getProjectId() != null) {
            Project project = projectRepository.findById(taskDTO.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            existingTask.setProject(project);
        }

        if (oldStatus != taskDTO.getStatus() && taskDTO.getStatus() == Task.TaskStatus.DONE) {
            existingTask.setCompletedAt(LocalDateTime.now());
            notificationService.sendTaskCompletedNotificationAsync(existingTask);
        }

        Task updatedTask = taskRepository.save(existingTask);
        return taskMapper.toDTO(updatedTask);
    }

    @Transactional
    @CacheEvict(value = "tasks", allEntries = true)
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    @Transactional
    @CacheEvict(value = "tasks", allEntries = true)
    public TaskDTO cloneTask(Long taskId) {
        Task originalTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        Task clonedTask = new Task();
        clonedTask.setTitle(originalTask.getTitle() + " (Copy)");
        clonedTask.setDescription(originalTask.getDescription());
        clonedTask.setPriority(originalTask.getPriority());
        clonedTask.setStatus(Task.TaskStatus.TODO);
        clonedTask.setEstimatedHours(originalTask.getEstimatedHours());
        clonedTask.setCreatedBy(getCurrentUser());
        clonedTask.setProject(originalTask.getProject());
        clonedTask.setTags(originalTask.getTags());

        Task savedTask = taskRepository.save(clonedTask);
        return taskMapper.toDTO(savedTask);
    }

    @Async("taskExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<List<TaskDTO>> getTasksByUserAsync(Long userId) {
        log.info("Fetching tasks for user {} on thread: {}", userId, Thread.currentThread());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<TaskDTO> tasks = taskRepository.findByAssignedTo(user).stream()
                .map(taskMapper::toDTO)
                .collect(Collectors.toList());

        return CompletableFuture.completedFuture(tasks);
    }

    @Async("taskExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<List<TaskDTO>> getOverdueTasksAsync() {
        log.info("Fetching overdue tasks on thread: {}", Thread.currentThread());
        List<TaskDTO> tasks = taskRepository.findOverdueTasks(LocalDateTime.now()).stream()
                .map(taskMapper::toDTO)
                .collect(Collectors.toList());

        return CompletableFuture.completedFuture(tasks);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> searchTasks(String keyword, Task.TaskStatus status,
                                      Task.TaskPriority priority, Long projectId) {
        Specification<Task> spec = (root, query, cb) -> cb.conjunction();

        if (keyword != null && !keyword.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                cb.or(
                    cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + keyword.toLowerCase() + "%")
                ));
        }

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        if (priority != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("priority"), priority));
        }

        if (projectId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("project").get("id"), projectId));
        }

        return taskRepository.findAll(spec).stream()
                .map(taskMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "tasks", allEntries = true)
    public TaskDTO addDependency(Long taskId, Long dependsOnId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        Task dependsOn = taskRepository.findById(dependsOnId)
                .orElseThrow(() -> new RuntimeException("Dependency task not found"));

        if (task.getId().equals(dependsOn.getId())) {
            throw new RuntimeException("Task cannot depend on itself");
        }

        task.getDependencies().add(dependsOn);
        Task updatedTask = taskRepository.save(task);
        return taskMapper.toDTO(updatedTask);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }
}
