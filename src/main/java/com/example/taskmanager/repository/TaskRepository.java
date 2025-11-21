package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    List<Task> findByAssignedTo(User user);
    List<Task> findByCreatedBy(User user);
    List<Task> findByStatus(Task.TaskStatus status);
    List<Task> findByPriority(Task.TaskPriority priority);
    List<Task> findByProjectId(Long projectId);

    @Query("SELECT t FROM Task t WHERE t.assignedTo.id = :userId AND t.status != 'DONE' AND t.status != 'CANCELLED'")
    List<Task> findActiveTasksByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :start AND :end")
    List<Task> findTasksDueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT t FROM Task t WHERE t.dueDate < :now AND t.status != 'DONE' AND t.status != 'CANCELLED'")
    List<Task> findOverdueTasks(@Param("now") LocalDateTime now);

    @Query("SELECT t FROM Task t WHERE t.assignedTo.id = :userId AND t.dueDate BETWEEN :start AND :end")
    List<Task> findUserTasksDueBetween(@Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
