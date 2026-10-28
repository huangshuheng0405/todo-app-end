package com.demo.todo.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** 新建 / 修改项目的请求体 */
public record ProjectUpsertRequest(
        @NotBlank(message = "项目名不能为空")
        @Size(max = 50, message = "项目名最长 50 字")
        String name,

        @NotNull(message = "颜色不能为空")
        Integer color,

        Integer sortOrder
) {
}
