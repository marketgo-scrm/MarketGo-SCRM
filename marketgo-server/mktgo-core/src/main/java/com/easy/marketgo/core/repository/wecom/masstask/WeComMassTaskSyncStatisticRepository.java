package com.easy.marketgo.core.repository.wecom.masstask;

import com.easy.marketgo.core.entity.masstask.WeComMassTaskSyncStatisticEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/1/22 9:12 PM
 * Describe:
 */
public interface WeComMassTaskSyncStatisticRepository extends CrudRepository<WeComMassTaskSyncStatisticEntity, Long> {
    @Modifying
    @Query("UPDATE wecom_mass_task_statistic_sync set deleted=1 where task_uuid = :task_uuid and send_id = :send_id")
    void deleteByTaskUuidAndSendId(@Param("task_uuid") String taskUuid, @Param("send_id") String sendId);

    @Query("SELECT * FROM wecom_mass_task_statistic_sync WHERE sync_status is null OR sync_status != :sync_status AND" +
            " deleted=0 AND task_type = :task_type AND send_id_type = :send_id_type ORDER BY id ASC LIMIT :offset, " +
            ":limit")
    List<WeComMassTaskSyncStatisticEntity> queryWeComMassTaskSendIdByTaskType(@Param("sync_status") String syncStatus,
                                                                              @Param("task_type") String taskType,
                                                                              @Param("send_id_type") String resultType,
                                                                              @Param("offset") Integer offset,
                                                                              @Param("limit") Integer limit);

    @Query("SELECT * FROM wecom_mass_task_statistic_sync WHERE task_uuid = :task_uuid AND deleted=0 ORDER BY id ASC " +
            "LIMIT :offset, :limit")
    List<WeComMassTaskSyncStatisticEntity> getWeComMassTaskResponseByTaskUuid(@Param("task_uuid") String taskUuid,
                                                                              @Param("offset") Integer offset,
                                                                              @Param("limit") Integer limit);

    @Query("SELECT COUNT(*) FROM wecom_mass_task_statistic_sync WHERE task_uuid = :task_uuid AND deleted=0")
    int countBySyncStatusAndTaskUuid(@Param("task_uuid") String taskUuid);


    @Query("SELECT * FROM wecom_mass_task_statistic_sync WHERE sync_status = :sync_status AND deleted=0 ORDER BY id " +
            "ASC LIMIT :offset, :limit")
    List<WeComMassTaskSyncStatisticEntity> getWeComMassTaskResponseBySyncStatus(@Param("sync_status") String syncStatus,
                                                                                @Param("offset") Integer offset,
                                                                                @Param("limit") Integer limit);

    @Query("SELECT * FROM wecom_mass_task_statistic_sync WHERE DATE_SUB(CURDATE(), INTERVAL :day DAY) > create_time " +
            "AND task_type = :task_type AND deleted=0 ORDER BY id ASC LIMIT :offset, :limit")
    List<WeComMassTaskSyncStatisticEntity> getWeComMassTaskResponseByCreateTime(@Param("day") int day, @Param(
            "task_type") String taskType,
                                                                                @Param("offset") Integer offset,
                                                                                @Param("limit") Integer limit);

    @Modifying
    @Query("UPDATE wecom_mass_task_statistic_sync set sync_status = :sync_status where id = :id")
    void updateWeComMassTaskSyncStatus(@Param("sync_status") String syncStatus, @Param("id") long id);

    @Modifying
    @Query("UPDATE wecom_mass_task_statistic_sync set sync_status = :to_sync_status where sync_status = " +
            ":from_sync_status AND id = :id")
    void resetWeComMassTaskSyncStatus(@Param("from_sync_status") String fromSyncStatus,
                                      @Param("to_sync_status") String toSyncStatus, @Param("id") long id);

    @Modifying
    @Query("UPDATE wecom_mass_task_statistic_sync set send_id = :new_send_id, send_id_type= :send_id_type where " +
            "send_id = :old_send_id")
    void updateSendIdMsg(@Param("old_send_id") String oldSendId, @Param("new_send_id") String newSendId,
                         @Param("send_id_type") String sendIdType);
}
