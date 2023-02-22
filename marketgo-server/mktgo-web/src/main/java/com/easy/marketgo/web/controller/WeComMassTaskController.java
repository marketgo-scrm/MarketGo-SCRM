package com.easy.marketgo.web.controller;

import com.easy.marketgo.web.model.request.WeComMassTaskRequest;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.web.model.response.masstask.*;
import com.easy.marketgo.web.service.wecom.WeComMassTaskService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/15/22 6:28 PM
 * Describe:
 */
@Api(value = "群发任务管理", tags = "群发任务管理")
@RestController
@RequestMapping(value = "/mktgo/wecom/mass_task")
public class WeComMassTaskController {

    @Autowired
    private WeComMassTaskService weComMassTaskService;

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "创建or更新企微群发好友任务", nickname = "updateOrInsertMassTask", notes = "", response =
            BaseResponse.class)
    @RequestMapping(value = {"/save"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity updateOrInsertMassTask(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value = "corp_id", required =
                    true) String corpId,
            @ApiParam(value = "企微群发类型; SINGLE 群发好友; GROUP 群发客户群; MOMENT 群发朋友圈", required = true, allowableValues =
                    "SINGLE, GROUP, MOMENT") @RequestParam("task_type") @NotBlank @Valid String taskType,
            @ApiParam(value = "创建群发任务", required = true) @RequestBody @Valid WeComMassTaskRequest weComMassTaskRequest) {
        return ResponseEntity.ok(weComMassTaskService.updateOrInsertMassTask(projectId, corpId, taskType,
                weComMassTaskRequest));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "提醒用户发送群发任务", nickname = "remindSendMassTask", notes = "", response = BaseResponse.class)
    @RequestMapping(value = {"/remind"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity remindSendMassTask(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value = "corp_id", required =
                    true) String corpId,
            @ApiParam(value = "群发类型; SINGLE 群发好友; GROUP 群发客户群; MOMENT 群发朋友圈", required = true, allowableValues =
                    "SINGLE, GROUP, MOMENT") @NotNull @Valid @RequestParam(value = "task_type", required = true) String taskType,
            @ApiParam(value = "群发的任务的uuid", required = true) @NotNull @Valid @RequestParam(value = "task_uuid",
                    required = true) String taskId) {
        return ResponseEntity.ok(weComMassTaskService.remindSendTask(projectId, corpId, taskType, taskId));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "停止发送群发任务", nickname = "stopSendMassTask", notes = "", response = BaseResponse.class)
    @RequestMapping(value = {"/stop"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity stopSendMassTask(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value = "corp_id", required =
                    true) String corpId,
            @ApiParam(value = "群发类型; SINGLE 群发好友; GROUP 群发客户群; MOMENT 群发朋友圈", required = true, allowableValues =
                    "SINGLE, GROUP, MOMENT") @NotNull @Valid @RequestParam(value = "task_type", required = true) String taskType,
            @ApiParam(value = "群发的任务的uuid", required = true) @NotNull @Valid @RequestParam(value = "task_uuid",
                    required = true) String taskId) {
        return ResponseEntity.ok(weComMassTaskService.stopMassTaskMessage(projectId, corpId, taskType, taskId));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComGetMassTaskListResponse.class)
    })
    @ApiOperation(value = "获取群发任务列表", nickname = "listMassTasks", notes = "", response =
            WeComGetMassTaskListResponse.class)
    @RequestMapping(value = {"/list"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity listMassTasks(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企微群发类型; SINGLE 群发好友; GROUP 群发客户群; MOMENT 群发朋友圈", required = true, allowableValues =
                    "SINGLE, GROUP, MOMENT") @NotNull @Valid @RequestParam(value = "task_type", required = true) String taskType,
            @ApiParam(value = "页码", required = true) @NotNull @Min(1) @Valid @RequestParam(value = "page_num",
                    required = true, defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页条数", required = true) @NotNull @Min(1) @Max(1000) @Valid @RequestParam(value =
                    "page_size", required = true, defaultValue = "20") Integer pageSize,
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value = "corp_id", required =
                    true) String corpId,
            @ApiParam(value = "任务状态", required = false) @Valid @RequestParam(value = "statuses", required = false) List<String> statuses,
            @ApiParam(value = "任务名称", required = false) @Valid @RequestParam(value = "keyword", required = false) String keyword,
            @ApiParam(value = "创建人", required = false) @Valid @RequestParam(value = "create_user_ids", required =
                    false) List<String> createUserIds,
            @Valid @RequestParam(value = "sort_key", required = false) String sortKey,
            @Valid @RequestParam(value = "sort_order", required = false) String sortOrder,
            @ApiParam(value = "开始时间", required = false) @Valid @RequestParam(value = "start_time", required = false) String startTime,
            @ApiParam(value = "结束时间", required = false) @Valid @RequestParam(value = "end_time", required = false) String endTime) {
        return ResponseEntity.ok(weComMassTaskService.listMassTask(projectId, taskType, pageNum, pageSize, corpId,
                statuses, keyword, createUserIds, sortKey, sortOrder, startTime, endTime));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComQueryMassTaskStatisticForMembers.class)
    })
    @ApiOperation(value = "获取群发任务统计的员工列表", nickname = "WeComGetMassTaskListResponse", notes = "", response =
            WeComQueryMassTaskStatisticForMembers.class)
    @RequestMapping(value = {"/members"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity listMembers(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value =
                    "corp_id", required = true) String corpId,
            @ApiParam(value = "企微群发类型; SINGLE 群发好友; GROUP 群发客户群; MOMENT 群发朋友圈", required = true, allowableValues =
                    "SINGLE, GROUP, MOMENT") @NotNull @Valid @RequestParam(value = "task_type", required = true) String taskType,
            @ApiParam(value = "页码", required = true) @NotNull @Min(1) @Valid @RequestParam(value = "page_num",
                    required = true,
                    defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页条数", required = true) @NotNull @Min(1) @Max(1000) @Valid @RequestParam(value =
                    "page_size", required
                    = true, defaultValue = "20") Integer pageSize,
            @ApiParam(value = "企微群发uuid", required = true) @Valid @RequestParam(value =
                    "task_uuid", required = false) String taskUuid,
            @ApiParam(value = "任务名称", required = true) @Valid @RequestParam(value =
                    "keyword", required = false) String keyword,
            @ApiParam(value = "统计指标类型 MEMBER EXTERNAL_USER COMMENTS", required = true, allowableValues =
                    "MEMBER, EXTERNAL_USER,COMMENTS") @NotNull @Valid @RequestParam(value =
                    "metrics_type", required = true) String metricsType,
            @ApiParam(value = "企微群发状态 员工维度(UNSENT 未执行; SENT 已执行;) 客户维度(UNDELIVERED 未送达客户; DELIVERED 已送达客户; UNFRIEND " +
                    "送达失败的客户; EXCEED_LIMIT 结束达上限； COMMENT 评论； LIKE 点赞)", required = false, allowableValues = "UNSENT, " +
                    "SENT, UNDELIVERED, DELIVERED, UNFRIEND, EXCEED_LIMIT, COMMENT, LIKE ") @Valid @RequestParam(value = "status", required = false) String status) {
        return ResponseEntity.ok(weComMassTaskService.listMembers(projectId, corpId, taskType, metricsType, pageNum,
                pageSize, taskUuid, keyword, status));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComMassTaskDetailResponse.class)
    })
    @ApiOperation(value = "获取群发任务配置详情", nickname = "getMassTaskDetails", notes = "", response =
            WeComMassTaskDetailResponse.class)
    @RequestMapping(value = {"/detail"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity getMassTaskDetails(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "任务ID", required = true) @RequestParam("task_id") Integer taskId) {
        return ResponseEntity.ok(weComMassTaskService.getMassTaskDetails(projectId, taskId));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComQueryMassTaskStatisticResponse.class)
    })
    @ApiOperation(value = "获取群发任务统计数据", nickname = "getMassTaskStatistic", notes = "", response =
            WeComQueryMassTaskStatisticResponse.class)
    @RequestMapping(value = {"/statistic"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity getMassTaskStatistic(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企微群发uuid", required = true) @Valid @RequestParam(value = "task_uuid",
                    required = false) String taskUuid,
            @ApiParam(value = "统计指标类型 MEMBER 员工； EXTERNAL_USER 客户, COMMENTS 互动统计", required = true, allowableValues =
                    "MEMBER, EXTERNAL_USER, COMMENTS") @NotNull @Valid @RequestParam(value = "metrics_type",
                    required = true) String metricsType) {
        return ResponseEntity.ok(weComMassTaskService.getMassTaskStatistic(taskUuid, metricsType));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComMassTaskCreatorsResponse.class)
    })
    @ApiOperation(value = "获取群发任务创建人列表", nickname = "getMassTaskCreators", notes = "", response =
            WeComMassTaskCreatorsResponse.class)
    @RequestMapping(value = {"/creators"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity getMassTaskCreators(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value =
                    "corp_id", required = true) String corpId,
            @ApiParam(value = "群发好友 SINGLE; 群发客户群 GROUP; 群发朋友圈 MOMENT", required = true, allowableValues = "SINGLE, " +
                    "GROUP, MOMENT") @NotNull @Valid @RequestParam(value = "task_type", required = true) String taskType) {
        return ResponseEntity.ok(weComMassTaskService.getMassTaskCreators(projectId, corpId, taskType));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "删除群发任务", nickname = "deleteMassTask", notes = "", response = BaseResponse.class)
    @RequestMapping(value = {"/delete"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity deleteMassTask(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "任务UUID", required = true) @RequestParam("task_uuid") String taskUuid,
            @ApiParam(value = "群发类型; 群发好友 SINGLE; 群发客户群 GROUP; 群发朋友圈 MOMENT", required = true, allowableValues =
                    "SINGLE,GROUP, MOMENT") @NotNull @Valid @RequestParam(value = "task_type", required = true) String taskType) {
        return ResponseEntity.ok(weComMassTaskService.deleteMassTask(taskType, taskUuid));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "群发任务检查任务名称是否存在", nickname = "checkMassTaskName", notes = "", response =
            BaseResponse.class)
    @RequestMapping(value = {"/check_name"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity checkMassTaskName(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "群发类型; 群发好友 SINGLE; 群发客户群 GROUP; 群发朋友圈 MOMENT",
                    required = true, allowableValues = "SINGLE,GROUP, MOMENT") @NotNull @Valid @RequestParam(value =
                    "task_type", required = true) String taskType,
            @ApiParam(value = "任务中文名", required = true) @NotNull @Valid @RequestParam(value = "task_name", required
                    = true) String taskName,
            @ApiParam(value = "任务ID") @Valid @RequestParam(value = "task_id", required = false) Integer taskId) {
        return ResponseEntity.ok(weComMassTaskService.checkMassTaskName(projectId, taskType, taskId, taskName));
    }
}
