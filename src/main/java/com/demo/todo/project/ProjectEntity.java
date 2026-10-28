package com.demo.todo.project;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 项目(清单)表,字段与安卓端 Room 实体 Project 一一对应。
 * color 是 ARGB int,比如 0xFFE91E63。
 */
@Entity
@Table(name = "projects", indexes = @Index(name = "idx_projects_owner", columnList = "ownerId"))
@Getter
@Setter
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer color;

    @Column(nullable = false)
    private Integer sortOrder = 0;
}
