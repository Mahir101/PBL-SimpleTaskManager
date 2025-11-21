package com.example.taskmanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "time_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_hours")
    private Double durationHours;

    @Column(length = 1000)
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (startTime != null && endTime != null) {
            durationHours = calculateDuration();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (startTime != null && endTime != null) {
            durationHours = calculateDuration();
        }
    }

    private Double calculateDuration() {
        long seconds = java.time.Duration.between(startTime, endTime).getSeconds();
        return seconds / 3600.0;
    }
}
