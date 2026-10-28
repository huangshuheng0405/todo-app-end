package com.demo.todo.project;

import com.demo.todo.common.ApiException;
import com.demo.todo.task.TaskEntity;
import com.demo.todo.task.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public ProjectService(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    public List<ProjectDto> list(Long uid) {
        return projectRepository.findByOwnerIdOrderBySortOrderAscIdAsc(uid)
                .stream().map(ProjectDto::from).toList();
    }

    @Transactional
    public ProjectDto create(Long uid, ProjectUpsertRequest request) {
        ProjectEntity entity = new ProjectEntity();
        entity.setOwnerId(uid);
        applyRequest(entity, request);
        return ProjectDto.from(projectRepository.save(entity));
    }

    @Transactional
    public ProjectDto update(Long uid, Long id, ProjectUpsertRequest request) {
        ProjectEntity entity = findOwned(uid, id);
        applyRequest(entity, request);
        return ProjectDto.from(projectRepository.save(entity));
    }

    /**
     * 删除项目时,把该项目下的任务移回收件箱(projectId 置空),
     * 避免留下指向已删项目的悬空引用。
     */
    @Transactional
    public void delete(Long uid, Long id) {
        ProjectEntity entity = findOwned(uid, id);
        List<TaskEntity> tasks = taskRepository.findByOwnerIdAndProjectId(uid, id);
        tasks.forEach(t -> t.setProjectId(null));
        taskRepository.saveAll(tasks);
        projectRepository.delete(entity);
    }

    private ProjectEntity findOwned(Long uid, Long id) {
        return projectRepository.findByIdAndOwnerId(id, uid)
                .orElseThrow(() -> ApiException.notFound("PROJECT_NOT_FOUND", "项目不存在"));
    }

    private void applyRequest(ProjectEntity entity, ProjectUpsertRequest request) {
        entity.setName(request.name().trim());
        entity.setColor(request.color());
        entity.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
    }
}
