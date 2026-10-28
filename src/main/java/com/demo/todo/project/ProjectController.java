package com.demo.todo.project;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目接口:
 *   GET    /projects        列表
 *   POST   /projects        新建
 *   PUT    /projects/{id}   修改(改名/换色/调序)
 *   DELETE /projects/{id}   删除(项目下的任务移回收件箱)
 */
@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectDto> list(@RequestAttribute("uid") Long uid) {
        return projectService.list(uid);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto create(@RequestAttribute("uid") Long uid,
                             @Valid @RequestBody ProjectUpsertRequest request) {
        return projectService.create(uid, request);
    }

    @PutMapping("/{id}")
    public ProjectDto update(@RequestAttribute("uid") Long uid,
                             @PathVariable Long id,
                             @Valid @RequestBody ProjectUpsertRequest request) {
        return projectService.update(uid, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestAttribute("uid") Long uid, @PathVariable Long id) {
        projectService.delete(uid, id);
    }
}
