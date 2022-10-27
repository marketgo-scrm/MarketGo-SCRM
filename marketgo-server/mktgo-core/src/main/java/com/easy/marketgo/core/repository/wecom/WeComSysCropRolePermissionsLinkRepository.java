package com.easy.marketgo.core.repository.wecom;

import com.easy.marketgo.core.entity.WeComSysCorpRolePermissionsLinkEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-16 08:22:22
 * @description : WeComSysCropRolePermissionsLinkRepository.java
 */
public interface WeComSysCropRolePermissionsLinkRepository extends CrudRepository<WeComSysCorpRolePermissionsLinkEntity, Integer> {

    List<WeComSysCorpRolePermissionsLinkEntity> findByCorpIdAndRoleUuid(String corpId, String roleUuid);

}
