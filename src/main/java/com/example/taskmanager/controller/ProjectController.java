package com.example.taskmanager.controller;

import com.example.taskmanager.dto.ProjectDTO;
import com.example.taskmanager.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Project management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @Operation(summary = "Create a new project")
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectDTO projectDTO) {
        ProjectDTO createdProject = projectService.createProject(projectDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        ProjectDTO project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    @GetMapping
    @Operation(summary = "Get all projects")
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<ProjectDTO> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/my-projects")
    @Operation(summary = "Get projects for current user")
    public ResponseEntity<List<ProjectDTO>> getMyProjects() {
        List<ProjectDTO> projects = projectService.getProjectsByCurrentUser();
        return ResponseEntity.ok(projects);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a project")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectDTO projectDTO) {
        ProjectDTO updatedProject = projectService.updateProject(id, projectDTO);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete a project")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/members/{userId}")
    @Operation(summary = "Add member to project")
    public ResponseEntity<ProjectDTO> addMember(@PathVariable Long projectId, @PathVariable Long userId) {
        ProjectDTO project = projectService.addMember(projectId, userId);
        return ResponseEntity.ok(project);
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    @Operation(summary = "Remove member from project")
    public ResponseEntity<ProjectDTO> removeMember(@PathVariable Long projectId, @PathVariable Long userId) {
        ProjectDTO project = projectService.removeMember(projectId, userId);
        return ResponseEntity.ok(project);
    }
}
