package com.demo.todo.task;

/**
 * 返回给安卓端的任务 JSON。字段名与安卓端 Task 数据类完全一致,
 * 客户端用 Gson / kotlinx-serialization 可以直接映射。
 */
public record TaskDto(
        Long id,
        String title,
        String note,
        Long dueDate,
        String dueTime,
        Integer priority,
        Long projectId,
        Boolean isCompleted,
        Long completedAt,
        Long createdAt,
        Integer sortOrder
) {

    public static TaskDto from(TaskEntity entity) {
        return new TaskDto(
                entity.getId(),
                entity.getTitle(),
                entity.getNote(),
                entity.getDueDate(),
                entity.getDueTime(),
                entity.getPriority(),
                entity.getProjectId(),
                entity.isCompleted(),
                entity.getCompletedAt(),
                entity.getCreatedAt(),
                entity.getSortOrder()
        );
    }
}
