package com.easy.marketgo.core.repository.wecom;

import com.easy.marketgo.core.entity.WeComSysBasePermissionsEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-15 22:48:59
 * @description : WeComSysBasePermissionsRepository.java
 */

public interface WeComSysBasePermissionsRepository extends CrudRepository<WeComSysBasePermissionsEntity, Long> {
    List<WeComSysBasePermissionsEntity> findByProjectUuid(String projectUuid);
}
