package com.example.taskmanager.config;

import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.ProjectRepository;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Initializing development data...");

        if (userRepository.count() > 0) {
            log.info("Data already exists. Skipping initialization.");
            return;
        }

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@taskmanager.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setRole(User.UserRole.ADMIN);
        admin.setActive(true);
        admin = userRepository.save(admin);

        User manager = new User();
        manager.setUsername("manager");
        manager.setEmail("manager@taskmanager.com");
        manager.setPassword(passwordEncoder.encode("manager123"));
        manager.setFirstName("Manager");
        manager.setLastName("User");
        manager.setRole(User.UserRole.MANAGER);
        manager.setActive(true);
        manager = userRepository.save(manager);

        User user1 = new User();
        user1.setUsername("john");
        user1.setEmail("john@taskmanager.com");
        user1.setPassword(passwordEncoder.encode("password123"));
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setRole(User.UserRole.USER);
        user1.setActive(true);
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("jane");
        user2.setEmail("jane@taskmanager.com");
        user2.setPassword(passwordEncoder.encode("password123"));
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setRole(User.UserRole.USER);
        user2.setActive(true);
        user2 = userRepository.save(user2);

        Project project1 = new Project();
        project1.setName("Website Redesign");
        project1.setDescription("Complete redesign of company website");
        project1.setStatus(Project.ProjectStatus.ACTIVE);
        project1.setStartDate(LocalDateTime.now());
        project1.setOwner(manager);
        project1.setMembers(Set.of(user1, user2));
        project1.setColorCode("#3498db");
        project1 = projectRepository.save(project1);

        Project project2 = new Project();
        project2.setName("Mobile App Development");
        project2.setDescription("Develop iOS and Android mobile applications");
        project2.setStatus(Project.ProjectStatus.ACTIVE);
        project2.setStartDate(LocalDateTime.now());
        project2.setOwner(admin);
        project2.setMembers(Set.of(user1));
        project2.setColorCode("#e74c3c");
        project2 = projectRepository.save(project2);

        Task task1 = new Task();
        task1.setTitle("Design Homepage Mockup");
        task1.setDescription("Create high-fidelity mockup for the new homepage");
        task1.setStatus(Task.TaskStatus.IN_PROGRESS);
        task1.setPriority(Task.TaskPriority.HIGH);
        task1.setDueDate(LocalDateTime.now().plusDays(7));
        task1.setEstimatedHours(8.0);
        task1.setCreatedBy(manager);
        task1.setAssignedTo(user1);
        task1.setProject(project1);
        task1.setTags(Set.of("design", "frontend", "urgent"));
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTitle("Implement Authentication API");
        task2.setDescription("Develop JWT-based authentication system");
        task2.setStatus(Task.TaskStatus.TODO);
        task2.setPriority(Task.TaskPriority.URGENT);
        task2.setDueDate(LocalDateTime.now().plusDays(5));
        task2.setEstimatedHours(16.0);
        task2.setCreatedBy(admin);
        task2.setAssignedTo(user2);
        task2.setProject(project2);
        task2.setTags(Set.of("backend", "security", "api"));
        taskRepository.save(task2);

        Task task3 = new Task();
        task3.setTitle("Setup CI/CD Pipeline");
        task3.setDescription("Configure automated deployment pipeline");
        task3.setStatus(Task.TaskStatus.TODO);
        task3.setPriority(Task.TaskPriority.MEDIUM);
        task3.setDueDate(LocalDateTime.now().plusDays(10));
        task3.setEstimatedHours(12.0);
        task3.setCreatedBy(admin);
        task3.setAssignedTo(user1);
        task3.setProject(project1);
        task3.setTags(Set.of("devops", "automation"));
        taskRepository.save(task3);

        Task task4 = new Task();
        task4.setTitle("Write Unit Tests");
        task4.setDescription("Achieve 80% code coverage with unit tests");
        task4.setStatus(Task.TaskStatus.DONE);
        task4.setPriority(Task.TaskPriority.MEDIUM);
        task4.setDueDate(LocalDateTime.now().minusDays(2));
        task4.setCompletedAt(LocalDateTime.now().minusDays(1));
        task4.setEstimatedHours(20.0);
        task4.setActualHours(18.0);
        task4.setCreatedBy(manager);
        task4.setAssignedTo(user2);
        task4.setProject(project2);
        task4.setTags(Set.of("testing", "quality"));
        taskRepository.save(task4);

        Task task5 = new Task();
        task5.setTitle("Database Schema Migration");
        task5.setDescription("Update database schema for new features");
        task5.setStatus(Task.TaskStatus.BLOCKED);
        task5.setPriority(Task.TaskPriority.HIGH);
        task5.setDueDate(LocalDateTime.now().plusDays(3));
        task5.setEstimatedHours(6.0);
        task5.setCreatedBy(admin);
        task5.setAssignedTo(user1);
        task5.setProject(project2);
        task5.setTags(Set.of("database", "migration"));
        taskRepository.save(task5);

        log.info("Development data initialized successfully!");
        log.info("Sample users:");
        log.info("  - Admin: username=admin, password=admin123");
        log.info("  - Manager: username=manager, password=manager123");
        log.info("  - User 1: username=john, password=password123");
        log.info("  - User 2: username=jane, password=password123");
    }
}
