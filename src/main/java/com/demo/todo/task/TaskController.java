package com.demo.todo.task;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务接口,与安卓端 TodoRepository 的方法一一对应:
 *   GET    /tasks                  -> observeTasks
 *   POST   /tasks                  -> addTask
 *   PUT    /tasks/{id}             -> updateTask
 *   PATCH  /tasks/{id}/completed   -> setCompleted
 *   DELETE /tasks/{id}             -> deleteTask
 *   DELETE /tasks/completed        -> deleteCompleted
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskDto> list(@RequestAttribute("uid") Long uid) {
        return taskService.list(uid);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto create(@RequestAttribute("uid") Long uid,
                          @Valid @RequestBody TaskUpsertRequest request) {
        return taskService.create(uid, request);
    }

    @PutMapping("/{id}")
    public TaskDto update(@RequestAttribute("uid") Long uid,
                          @PathVariable Long id,
                          @Valid @RequestBody TaskUpsertRequest request) {
        return taskService.update(uid, id, request);
    }

    @PatchMapping("/{id}/completed")
    public TaskDto setCompleted(@RequestAttribute("uid") Long uid,
                                @PathVariable Long id,
                                @Valid @RequestBody CompletedRequest request) {
        return taskService.setCompleted(uid, id, request.done());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestAttribute("uid") Long uid, @PathVariable Long id) {
        taskService.delete(uid, id);
    }

    /** 注意:必须放在 /{id} 之后声明也不影响,Spring 会优先匹配字面量路径 */
    @DeleteMapping("/completed")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompleted(@RequestAttribute("uid") Long uid) {
        taskService.deleteCompleted(uid);
    }
}
