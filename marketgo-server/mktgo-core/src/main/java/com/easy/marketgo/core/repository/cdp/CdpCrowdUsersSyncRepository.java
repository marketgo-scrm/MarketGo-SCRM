package com.easy.marketgo.core.repository.cdp;

import com.easy.marketgo.core.entity.cdp.CdpCrowdUsersSyncEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/24/22 3:29 PM
 * Describe:
 */
public interface CdpCrowdUsersSyncRepository extends CrudRepository<CdpCrowdUsersSyncEntity, Long> {


    @Query("SELECT * FROM cdp_crowd_users_sync WHERE sync_status = :sync_status")
    List<CdpCrowdUsersSyncEntity> getQueryCrowdUsersBySyncStatus(@Param("sync_status") String syncStatus);

    @Modifying
    @Query("DELETE FROM cdp_crowd_users_sync WHERE uuid = :uuid")
    int deleteByUuid(String uuid);

    @Query("SELECT * FROM cdp_crowd_users_sync WHERE project_uuid = :projectId AND corp_id = :corpId AND cdp_type = " +
            ":cdpType AND sync_status IN (:syncStatus) AND crowd_code = :crowdCode")
    CdpCrowdUsersSyncEntity getFinishCrowdByCdpAndCrowd(String projectId, String corpId, String cdpType,
                                                        List<String> syncStatus, String crowdCode);

    @Query("SELECT * FROM cdp_crowd_users_sync WHERE task_uuid = :taskUuid")
    List<CdpCrowdUsersSyncEntity> getCrowdsByTaskUuid(String taskUuid);

    @Query("SELECT DISTINCT(task_uuid) FROM cdp_crowd_users_sync WHERE sync_status = :syncStatus")
    List<String> queryTaskUuidByCrowdCode(String syncStatus);

    @Query("SELECT DISTINCT(crowd_code) FROM cdp_crowd_users_sync WHERE sync_status = :syncStatus AND task_uuid = " +
            ":taskUuid")
    List<String> queryCrowdCodeByTaskUuidAndSyncStatus(String taskUuid, String syncStatus);

    @Query("SELECT * FROM cdp_crowd_users_sync WHERE sync_status != :syncStatus AND task_uuid = :taskUuid")
    List<CdpCrowdUsersSyncEntity> getCrowdsByTaskUuidAndSyncStatus(String taskUuid, String syncStatus);

    @Modifying
    @Query("UPDATE cdp_crowd_users_sync SET sync_status = :syncStatus WHERE project_uuid = :projectId AND corp_id = " +
            ":corpId AND cdp_type = :cdpType AND crowd_code = :crowdCode")
    int updateSyncStatusByCrowd(String projectId, String corpId, String cdpType, String crowdCode,
                                String syncStatus);

    @Modifying
    @Query("UPDATE cdp_crowd_users_sync SET status = :status WHERE project_uuid = :projectId AND corp_id = :corpId " +
            "AND cdp_type" +
            " = :cdpType")
    int updateCdpStatusByCorpIdAndCdpType(String projectId, String corpId, String cdpType, Boolean status);

    @Modifying
    @Query("DELETE FROM cdp_crowd_users_sync WHERE project_uuid = :projectId AND corp_id = :corpId AND cdp_type = " +
            ":cdpType")
    int deleteByCorpIdAndCdpType(String projectId, String corpId, String cdpType);
}
