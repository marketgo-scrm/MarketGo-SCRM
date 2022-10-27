package com.easy.marketgo.core.repository.wecom;

import com.easy.marketgo.core.entity.masstask.WeComUserGroupAudienceEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/27/22 11:26 AM
 * Describe:
 */
public interface WeComUserGroupAudienceRepository extends CrudRepository<WeComUserGroupAudienceEntity, Long> {
    @Modifying
    @Query("UPDATE user_group_estimate SET estimate_result = :estimate_result, status = " +
            ":status WHERE request_id = :request_id AND project_uuid = :project_uuid")
    int updateResultByRequestId(@Param("request_id") String requestId,
                                @Param("project_uuid") String projectId,
                                @Param("estimate_result") String estimateResult,
                                @Param("status") String status);


    @Query("SELECT * FROM user_group_estimate WHERE project_uuid = :projectUuid AND request_id = :requestId")
    WeComUserGroupAudienceEntity queryWeComUserGroupAudienceEntityByRequestId(String projectUuid, String requestId);

    @Query("SELECT * FROM user_group_estimate WHERE uuid = :uuid")
    WeComUserGroupAudienceEntity queryWeComUserGroupAudienceEntityByUuid(String uuid);

    @Modifying
    @Query("UPDATE user_group_estimate SET status = :status WHERE uuid = :uuid AND project_uuid = :project_uuid")
    int updateStatusByUuid(@Param("project_uuid") String projectId, @Param("uuid") String uuid,
                           @Param("status") String status);
}
