package com.easy.marketgo.gateway.controller;

import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.gateway.wecom.request.client.WeComChangeStatusRequest;
import com.easy.marketgo.gateway.wecom.request.client.WeComTaskCenterContentClientResponse;
import com.easy.marketgo.gateway.wecom.request.client.WeComTaskCenterDetailClientResponse;
import com.easy.marketgo.gateway.wecom.request.client.WeComTaskCenterListClientResponse;
import com.easy.marketgo.gateway.wecom.sevice.taskcenter.QueryTaskCenterDetailService;
import com.easy.marketgo.gateway.wecom.sevice.SidebarManagerService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/21/22 3:01 PM
 * Describe:
 */
@Api(value = "客户端任务管理", tags = "客户端任务管理")
@RestController
@RequestMapping(value = "/mktgo/client/wecom/sidebar")
@Slf4j
public class WeComClientSidebarController {

    @Autowired
    private QueryTaskCenterDetailService queryTaskCenterDetailService;

    @Autowired
    private SidebarManagerService sidebarManagerService;

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComTaskCenterListClientResponse.class)
    })
    @ApiOperation(value = "获取任务中心列表", nickname = "getTaskCenterList", notes = "", response =
            WeComTaskCenterListClientResponse.class)
    @RequestMapping(value = {"/list"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity getTaskCenterList(
            @ApiParam(value = "类型 MASS_TASK 群发任务 TASK_CENTER 任务中心", required = false) @Valid @RequestParam(value =
                    "types",
                    required = false) List<String> types,
            @ApiParam(value = "任务类型; SINGLE 客户任务; GROUP 客户群任务; MOMENT 朋友圈", required = false) @NotNull @Valid @RequestParam(value = "task_types", required = false) List<String> taskTypes,
            @ApiParam(value = "页码", required = true) @NotNull @Min(1) @Valid @RequestParam(value = "page_num",
                    required = true, defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页条数", required = true) @NotNull @Min(1) @Max(1000) @Valid @RequestParam(value =
                    "page_size", required = true, defaultValue = "20") Integer pageSize,
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value = "corp_id", required =
                    true) String corpId,
            @ApiParam(value = "任务状态", required = false) @Valid @RequestParam(value = "statuses", required = false) List<String> statuses,
            @ApiParam(value = "任务名称", required = false) @Valid @RequestParam(value = "keyword", required = false) String keyword,
            @ApiParam(value = "员工ID", required = true) @Valid @RequestParam(value = "member_id", required = true) String memberId,
            @ApiParam(value = "创建人", required = false) @Valid @RequestParam(value = "create_user_ids", required =
                    false) List<String> createUserIds,
            @Valid @RequestParam(value = "sort_key", required = false) String sortKey,
            @Valid @RequestParam(value = "sort_order", required = false) String sortOrder,
            @ApiParam(value = "开始时间", required = false) @Valid @RequestParam(value = "start_time", required = false) String startTime,
            @ApiParam(value = "结束时间", required = false) @Valid @RequestParam(value = "end_time", required = false) String endTime) {
        return ResponseEntity.ok(queryTaskCenterDetailService.listTaskCenter(types, taskTypes, pageNum, pageSize,
                corpId, statuses, keyword, memberId, createUserIds, sortKey, sortOrder, startTime, endTime));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComTaskCenterDetailClientResponse.class)
    })
    @ApiOperation(value = "获取员工任务配置详情", nickname = "getTaskCenterDetails", notes = "", response =
            WeComTaskCenterDetailClientResponse.class)
    @RequestMapping(value = {"/detail"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity getTaskCenterDetails(
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value = "corp_id", required =
                    true) String corpId,
            @ApiParam(value = "员工ID", required = false) @Valid @RequestParam(value = "member_id", required = false) String memberId,
            @ApiParam(value = "任务ID", required = true) @RequestParam("task_uuid") String taskUuid,
            @ApiParam(value = "uuid", required = false) @NotNull @Valid @RequestParam(value = "uuid", required =
                    false) String uuid) {
        return ResponseEntity.ok(queryTaskCenterDetailService.getTaskCenterDetails(corpId, memberId, taskUuid, uuid));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "修改任务的状态", nickname = "changeTaskCenterMemberStatus", notes = "", response =
            BaseResponse.class)
    @RequestMapping(value = {"/status"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity changeTaskCenterMemberStatus(
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value = "corp_id", required =
                    true) String corpId,
            @ApiParam(value = "修改任务的状态", required = true) @RequestBody @Valid WeComChangeStatusRequest request) {
        return ResponseEntity.ok(queryTaskCenterDetailService.changeTaskCenterMemberStatus(corpId, request));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComTaskCenterContentClientResponse.class)
    })
    @ApiOperation(value = "获取侧边栏详情", nickname = "getSideBarDetail", notes = "", response =
            WeComTaskCenterDetailClientResponse.class)
    @RequestMapping(value = {"/detail"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity getSideBarDetail(
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value = "corp_id", required =
                    true) String corpId,
            @ApiParam(value = "内容类型; TASK_CENTER_SEND_CONTENT 任务中心的发送内容;", required = false) @Valid @RequestParam(value =
                    "content_type", required = false) String contentType,
            @ApiParam(value = "员工ID", required = false) @Valid @RequestParam(value = "member_id", required = false) String memberId,
            @ApiParam(value = "任务ID", required = true) @RequestParam("task_uuid") String taskUuid) {
        return ResponseEntity.ok(sidebarManagerService.getSidebarContent(corpId, contentType, memberId, taskUuid));
    }
}
