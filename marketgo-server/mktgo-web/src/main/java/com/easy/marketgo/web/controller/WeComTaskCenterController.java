package com.easy.marketgo.web.controller;

import com.easy.marketgo.web.model.request.WeComTaskCenterRequest;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.web.model.response.masstask.WeComMassTaskCreatorsResponse;
import com.easy.marketgo.web.model.response.taskcenter.WeComMembersStatisticResponse;
import com.easy.marketgo.web.model.response.taskcenter.WeComTaskCenterDetailResponse;
import com.easy.marketgo.web.model.response.taskcenter.WeComTaskCenterListResponse;
import com.easy.marketgo.web.model.response.taskcenter.WeComTaskCenterStatisticResponse;
import com.easy.marketgo.web.service.wecom.WeComTaskCenterService;
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
 * @data : 12/15/22 6:28 PM
 * Describe:
 */
@Api(value = "任务中心管理", tags = "任务中心管理")
@RestController
@RequestMapping(value = "/mktgo/wecom/task_center")
public class WeComTaskCenterController {

    @Autowired
    private WeComTaskCenterService weComTaskCenterService;

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "创建or更新企微员工任务", nickname = "updateOrInsertTaskCenter", notes = "", response =
            BaseResponse.class)
    @RequestMapping(value = {"/save"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity updateOrInsertTaskCenter(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value = "corp_id", required =
                    true) String corpId,
            @ApiParam(value = "企微群发类型; SINGLE 群发好友; GROUP 群发客户群; MOMENT 群发朋友圈", required = true, allowableValues =
                    "SINGLE, GROUP, MOMENT") @RequestParam("task_type") @NotBlank @Valid String taskType,
            @ApiParam(value = "创建员工任务", required = true) @RequestBody @Valid WeComTaskCenterRequest weComTaskCenterRequest) {
        return ResponseEntity.ok(weComTaskCenterService.updateOrInsertTaskCenter(projectId, corpId, taskType,
                weComTaskCenterRequest));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "提醒员工执行任务", nickname = "remindSendTaskCenter", notes = "", response = BaseResponse.class)
    @RequestMapping(value = {"/remind"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity remindSendTaskCenter(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value = "corp_id", required =
                    true) String corpId,
            @ApiParam(value = "群发类型; SINGLE 群发好友; GROUP 群发客户群; MOMENT 群发朋友圈", required = true, allowableValues =
                    "SINGLE, GROUP, MOMENT") @NotNull @Valid @RequestParam(value = "task_type", required = true) String taskType,
            @ApiParam(value = "群发的任务的uuid", required = true) @NotNull @Valid @RequestParam(value = "task_uuid",
                    required = true) String taskId) {
        return ResponseEntity.ok(weComTaskCenterService.remindSendTask(projectId, corpId, taskType, taskId));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComTaskCenterListResponse.class)
    })
    @ApiOperation(value = "获取任务中心列表", nickname = "getTaskCenterList", notes = "", response =
            WeComTaskCenterListResponse.class)
    @RequestMapping(value = {"/list"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity getTaskCenterList(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "任务类型; SINGLE 客户任务; GROUP 客户群任务; MOMENT 朋友圈", required = false) @NotNull @Valid @RequestParam(value = "task_types", required = false) List<String> taskTypes,
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
        return ResponseEntity.ok(weComTaskCenterService.listTaskCenter(projectId, taskTypes, pageNum, pageSize, corpId,
                statuses, keyword, createUserIds, sortKey, sortOrder, startTime, endTime));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComMembersStatisticResponse.class)
    })
    @ApiOperation(value = "获取员工任务统计的员工列表", nickname = "WeComMembersStatisticResponse", notes = "", response =
            WeComMembersStatisticResponse.class)
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
            @ApiParam(value = "计划执行日期", required = true) @Valid @RequestParam(value =
                    "plan_date", required = false) String planDate,
            @ApiParam(value = "统计指标类型 MEMBER EXTERNAL_USER RATE", required = true, allowableValues =
                    "MEMBER, EXTERNAL_USER,RATE") @NotNull @Valid @RequestParam(value =
                    "metrics_type", required = true) String metricsType,
            @ApiParam(value = "企微群发状态 员工维度(UNSENT 未执行; SENT 已执行;) 客户维度(UNDELIVERED 未送达客户; DELIVERED 已送达客户; UNFRIEND " +
                    "送达失败的客户;)", required = false, allowableValues = "UNSENT, " +
                    "SENT, UNDELIVERED, DELIVERED, UNFRIEND") @Valid @RequestParam(value = "status",
                    required = false) String status) {
        return ResponseEntity.ok(weComTaskCenterService.listMembers(projectId, corpId, taskType, metricsType, pageNum,
                pageSize, taskUuid, keyword, status, planDate));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComTaskCenterDetailResponse.class)
    })
    @ApiOperation(value = "获取员工任务配置详情", nickname = "getTaskCenterDetails", notes = "", response =
            WeComTaskCenterDetailResponse.class)
    @RequestMapping(value = {"/detail"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity getTaskCenterDetails(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "任务ID", required = true) @RequestParam("task_id") Integer taskId) {
        return ResponseEntity.ok(weComTaskCenterService.getTaskCenterDetails(projectId, taskId));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComTaskCenterStatisticResponse.class)
    })
    @ApiOperation(value = "获取员工任务统计数据", nickname = "getTaskCenterStatistic", notes = "", response =
            WeComTaskCenterStatisticResponse.class)
    @RequestMapping(value = {"/statistic"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity getTaskCenterStatistic(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企微群发uuid", required = true) @Valid @RequestParam(value = "task_uuid",
                    required = false) String taskUuid,
            @ApiParam(value = "计划执行日期", required = true) @Valid @RequestParam(value =
                    "plan_date", required = false) String planDate,
            @ApiParam(value = "统计指标类型 MEMBER 员工； EXTERNAL_USER 客户, RATE 周期统计", required = true, allowableValues =
                    "MEMBER, EXTERNAL_USER, RATE") @NotNull @Valid @RequestParam(value = "metrics_type",
                    required = true) String metricsType) {
        return ResponseEntity.ok(weComTaskCenterService.getTaskCenterStatistic(taskUuid, metricsType, planDate));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComMassTaskCreatorsResponse.class)
    })
    @ApiOperation(value = "获取员工任务创建人列表", nickname = "getTaskCenterCreators", notes = "", response =
            WeComMassTaskCreatorsResponse.class)
    @RequestMapping(value = {"/creators"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity getTaskCenterCreators(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value =
                    "corp_id", required = true) String corpId,
            @ApiParam(value = "群发好友 SINGLE; 群发客户群 GROUP; 群发朋友圈 MOMENT", required = true, allowableValues = "SINGLE, " +
                    "GROUP, MOMENT") @NotNull @Valid @RequestParam(value = "task_type", required = true) String taskType) {
        return ResponseEntity.ok(weComTaskCenterService.getTaskCenterCreators(projectId, corpId, taskType));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "删除员工任务", nickname = "deleteTaskCenter", notes = "", response = BaseResponse.class)
    @RequestMapping(value = {"/delete"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity deleteTaskCenter(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "任务UUID", required = true) @RequestParam("task_uuid") String taskUuid,
            @ApiParam(value = "群发类型; 群发好友 SINGLE; 群发客户群 GROUP; 群发朋友圈 MOMENT", required = true, allowableValues =
                    "SINGLE,GROUP, MOMENT") @NotNull @Valid @RequestParam(value = "task_type", required = true) String taskType) {
        return ResponseEntity.ok(weComTaskCenterService.deleteTaskCenter(taskType, taskUuid));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "员工任务检查任务名称是否存在", nickname = "checkTaskCenterName", notes = "", response =
            BaseResponse.class)
    @RequestMapping(value = {"/check_name"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity checkTaskCenterName(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "群发类型; 群发好友 SINGLE; 群发客户群 GROUP; 群发朋友圈 MOMENT",
                    required = true, allowableValues = "SINGLE,GROUP, MOMENT") @NotNull @Valid @RequestParam(value =
                    "task_type", required = true) String taskType,
            @ApiParam(value = "任务中文名", required = true) @NotNull @Valid @RequestParam(value = "task_name", required
                    = true) String taskName,
            @ApiParam(value = "任务ID") @Valid @RequestParam(value = "task_id", required = false) Integer taskId) {
        return ResponseEntity.ok(weComTaskCenterService.checkTaskCenterName(projectId, taskType, taskId, taskName));
    }
}
