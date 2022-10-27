package com.easy.marketgo.web.controller;

import com.easy.marketgo.web.model.request.WeComAgentMessageRequest;
import com.easy.marketgo.web.model.request.WeComCorpMessageRequest;
import com.easy.marketgo.web.model.response.BaseResponse;
import com.easy.marketgo.web.model.response.corp.WeComCorpCallbackResponse;
import com.easy.marketgo.web.model.response.corp.WeComCorpConfigResponse;
import com.easy.marketgo.web.model.response.customer.WeComGroupChatsResponse;
import com.easy.marketgo.web.service.wecom.CorpMessageService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/19/22 9:01 PM
 * Describe:
 */
@Api(value = "企业信息和应用管理", tags = "企业信息和应用管理")
@RestController
@RequestMapping(value = "/mktgo/wecom/corp")
public class WeComCorpMessageController {

    @Autowired
    private CorpMessageService corpMessageService;

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComGroupChatsResponse.class)
    })
    @ApiOperation(value = "创建企业信息", nickname = "updateOrInsertCorpMessage", notes = "", response = BaseResponse.class)
    @RequestMapping(value = {"/save"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity updateOrInsertCorpMessage(@NotNull @Valid @RequestParam(value = "project_id", required =
            true) String projectId, @ApiParam(value = "企微配置企业信息", required = true) @RequestBody @Valid WeComCorpMessageRequest weComCorpMessageRequest) {
        return ResponseEntity.ok(corpMessageService.updateOrInsertCorpMessage(projectId, weComCorpMessageRequest));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComGroupChatsResponse.class)
    })
    @ApiOperation(value = "检查应用信息", nickname = "checkAgentMessage", notes = "", response = BaseResponse.class)
    @RequestMapping(value = {"/check_agent"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity checkAgentMessage(@NotNull @Valid @RequestParam(value = "project_id", required =
            true) String projectId,
                                            @NotNull @Valid @RequestParam(value = "corp_id", required = true) String corpId,
                                            @NotNull @Valid @RequestParam(value = "agent_id", required = true) String agentId,
                                            @NotNull @Valid @RequestParam(value = "secret", required = true) String secret) {
        return ResponseEntity.ok(corpMessageService.checkAgentParams(projectId, corpId, agentId, secret));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComGroupChatsResponse.class)
    })
    @ApiOperation(value = "企业微信添加内建应用", nickname = "updateOrInsertAgentMessage", notes = "", response =
            WeComCorpCallbackResponse.class)
    @RequestMapping(value = {"/agent_save"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity updateOrInsertAgentMessage(@NotNull @Valid @RequestParam(value = "project_id", required =
            true) String projectId, @ApiParam(value = "企微配置企业信息", required = true) @RequestBody @Valid WeComAgentMessageRequest agentMessageRequest) {
        return ResponseEntity.ok(corpMessageService.updateOrInsertAgentMessage(projectId, agentMessageRequest));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComGroupChatsResponse.class)
    })
    @ApiOperation(value = "获取callback配置信息", nickname = "checkAgentMessage", notes = "", response =
            WeComCorpCallbackResponse.class)
    @RequestMapping(value = {"/get_config"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity getCallbackConfig(@NotNull @Valid @RequestParam(value = "project_id", required = true) String projectId,
                                            @NotNull @Valid @RequestParam(value = "corp_id", required = true) String corpId,
                                            @NotNull @Valid @RequestParam(value = "config_type", required = true) String configType) {
        return ResponseEntity.ok(corpMessageService.getCallbackConfig(projectId, configType, corpId));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComCorpConfigResponse.class)
    })
    @ApiOperation(value = "获取企业配置信息", nickname = "getCorpConfig", notes = "", response =
            WeComCorpConfigResponse.class)
    @RequestMapping(value = {"/config"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity getCorpConfig(@NotNull @Valid @RequestParam(value = "project_id", required = true) String projectId) {
        return ResponseEntity.ok(corpMessageService.getCorpConfig(projectId));
    }
}
