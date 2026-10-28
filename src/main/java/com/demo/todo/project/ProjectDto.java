package com.demo.todo.project;

/** 返回给安卓端的项目 JSON,字段名与安卓端 Project 数据类一致 */
public record ProjectDto(
        Long id,
        String name,
        Integer color,
        Integer sortOrder
) {

    public static ProjectDto from(ProjectEntity entity) {
        return new ProjectDto(entity.getId(), entity.getName(), entity.getColor(), entity.getSortOrder());
    }
}
