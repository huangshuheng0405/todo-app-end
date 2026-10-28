package com.demo.todo.task;

import jakarta.validation.constraints.NotNull;

/** 切换完成状态的请求体:{ "done": true } */
public record CompletedRequest(@NotNull(message = "done 不能为空") Boolean done) {
}
