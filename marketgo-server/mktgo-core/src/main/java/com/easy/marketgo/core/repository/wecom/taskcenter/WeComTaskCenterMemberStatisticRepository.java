package com.easy.marketgo.core.repository.wecom.taskcenter;

import com.easy.marketgo.core.entity.taskcenter.WeComTaskCenterMemberStatisticEntity;
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
public interface WeComTaskCenterMemberStatisticRepository extends CrudRepository<WeComTaskCenterMemberStatisticEntity,
        Long>, WeComTaskCenterMemberStatisticCustomizedRepository {
    @Modifying
    @Query("UPDATE wecom_task_center_statistic_member set send_id = :send_id, status= :status where " +
            "task_uuid= :task_uuid AND member_id= :member_id")
    void updateMemberStatus(@Param("send_id") String sendId, @Param("status") String status,
                            @Param("task_uuid") String taskUuid, @Param("member_id") String memberId);

    @Modifying
    @Query("UPDATE wecom_task_center_statistic_member set status= :status, sent_time= :sent_time where uuid= " +
            ":uuid AND status= :old_status AND member_id= :member_id AND task_uuid= :task_uuid")
    void updateMemberStatusBytaskUuid(@Param("old_status") String oldStatus, @Param("status") String status, @Param(
            "sent_time") String sentTime, @Param("task_uuid") String taskUuid, @Param("uuid") String uuid,
                                      @Param("member_id") String memberId);

    @Query("SELECT * FROM wecom_task_center_statistic_member WHERE task_uuid= :task_uuid AND member_id= :member_id")
    WeComTaskCenterMemberStatisticEntity queryByMemberAndTaskUuid(@Param("task_uuid") String taskUuid,
                                                                  @Param("member_id") String memberId);

    @Query("SELECT COUNT(*) FROM wecom_task_center_statistic_member WHERE task_uuid= :taskUuid AND status= :status " +
            "AND plan_time LIKE concat('', :planTime, '%')")
    Integer countByTaskUuidAndStatus(String taskUuid, String status, String planTime);

    @Query("SELECT COUNT(distinct plan_time) FROM wecom_task_center_statistic_member WHERE task_uuid= :taskUuid")
    Integer countByTaskUuidAndPlanTime(String taskUuid);

    @Query("SELECT COUNT(*) FROM wecom_task_center_statistic_member WHERE task_uuid= :taskUuid AND status= :status")
    Integer countByTaskUuidAndTaskStatus(String taskUuid, String status);

    @Query("SELECT * FROM wecom_task_center_statistic_member WHERE task_uuid= :taskUuid AND status = :status")
    List<WeComTaskCenterMemberStatisticEntity> queryByTaskUuidAndStatus(String taskUuid, String status);

    @Query("SELECT * FROM wecom_task_center_statistic_member WHERE task_uuid= :taskUuid")
    List<WeComTaskCenterMemberStatisticEntity> queryByTaskUuid(String taskUuid);

    @Query("SELECT * FROM wecom_task_center_statistic_member WHERE task_uuid= :taskUuid AND plan_time LIKE concat('', :planTime, '%')")
    List<WeComTaskCenterMemberStatisticEntity> queryByTaskUuidAndplanTime(String taskUuid, String planTime);

    @Modifying
    @Query("UPDATE wecom_task_center_statistic_member set delivered_count = :delivered_count, non_friend_count= " +
            ":non_friend_count where task_uuid= :task_uuid AND member_id= :member_id")
    void updateMemberMetricsForExternalUser(@Param("delivered_count") Integer deliveredCount,
                                            @Param("non_friend_count") Integer nonFriendCount,
                                            @Param("task_uuid") String taskUuid, @Param("member_id") String memberId);

    @Modifying
    @Query("DELETE FROM wecom_task_center_statistic_member WHERE task_uuid= :taskUuid")
    int deleteByTaskUuid(String taskUuid);
}
