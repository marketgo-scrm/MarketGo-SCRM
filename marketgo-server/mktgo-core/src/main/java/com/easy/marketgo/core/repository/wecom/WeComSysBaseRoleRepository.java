package com.easy.marketgo.core.repository.wecom;

import com.easy.marketgo.core.entity.WeComSysBaseRoleEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-15 22:48:59
 * @description : WeComSysBaseRoleRepository.java
 */

public interface WeComSysBaseRoleRepository extends CrudRepository<WeComSysBaseRoleEntity, Long> {
    List<WeComSysBaseRoleEntity> findByProjectUuid( String projectUuid);
    List<WeComSysBaseRoleEntity> findByProjectUuidAndUuid( String projectUuid,String roleUuid);

}
