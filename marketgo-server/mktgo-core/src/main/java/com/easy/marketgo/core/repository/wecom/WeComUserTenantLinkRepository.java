package com.easy.marketgo.core.repository.wecom;

import com.easy.marketgo.core.entity.WeComUserTenantLinkEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-16 08:24:40
 * @description : WeComUserTenantLinkRepository.java
 */
public interface WeComUserTenantLinkRepository extends CrudRepository<WeComUserTenantLinkEntity, Long> {

    WeComUserTenantLinkEntity findByUserUuidAndTenantUuid(String userUuid, String tenantUuid);

    List<WeComUserTenantLinkEntity> findByUserUuid(String userUuid);

}
