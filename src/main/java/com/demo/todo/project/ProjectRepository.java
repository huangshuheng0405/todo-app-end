package com.demo.todo.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    /** 与安卓端 TodoDao.observeProjects 的排序保持一致 */
    List<ProjectEntity> findByOwnerIdOrderBySortOrderAscIdAsc(Long ownerId);

    Optional<ProjectEntity> findByIdAndOwnerId(Long id, Long ownerId);
}
