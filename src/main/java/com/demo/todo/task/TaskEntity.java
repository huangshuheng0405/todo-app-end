package com.demo.todo.task;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 任务表,字段与安卓端 Room 实体 Task 一一对应:
 * - dueDate: 到期日 epoch 毫秒(当天 00:00),null 表示没有日期
 * - dueTime: 可选提醒时间,形如 "14:00"
 * - priority: 1 = P1 最高 … 4 = 无优先级
 * - ownerId: 数据归属者,等于 users.id,所有查询都按它隔离
 */
@Entity
@Table(name = "tasks", indexes = @Index(name = "idx_tasks_owner", columnList = "ownerId"))
@Getter
@Setter
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String note = "";

    private Long dueDate;

    @Column(length = 5)
    private String dueTime;

    @Column(nullable = false)
    private Integer priority = 4;

    /** 所属项目,允许为空(收件箱) */
    private Long projectId;

    @Column(nullable = false)
    private boolean completed = false;

    /** 完成时间,取消完成时清回 null;由服务端维护 */
    private Long completedAt;

    @Column(nullable = false)
    private Long createdAt;

    @Column(nullable = false)
    private Integer sortOrder = 0;
}
