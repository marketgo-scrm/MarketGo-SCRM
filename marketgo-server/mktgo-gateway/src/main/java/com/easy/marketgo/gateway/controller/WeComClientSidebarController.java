package com.easy.marketgo.gateway.controller;

import com.easy.marketgo.gateway.wecom.request.client.WeComTaskCenterContentClientResponse;
import com.easy.marketgo.gateway.wecom.request.client.WeComTaskCenterDetailClientResponse;
import com.easy.marketgo.gateway.wecom.sevice.SidebarManagerService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
    private SidebarManagerService sidebarManagerService;

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComTaskCenterContentClientResponse.class)
    })
    @ApiOperation(value = "获取侧边栏详情", nickname = "getSideBarDetail", notes = "", response =
            WeComTaskCenterDetailClientResponse.class)
    @RequestMapping(value = {"/detail"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity getSideBarDetail(
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value = "corp_id", required =
                    true) String corpId,
            @ApiParam(value = "内容类型; TASK_CENTER_SEND_CONTENT 任务中心的发送内容;", required = true, allowableValues =
                    "TASK_CENTER_SEND_CONTENT") @Valid @RequestParam(value =
                    "content_type", required = true, defaultValue = "TASK_CENTER_SEND_CONTENT") String contentType,
            @ApiParam(value = "员工ID", required = false) @Valid @RequestParam(value = "member_id", required = false) String memberId,
            @ApiParam(value = "任务ID", required = false) @RequestParam(value = "task_uuid", required = false) String taskUuid) {

        return ResponseEntity.ok(sidebarManagerService.getSidebarContent(corpId, contentType, memberId, taskUuid));
    }
}
