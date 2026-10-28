package com.demo.todo.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    /** 与安卓端 TodoDao.observeTasks 的排序保持一致 */
    List<TaskEntity> findByOwnerIdOrderBySortOrderAscCreatedAtDesc(Long ownerId);

    /** 所有写操作都走这个方法取实体,天然防止改到别人的数据 */
    Optional<TaskEntity> findByIdAndOwnerId(Long id, Long ownerId);

    long deleteByOwnerIdAndCompletedTrue(Long ownerId);

    /** 删除项目时把该项目下的任务摘出来(projectId 置空) */
    List<TaskEntity> findByOwnerIdAndProjectId(Long ownerId, Long projectId);
}
