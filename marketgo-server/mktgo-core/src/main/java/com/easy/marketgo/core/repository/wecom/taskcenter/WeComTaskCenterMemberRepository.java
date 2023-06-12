package com.easy.marketgo.core.repository.wecom.taskcenter;

import com.easy.marketgo.core.entity.taskcenter.WeComTaskCenterMemberEntity;
import com.easy.marketgo.core.model.bo.WeComMassTaskCreators;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/24/22 9:20 PM
 * Describe:
 */
public interface WeComTaskCenterMemberRepository extends CrudRepository<WeComTaskCenterMemberEntity, Long>,
        WeComTaskCenterMemberCustomizedRepository {

    @Query("SELECT * FROM wecom_task_center_member WHERE project_uuid = :project_uuid AND name = :name")
    WeComTaskCenterMemberEntity getTaskCenterByName(@Param("project_uuid") String projectUuid,
                                                    @Param("name") String name);

    @Query("SELECT * FROM wecom_task_center_member WHERE id = :id")
    WeComTaskCenterMemberEntity queryById(@Param("id") Integer id);

    @Query("SELECT * FROM wecom_task_center_member WHERE DATE_ADD(NOW(), INTERVAL :minute MINUTE) > schedule_time AND" +
            " " +
            "task_type = :task_type AND task_status = :task_status AND schedule_type IN (:schedule_type)")
    List<WeComTaskCenterMemberEntity> getWeComMassTaskByScheduleTime(@Param("minute") int minute,
                                                                     @Param("task_type") String taskType,
                                                                     @Param("task_status") String taskStatus,
                                                                     @Param("schedule_type") List<String> scheduleType);

    @Query("SELECT * FROM wecom_task_center_member WHERE task_type = :task_type AND schedule_type IN (:schedule_type)" +
            " AND ( " +
            "plan_time is null OR DATE_ADD(plan_time, INTERVAL :hour HOUR) <  NOW())")
    List<WeComTaskCenterMemberEntity> getWeComMassTaskByScheduleType(@Param("hour") int hour,
                                                                     @Param("task_type") String taskType,
                                                                     @Param("schedule_type") List<String> scheduleType);

    @Query("SELECT * FROM wecom_task_center_member WHERE task_type = :task_type AND schedule_type IN (:schedule_type)" +
            " AND ( " +
            "plan_time is not null AND DATE_ADD(NOW(), INTERVAL :minute MINUTE) >plan_time)")
    List<WeComTaskCenterMemberEntity> getWeComMassTaskByPlanTime(@Param("minute") int minute,
                                                                 @Param("task_type") String taskType,
                                                                 @Param("schedule_type") List<String> scheduleType);

    @Query("SELECT * FROM wecom_task_center_member WHERE corp_id = :corpId AND member_id = :memberId AND task_status" +
            " != :taskStatus")
    List<WeComTaskCenterMemberEntity> getMemberTaskByStatus(String corpId, String memberId, String taskStatus);

    @Query("SELECT * FROM wecom_task_center_member WHERE project_uuid = :project_uuid AND task_type = :task_type AND " +
            "name = " +
            ":name")
    WeComTaskCenterMemberEntity getByProjectIdTypeAndName(@Param("project_uuid") String projectUuid,
                                                          @Param("task_type") String taskType,
                                                          @Param("name") String name);

    @Query("SELECT DISTINCT creator_id, creator_name FROM wecom_task_center_member WHERE project_uuid = :project_uuid" +
            " AND corp_id = :corp_id AND task_type = :task_type")
    List<WeComMassTaskCreators> listCreatorsByTaskType(@Param("project_uuid") String projectUuid,
                                                       @Param("corp_id") String corpId,
                                                       @Param("task_type") String taskType);

    @Query("SELECT * FROM wecom_task_center_member WHERE uuid = :uuid")
    WeComTaskCenterMemberEntity getByTaskUUID(@Param("uuid") String uuid);

    @Query("SELECT * FROM wecom_task_center_member WHERE task_type = :task_type AND task_status = :task_status and " +
            "schedule_time < :time")
    List<WeComTaskCenterMemberEntity> querytByStatusAndScheduleTime(@Param("task_type") String taskType,
                                                                    @Param("task_status") String taskStatus,
                                                                    @Param("time") String time);

    @Query("SELECT * FROM wecom_task_center_member WHERE  schedule_time < time")
    List<WeComTaskCenterMemberEntity> getByScheduleTime(@Param("time") String time);

    @Query("SELECT * FROM wecom_task_center_member WHERE task_status = :task_status")
    List<WeComTaskCenterMemberEntity> getByTaskStatus(@Param("task_status") String taskStatus);

    @Modifying
    @Query("UPDATE wecom_task_center_member SET plan_time = :plan_time  WHERE uuid=:uuid")
    int updateTaskPlanTime(@Param("plan_time") Date remindTime, @Param("uuid") String uuid);

    @Modifying
    @Query("DELETE FROM wecom_task_center_member WHERE task_uuid = :taskUuid")
    int deleteByUuid(String taskUuid);

    @Modifying
    @Query("DELETE FROM wecom_task_center_member WHERE id = :id AND task_status IN (:statuses)")
    int deleteByIdAndStatuses(@Param("id") Integer id, @Param("statuses") List<String> statuses);

    @Modifying
    @Query("DELETE FROM wecom_task_center_member WHERE id = :id AND task_type = :task_type")
    int deleteByIdAndTaskType(@Param("id") Integer id, @Param("task_type") String taskType);

    @Modifying
    @Query("UPDATE wecom_task_center_member SET task_status = :task_status WHERE uuid = :uuid")
    int updateTaskStatusByUUID(@Param("uuid") String uuid, @Param("task_status") String taskStatus);

    @Modifying
    @Query("UPDATE wecom_task_center_member SET task_status = :task_status, finish_time=:finishTime WHERE uuid = :uuid")
    int updateTaskStatusAndFinishTime(@Param("uuid") String uuid, @Param("task_status") String taskStatus, @Param(
            "finish_time") Date finishTime);

    @Query("SELECT * FROM wecom_task_center_member WHERE schedule_time BETWEEN CONCAT(DATE_ADD(curdate(), INTERVAL 1 " +
            "DAY), '" +
            " " +
            "00:00:00') AND CONCAT(DATE_ADD(curdate(), INTERVAL 1 DAY), ' 23:59:59')")
    List<WeComTaskCenterMemberEntity> findTomorrowTobeSentTaskList();

    @Query("SELECT * FROM wecom_task_center_member WHERE project_uuid = :projectUuid AND task_type = :taskType AND " +
            "uuid = " +
            ":uuid")
    WeComTaskCenterMemberEntity findByUuid(String projectUuid, String taskType, @Param("uuid") String uuid);
}
