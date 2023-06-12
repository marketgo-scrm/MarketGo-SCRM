package com.easy.marketgo.core.repository.wecom;

import com.easy.marketgo.core.entity.ProjectConfigEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-16 08:54:41
 * @description : ProjectConfigRepository.java
 */
public interface ProjectConfigRepository extends CrudRepository<ProjectConfigEntity, Long> {

    ProjectConfigEntity findByTenantUuidAndUuid(String tenantUuid, String uuid);

    @Query("SELECT * FROM project_config WHERE uuid in (:uuids)")
    List<ProjectConfigEntity>  findByUuids(List<String> uuids);

    List<ProjectConfigEntity> findByTenantUuid(String tenantUuid);

    ProjectConfigEntity findAllByUuid(String uuid);

    @Query("SELECT * FROM project_config WHERE id = :projectId AND name = :name")
    ProjectConfigEntity queryByName(Integer projectId, String name);
}
