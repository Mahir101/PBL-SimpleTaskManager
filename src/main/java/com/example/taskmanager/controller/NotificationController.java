package com.example.taskmanager.controller;

import com.example.taskmanager.entity.Notification;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.service.NotificationService;
import com.example.taskmanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all notifications for current user")
    public ResponseEntity<List<Notification>> getMyNotifications(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        List<Notification> notifications = notificationService.getUserNotifications(user.getId());
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    @Operation(summary = "Get unread notifications for current user")
    public ResponseEntity<List<Notification>> getUnreadNotifications(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        List<Notification> notifications = notificationService.getUnreadNotifications(user.getId());
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok().build();
    }
}
