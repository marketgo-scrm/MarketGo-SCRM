package com.easy.marketgo.core.repository.wecom.taskcenter;

import com.easy.marketgo.core.entity.taskcenter.WeComTaskCenterExternalUserStatisticEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/8/22 4:24 PM
 * Describe:
 */
public interface WeComTaskCenterExternalUserStatisticRepository extends CrudRepository<WeComTaskCenterExternalUserStatisticEntity, Long> {
    @Modifying
    @Query("UPDATE wecom_task_center_statistic_external_user set status= :status, receive_time = :receiveTime where " +
            "uuid= :uuid AND task_uuid=:task_uuid AND member_id=:member_id AND external_user_id=:external_user_id")
    void updateExternalUserStatus(@Param("status") String status, String receiveTime,
                                  String uuid,
                                  @Param("task_uuid") String taskUuid,
                                  @Param("member_id") String memberId,
                                  @Param("external_user_id") String externalUserId);

    @Query("SELECT COUNT(*) FROM wecom_task_center_statistic_external_user WHERE uuid= :uuid AND task_uuid= " +
            ":taskUuid AND status= :status")
    Integer countByTaskUuidAndStatus(String taskUuid, String uuid, String status);

    @Query("SELECT COUNT(*) FROM wecom_task_center_statistic_external_user WHERE task_uuid= :taskUuid AND status= " +
            ":status " +
            "AND plan_time LIKE concat('', :planTime, '%')")
    Integer countByTaskUuidAndStatusAndPlanTime(String taskUuid, String status, String planTime);

    @Query("SELECT COUNT(*) FROM wecom_task_center_statistic_external_user WHERE task_uuid= :taskUuid AND " +
            "external_user_type = :externalUserType AND status= :status")
    Integer countByTaskUuidAndStatusAndType(String taskUuid, String externalUserType, String status);

    @Query("SELECT COUNT(*) FROM wecom_task_center_statistic_external_user WHERE task_uuid= :taskUuid AND " +
            "external_user_id = :externalUserId AND member_id= :memberId")
    Integer countByTaskUuidAndMemberId(String taskUuid, String memberId, String externalUserId);

    @Modifying
    @Query("DELETE FROM wecom_task_center_statistic_external_user WHERE task_uuid = :task_uuid AND member_id = " +
            ":member_id AND status= :status")
    Integer deleteByTaskUuidAndMemberId(@Param("task_uuid") String taskUuid, @Param("member_id") String memberId,
                                        String status);

    @Modifying
    @Query("DELETE FROM wecom_task_center_statistic_external_user WHERE task_uuid = :task_uuid")
    Integer deleteByTaskUuid(@Param("task_uuid") String taskUuid);
}
