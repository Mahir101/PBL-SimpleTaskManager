package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TaskManagerRepository extends JpaRepository<Task, Long> {

}
