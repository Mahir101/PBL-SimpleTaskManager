package com.example.taskmanager.controller;

import com.example.taskmanager.dto.AnalyticsDTO;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.service.AnalyticsService;
import com.example.taskmanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Analytics and reporting endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final UserService userService;

    @GetMapping("/overall")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get overall analytics (Admin/Manager only)")
    public ResponseEntity<AnalyticsDTO> getOverallAnalytics() {
        AnalyticsDTO analytics = analyticsService.getOverallAnalytics();
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/my-analytics")
    @Operation(summary = "Get analytics for current user")
    public ResponseEntity<AnalyticsDTO> getMyAnalytics(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        AnalyticsDTO analytics = analyticsService.getUserAnalytics(user.getId());
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get analytics for specific user (Admin/Manager only)")
    public ResponseEntity<AnalyticsDTO> getUserAnalytics(@PathVariable Long userId) {
        AnalyticsDTO analytics = analyticsService.getUserAnalytics(userId);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get analytics for specific project")
    public ResponseEntity<AnalyticsDTO> getProjectAnalytics(@PathVariable Long projectId) {
        AnalyticsDTO analytics = analyticsService.getProjectAnalytics(projectId);
        return ResponseEntity.ok(analytics);
    }
}
