package com.example.taskmanager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Task {
    @Id
    private Long id;
    private String title;
    private String description;
    private String status;

}
