package com.easy.marketgo.core.repository.usergroup;

import com.easy.marketgo.core.entity.usergroup.UserGroupOfflineEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/17/22 11:27 AM
 * Describe:
 */
public interface UserGroupOfflineRepository extends CrudRepository<UserGroupOfflineEntity, Long> {
    @Modifying
    @Query("UPDATE user_group__offline SET estimate_result = :estimate_result, status = " +
            ":status WHERE request_id = :request_id AND project_uuid = :project_uuid")
    int updateResultByRequestId(@Param("request_id") String requestId,
                                @Param("project_uuid") String projectId,
                                @Param("estimate_result") String estimateResult,
                                @Param("status") String status);


    @Query("SELECT COUNT(DISTINCT(external_user_id)) FROM user_group_offline WHERE corp_id = :corpId AND uuid = :uuid")
    Integer queryExternalUserCountByUuid(String corpId, String uuid);

    @Query("SELECT COUNT(DISTINCT(member_id)) FROM user_group_offline WHERE corp_id = :corpId AND uuid = :uuid")
    Integer queryMemberCountByUuid(String corpId, String uuid);

    @Modifying
    @Query("UPDATE user_group__offline SET status = :status WHERE uuid = :uuid AND project_uuid = :project_uuid")
    int updateStatusByUuid(@Param("project_uuid") String projectId, @Param("uuid") String uuid,
                           @Param("status") String status);
}
