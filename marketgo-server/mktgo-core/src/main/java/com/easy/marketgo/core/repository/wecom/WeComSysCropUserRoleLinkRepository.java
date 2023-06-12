package com.easy.marketgo.core.repository.wecom;

import com.easy.marketgo.core.entity.WeComSysCorpUserRoleLinkEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-16 08:16:59
 * @description : WeComSysCropUserRoleLinkEntity.java
 */
public interface WeComSysCropUserRoleLinkRepository extends CrudRepository<WeComSysCorpUserRoleLinkEntity, Long> {

    List<WeComSysCorpUserRoleLinkEntity>  findByCorpIdAndProjectUuid(String corpId, String projectUuid);
    List<WeComSysCorpUserRoleLinkEntity> findByCorpIdAndRoleUuidAndProjectUuid(String corpId, String roleUuid, String projectUuid);

    List<WeComSysCorpUserRoleLinkEntity>  findByCorpIdAndProjectUuidAndMemberIdIn(String corpId,String projectUuid,List<String> memberIds);

    List<WeComSysCorpUserRoleLinkEntity>  findByMemberId(String memberId);

    WeComSysCorpUserRoleLinkEntity findByCorpIdAndProjectUuidAndMemberId(String corpId,String projectUuid,String memberIds);
}
