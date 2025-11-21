package com.example.taskmanager.service;

import com.example.taskmanager.dto.ProjectDTO;
import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.ProjectRepository;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setStatus(projectDTO.getStatus() != null ? projectDTO.getStatus() : Project.ProjectStatus.ACTIVE);
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());
        project.setColorCode(projectDTO.getColorCode());

        User owner = getCurrentUser();
        project.setOwner(owner);

        if (projectDTO.getMemberIds() != null && !projectDTO.getMemberIds().isEmpty()) {
            Set<User> members = projectDTO.getMemberIds().stream()
                    .map(id -> userRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("User not found with id: " + id)))
                    .collect(Collectors.toSet());
            project.setMembers(members);
        }

        Project savedProject = projectRepository.save(project);
        return toDTO(savedProject);
    }

    @Transactional(readOnly = true)
    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        return toDTO(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjectDTO> getProjectsByCurrentUser() {
        User currentUser = getCurrentUser();
        return projectRepository.findProjectsByUserInvolvement(currentUser.getId()).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectDTO updateProject(Long id, ProjectDTO projectDTO) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setStatus(projectDTO.getStatus());
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());
        project.setColorCode(projectDTO.getColorCode());

        if (projectDTO.getMemberIds() != null) {
            Set<User> members = projectDTO.getMemberIds().stream()
                    .map(userId -> userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId)))
                    .collect(Collectors.toSet());
            project.setMembers(members);
        }

        Project updatedProject = projectRepository.save(project);
        return toDTO(updatedProject);
    }

    @Transactional
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new RuntimeException("Project not found with id: " + id);
        }
        projectRepository.deleteById(id);
    }

    @Transactional
    public ProjectDTO addMember(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        project.getMembers().add(user);
        Project updatedProject = projectRepository.save(project);
        return toDTO(updatedProject);
    }

    @Transactional
    public ProjectDTO removeMember(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        project.getMembers().remove(user);
        Project updatedProject = projectRepository.save(project);
        return toDTO(updatedProject);
    }

    private ProjectDTO toDTO(Project project) {
        return ProjectDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .status(project.getStatus())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .ownerId(project.getOwner().getId())
                .ownerUsername(project.getOwner().getUsername())
                .memberIds(project.getMembers().stream()
                        .map(User::getId)
                        .collect(Collectors.toSet()))
                .colorCode(project.getColorCode())
                .taskCount(project.getTasks().size())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }
}
