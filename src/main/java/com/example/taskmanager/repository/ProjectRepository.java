package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwnerId(Long ownerId);
    List<Project> findByStatus(Project.ProjectStatus status);

    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN p.members m WHERE p.owner.id = :userId OR m.id = :userId")
    List<Project> findProjectsByUserInvolvement(@Param("userId") Long userId);
}
