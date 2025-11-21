package com.example.taskmanager.service;

import com.example.taskmanager.entity.Notification;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.NotificationRepository;
import com.example.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    private final TaskRepository taskRepository;

    @Value("${spring.mail.username:noreply@taskmanager.com}")
    private String fromEmail;

    @Value("${app.base-url}")
    private String baseUrl;

    @Async("notificationExecutor")
    @Transactional
    public void sendTaskAssignedNotificationAsync(Task task) {
        log.info("Sending task assigned notification on thread: {}", Thread.currentThread());

        if (task.getAssignedTo() == null) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(task.getAssignedTo());
        notification.setTitle("New Task Assigned");
        notification.setMessage(String.format("You have been assigned to task: %s", task.getTitle()));
        notification.setType(Notification.NotificationType.TASK_ASSIGNED);
        notification.setRelatedTaskId(task.getId());
        notificationRepository.save(notification);

        sendEmail(
                task.getAssignedTo().getEmail(),
                "New Task Assigned: " + task.getTitle(),
                String.format("You have been assigned to task: %s\n\nDescription: %s\n\nDue Date: %s\n\nView task: %s/tasks/%d",
                        task.getTitle(),
                        task.getDescription() != null ? task.getDescription() : "N/A",
                        task.getDueDate() != null ? task.getDueDate() : "Not set",
                        baseUrl,
                        task.getId())
        );
    }

    @Async("notificationExecutor")
    @Transactional
    public void sendTaskCompletedNotificationAsync(Task task) {
        log.info("Sending task completed notification on thread: {}", Thread.currentThread());

        Notification notification = new Notification();
        notification.setUser(task.getCreatedBy());
        notification.setTitle("Task Completed");
        notification.setMessage(String.format("Task '%s' has been marked as completed", task.getTitle()));
        notification.setType(Notification.NotificationType.TASK_COMPLETED);
        notification.setRelatedTaskId(task.getId());
        notificationRepository.save(notification);

        sendEmail(
                task.getCreatedBy().getEmail(),
                "Task Completed: " + task.getTitle(),
                String.format("Task '%s' has been marked as completed.\n\nView task: %s/tasks/%d",
                        task.getTitle(),
                        baseUrl,
                        task.getId())
        );
    }

    @Scheduled(cron = "0 0 9 * * *") // Run every day at 9 AM
    @Transactional
    public void sendDueDateReminders() {
        log.info("Running scheduled task for due date reminders");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);

        List<Task> upcomingTasks = taskRepository.findTasksDueBetween(now, tomorrow);

        for (Task task : upcomingTasks) {
            if (task.getAssignedTo() != null) {
                Notification notification = new Notification();
                notification.setUser(task.getAssignedTo());
                notification.setTitle("Task Due Soon");
                notification.setMessage(String.format("Task '%s' is due on %s", task.getTitle(), task.getDueDate()));
                notification.setType(Notification.NotificationType.TASK_DUE_SOON);
                notification.setRelatedTaskId(task.getId());
                notificationRepository.save(notification);

                sendEmail(
                        task.getAssignedTo().getEmail(),
                        "Task Due Soon: " + task.getTitle(),
                        String.format("Reminder: Task '%s' is due on %s\n\nView task: %s/tasks/%d",
                                task.getTitle(),
                                task.getDueDate(),
                                baseUrl,
                                task.getId())
                );
            }
        }

        List<Task> overdueTasks = taskRepository.findOverdueTasks(now);
        for (Task task : overdueTasks) {
            if (task.getAssignedTo() != null) {
                Notification notification = new Notification();
                notification.setUser(task.getAssignedTo());
                notification.setTitle("Task Overdue");
                notification.setMessage(String.format("Task '%s' is overdue!", task.getTitle()));
                notification.setType(Notification.NotificationType.TASK_OVERDUE);
                notification.setRelatedTaskId(task.getId());
                notificationRepository.save(notification);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}. Error: {}", to, e.getMessage());
        }
    }
}
