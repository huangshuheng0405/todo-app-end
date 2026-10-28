package com.demo.todo.task;

import com.demo.todo.common.ApiException;
import com.demo.todo.project.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 任务业务逻辑。所有方法都要求传入当前登录用户 id,
 * 查询条件里带上 ownerId,保证用户之间数据互不可见。
 */
@Service
public class TaskService {

    /** 与安卓端 PRIORITY_NONE 保持一致 */
    private static final int PRIORITY_NONE = 4;

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    public List<TaskDto> list(Long uid) {
        return taskRepository.findByOwnerIdOrderBySortOrderAscCreatedAtDesc(uid)
                .stream().map(TaskDto::from).toList();
    }

    @Transactional
    public TaskDto create(Long uid, TaskUpsertRequest request) {
        TaskEntity entity = new TaskEntity();
        entity.setOwnerId(uid);
        applyRequest(entity, request, uid);
        entity.setCreatedAt(System.currentTimeMillis());
        return TaskDto.from(taskRepository.save(entity));
    }

    @Transactional
    public TaskDto update(Long uid, Long id, TaskUpsertRequest request) {
        TaskEntity entity = findOwned(uid, id);
        applyRequest(entity, request, uid);
        return TaskDto.from(taskRepository.save(entity));
    }

    /** 对应安卓端 setCompleted:完成时由服务端写 completedAt,取消完成时清空 */
    @Transactional
    public TaskDto setCompleted(Long uid, Long id, boolean done) {
        TaskEntity entity = findOwned(uid, id);
        entity.setCompleted(done);
        entity.setCompletedAt(done ? System.currentTimeMillis() : null);
        return TaskDto.from(taskRepository.save(entity));
    }

    @Transactional
    public void delete(Long uid, Long id) {
        TaskEntity entity = findOwned(uid, id);
        taskRepository.delete(entity);
    }

    /** 对应安卓端 deleteCompleted:清空当前用户所有已完成任务 */
    @Transactional
    public void deleteCompleted(Long uid) {
        taskRepository.deleteByOwnerIdAndCompletedTrue(uid);
    }

    private TaskEntity findOwned(Long uid, Long id) {
        return taskRepository.findByIdAndOwnerId(id, uid)
                .orElseThrow(() -> ApiException.notFound("TASK_NOT_FOUND", "任务不存在"));
    }

    private void applyRequest(TaskEntity entity, TaskUpsertRequest request, Long uid) {
        entity.setTitle(request.title().trim());
        entity.setNote(request.note() == null ? "" : request.note());
        entity.setDueDate(request.dueDate());
        entity.setDueTime(request.dueTime());
        entity.setPriority(normalizePriority(request.priority()));
        entity.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        entity.setProjectId(validateProjectId(request.projectId(), uid));
    }

    /** priority 只接受 1~4,其余一律按"无优先级"处理 */
    private int normalizePriority(Integer priority) {
        if (priority == null || priority < 1 || priority > PRIORITY_NONE) {
            return PRIORITY_NONE;
        }
        return priority;
    }

    /** 传了 projectId 就必须是当前用户自己的项目,防止把任务挂到别人项目下 */
    private Long validateProjectId(Long projectId, Long uid) {
        if (projectId == null) {
            return null;
        }
        projectRepository.findByIdAndOwnerId(projectId, uid)
                .orElseThrow(() -> ApiException.badRequest("PROJECT_NOT_FOUND", "项目不存在"));
        return projectId;
    }
}
