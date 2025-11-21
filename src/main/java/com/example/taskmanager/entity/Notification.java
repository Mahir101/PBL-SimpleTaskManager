package com.example.taskmanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(name = "is_read")
    private boolean isRead = false;

    @Column(name = "related_task_id")
    private Long relatedTaskId;

    @Column(name = "related_project_id")
    private Long relatedProjectId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum NotificationType {
        TASK_ASSIGNED,
        TASK_UPDATED,
        TASK_COMPLETED,
        TASK_DUE_SOON,
        TASK_OVERDUE,
        COMMENT_ADDED,
        PROJECT_UPDATED,
        MENTION
    }
}
