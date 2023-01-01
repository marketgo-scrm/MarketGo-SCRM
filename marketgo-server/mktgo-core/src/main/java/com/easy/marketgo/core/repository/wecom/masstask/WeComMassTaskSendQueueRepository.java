package com.easy.marketgo.core.repository.wecom.masstask;

import com.easy.marketgo.core.entity.masstask.WeComMassTaskSendQueueEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/12/22 4:04 PM
 * Describe:
 */
public interface WeComMassTaskSendQueueRepository extends CrudRepository<WeComMassTaskSendQueueEntity, Long> {
    @Query("SELECT * FROM wecom_mass_task_send_queue WHERE task_uuid = :task_uuid AND member_id = :member_id AND " +
            "status = :status")
    WeComMassTaskSendQueueEntity queryByTaskUuidAndMember(@Param("task_uuid") String taskUuid,
                                                          @Param("member_id") String memberId,
                                                          @Param("status") String status);

    @Modifying
    @Query("UPDATE wecom_mass_task_send_queue set status= :status where " +
            "task_uuid= :task_uuid AND member_id= :member_id AND status = :old_status")
    void updateStatusByTaskUuidAndMember(@Param("old_status") String oldStatus, @Param("status") String status,
                                         @Param("task_uuid") String taskUuid, @Param("member_id") String memberId);

    @Query("SELECT * FROM wecom_mass_task_send_queue WHERE task_uuid = :task_uuid AND status = :status")
    List<WeComMassTaskSendQueueEntity> queryByTaskUuid(@Param("task_uuid") String taskUuid,
                                                       @Param("status") String status);

    @Modifying
    @Query("UPDATE wecom_mass_task_send_queue set status= :status where task_uuid= :task_uuid AND status = :old_status")
    void updateStatusByTaskUuid(@Param("old_status") String oldStatus, @Param("status") String status,
                                @Param("task_uuid") String uuid);

    @Query("SELECT * FROM wecom_mass_task_send_queue WHERE uuid = :uuid")
    WeComMassTaskSendQueueEntity queryByUuid(String uuid);

    @Modifying
    @Query("DELETE FROM wecom_mass_task_send_queue WHERE uuid = :uuid")
    Integer deleteSendQueueByUuid(String uuid);
}
