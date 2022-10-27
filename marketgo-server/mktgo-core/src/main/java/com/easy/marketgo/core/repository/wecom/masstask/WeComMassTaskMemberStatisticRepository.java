package com.easy.marketgo.core.repository.wecom.masstask;

import com.easy.marketgo.core.entity.masstask.WeComMassTaskMemberStatisticEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/8/22 4:24 PM
 * Describe:
 */
public interface WeComMassTaskMemberStatisticRepository extends CrudRepository<WeComMassTaskMemberStatisticEntity,
        Long>, WeComMassTaskMemberStatisticCustomizedRepository {
    @Modifying
    @Query("UPDATE wecom_mass_task_statistic_member set send_id = :send_id, status= :status where " +
            "task_uuid= :task_uuid AND member_id= :member_id")
    void updateMemberStatus(@Param("send_id") String sendId, @Param("status") String status,
                            @Param("task_uuid") String taskUuid, @Param("member_id") String memberId);

    @Modifying
    @Query("UPDATE wecom_mass_task_statistic_member set status= :status where task_uuid= :task_uuid AND status= :old_status")
    void updateMemberStatusBytaskUuid(@Param("old_status") String oldStatus, @Param("status") String status,
                            @Param("task_uuid") String taskUuid);

    @Query("SELECT * FROM wecom_mass_task_statistic_member WHERE task_uuid= :task_uuid AND member_id= :member_id")
    WeComMassTaskMemberStatisticEntity queryByMemberAndTaskUuid(@Param("task_uuid") String taskUuid,
                                                                @Param("member_id") String memberId);

    @Query("SELECT COUNT(*) FROM wecom_mass_task_statistic_member WHERE task_uuid= :taskUuid AND status= :status")
    int countByTaskUuidAAndStatus(String taskUuid, String status);

    @Query("SELECT COUNT(*) FROM wecom_mass_task_statistic_member WHERE task_uuid= :taskUuid")
    List<WeComMassTaskMemberStatisticEntity> queryByTaskUuid(String taskUuid);

    @Modifying
    @Query("UPDATE wecom_mass_task_statistic_member set delivered_count = :delivered_count, non_friend_count= " +
            ":non_friend_count, exceed_limit_count= :exceed_limit_count where " +
            "task_uuid= :task_uuid AND member_id= :member_id")
    void updateMemberMetricsForExternalUser(@Param("delivered_count") Integer deliveredCount,
                                            @Param("non_friend_count") Integer nonFriendCount,
                                            @Param("exceed_limit_count") Integer exceedLimitCount,
                                            @Param("task_uuid") String taskUuid, @Param("member_id") String memberId);
}
