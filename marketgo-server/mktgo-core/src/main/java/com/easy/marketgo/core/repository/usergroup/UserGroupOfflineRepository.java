package com.easy.marketgo.core.repository.usergroup;

import com.easy.marketgo.core.entity.usergroup.UserGroupOfflineEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/17/22 11:27 AM
 * Describe:
 */
public interface UserGroupOfflineRepository extends CrudRepository<UserGroupOfflineEntity, Long> {
    @Query("SELECT DISTINCT(member_id) FROM user_group_offline WHERE corp_id = :corpId AND uuid = :uuid")
    List<String> queryMemberByUuid(String corpId, String uuid);

    @Query("SELECT COUNT(DISTINCT(external_user_id)) FROM user_group_offline WHERE corp_id = :corpId AND uuid = :uuid")
    Integer queryExternalUserCountByUuid(String corpId, String uuid);

    @Query("SELECT COUNT(DISTINCT(member_id)) FROM user_group_offline WHERE corp_id = :corpId AND uuid = :uuid")
    Integer queryMemberCountByUuid(String corpId, String uuid);

    @Query("SELECT DISTINCT(external_user_id) FROM user_group_offline WHERE corp_id = :corpId AND uuid = :uuid AND " +
            "member_id=:memberId")
    List<String> queryExternalUsersByUuidAndMemberId(String corpId, String uuid, String memberId);

    @Modifying
    @Query("DELETE FROM user_group_offline WHERE corp_id = :corpId AND uuid = :uuid")
    int deleteByUuid(String corpId, String uuid);
}
