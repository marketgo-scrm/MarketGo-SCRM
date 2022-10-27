package com.easy.marketgo.core.repository.wecom;

import com.easy.marketgo.core.entity.TenantConfigEntity;
import org.springframework.data.repository.CrudRepository;


/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-16 08:36:36
 * @description : TenantConfigRepository.java
 */
public interface TenantConfigRepository extends CrudRepository<TenantConfigEntity, Long> {

    TenantConfigEntity findByUuid(String uuid);
}
