package com.demo.todo.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 新建 / 修改任务的请求体。
 * id、isCompleted、completedAt、createdAt 由服务端管理,不接受客户端传值:
 * 完成状态只能走 PATCH /tasks/{id}/completed。
 */
public record TaskUpsertRequest(
        @NotBlank(message = "标题不能为空")
        @Size(max = 200, message = "标题最长 200 字")
        String title,

        String note,
        Long dueDate,

        @Size(max = 5, message = "提醒时间格式应为 HH:mm")
        String dueTime,

        Integer priority,
        Long projectId,
        Integer sortOrder
) {
}
