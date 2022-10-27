package com.easy.marketgo.core.repository.wecom.masstask;

import com.easy.marketgo.core.entity.masstask.WeComMassTaskEntity;
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
 * @data : 6/24/22 9:20 PM
 * Describe:
 */
public interface WeComMassTaskRepository extends CrudRepository<WeComMassTaskEntity, Long>,
        WeComMassTaskCustomizedRepository {

    @Query("SELECT * FROM wecom_mass_task WHERE project_uuid = :project_uuid AND task_type = :task_type AND name = " +
            ":name")
    WeComMassTaskEntity getMassTaskByMassTaskTypeAndName(@Param("project_uuid") String projectUuid,
                                                         @Param("task_type") String taskType,
                                                         @Param("name") String name);

    @Query("SELECT * FROM wecom_mass_task WHERE id = :id")
    WeComMassTaskEntity queryById(@Param("id") Integer id);

    @Query("SELECT * FROM wecom_mass_task WHERE DATE_ADD(NOW(), INTERVAL :minute MINUTE) > schedule_time AND " +
            "task_type = :task_type AND task_status = :task_status")
    List<WeComMassTaskEntity> getWeComMassTaskByScheduleTime(@Param("minute") int minute,
                                                             @Param("task_type") String taskType,
                                                             @Param("task_status") String taskStatus);

    @Query("SELECT * FROM wecom_mass_task WHERE id = :id AND project_uuid = :project_uuid AND task_type = :task_type")
    WeComMassTaskEntity getByProjectIdTypeAndId(@Param("project_uuid") String projectUuid,
                                                @Param("task_type") String taskType,
                                                @Param("id") Integer id);

    @Query("SELECT * FROM wecom_mass_task WHERE project_uuid = :project_uuid AND task_type = :task_type AND name = " +
            ":name")
    WeComMassTaskEntity getByProjectIdTypeAndName(@Param("project_uuid") String projectUuid,
                                                  @Param("task_type") String taskType,
                                                  @Param("name") String name);

    @Query("SELECT DISTINCT creator_id, creator_name FROM wecom_mass_task WHERE project_uuid = :project_uuid" +
            " AND corp_id = :corp_id AND task_type = :task_type")
    List<WeComMassTaskCreators> listCreatorsByTaskType(@Param("project_uuid") String projectUuid,
                                                       @Param("corp_id") String corpId,
                                                       @Param("task_type") String taskType);

    @Query("SELECT * FROM wecom_mass_task WHERE uuid = :uuid")
    WeComMassTaskEntity getByTaskUUID(@Param("uuid") String uuid);

    @Query("SELECT * FROM wecom_mass_task WHERE task_type = :task_type AND task_status = :task_status and " +
            "schedule_time < :time")
    List<WeComMassTaskEntity> querytByStatusAndScheduleTime(@Param("task_type") String taskType,
                                                            @Param("task_status") String taskStatus,
                                                            @Param("time") String time);

    @Query("SELECT * FROM wecom_mass_task WHERE  schedule_time < time")
    List<WeComMassTaskEntity> getByScheduleTime(@Param("time") String time);

    @Query("SELECT * FROM wecom_mass_task WHERE task_status = :task_status")
    List<WeComMassTaskEntity> getByTaskStatus(@Param("task_status") String taskStatus);

    @Modifying
    @Query("UPDATE wecom_mass_task SET version = version + 1  WHERE id = :id AND version = :version")
    int updateTaskVersion(@Param("id") Integer id, @Param("version") Integer version);

    @Modifying
    @Query("UPDATE wecom_mass_task SET remind_time = :remind_time  WHERE uuid=:uuid")
    int updateTaskRemindTime(@Param("remind_time") Date remindTime, @Param("uuid") String uuid);

    @Modifying
    @Query("DELETE FROM wecom_mass_task WHERE uuid IN (:uuids) AND task_status IN (:statuses)")
    int deleteByUuidsAndStatuses(@Param("uuids") List<String> uuids, @Param("statuses") List<String> statuses);

    @Modifying
    @Query("DELETE FROM wecom_mass_task WHERE id = :id AND task_status IN (:statuses)")
    int deleteByIdAndStatuses(@Param("id") Integer id, @Param("statuses") List<String> statuses);

    @Modifying
    @Query("DELETE FROM wecom_mass_task WHERE id = :id AND task_type = :task_type")
    int deleteByIdAndTaskType(@Param("id") Integer id, @Param("task_type") String taskType);

    @Modifying
    @Query("UPDATE wecom_mass_task SET task_status = :task_status WHERE uuid = :uuid")
    int updateTaskStatusByUUID(@Param("uuid") String uuid, @Param("task_status") String taskStatus);

    @Modifying
    @Query("UPDATE wecom_mass_task SET task_status = :task_status, finish_time=:finishTime WHERE uuid = :uuid")
    int updateTaskStatusAndFinishTime(@Param("uuid") String uuid, @Param("task_status") String taskStatus, @Param(
            "finish_time") Date finishTime);

    @Query("SELECT * FROM wecom_mass_task WHERE schedule_time BETWEEN CONCAT(DATE_ADD(curdate(), INTERVAL 1 DAY), ' " +
            "00:00:00') AND CONCAT(DATE_ADD(curdate(), INTERVAL 1 DAY), ' 23:59:59')")
    List<WeComMassTaskEntity> findTomorrowTobeSentTaskList();

    @Query("SELECT * FROM wecom_mass_task WHERE project_uuid = :projectUuid AND task_type = :taskType AND uuid = :uuid")
    WeComMassTaskEntity findByUuid(String projectUuid, String taskType, @Param("uuid") String uuid);
}
