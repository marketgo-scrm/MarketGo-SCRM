package com.easy.marketgo.web.controller;

import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.web.model.request.WeComAgentMessageRequest;
import com.easy.marketgo.web.model.request.WeComCorpMessageRequest;
import com.easy.marketgo.web.model.request.WeComForwardServerMessageRequest;
import com.easy.marketgo.web.model.response.corp.*;
import com.easy.marketgo.web.model.response.customer.WeComGroupChatsResponse;
import com.easy.marketgo.web.service.wecom.CorpMessageService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
            @ApiResponse(code = 0, message = "ok", response = WeComCorpCallbackResponse.class)
    })
    @ApiOperation(value = "获取callback配置信息", nickname = "checkAgentMessage", notes = "", response =
            WeComCorpCallbackResponse.class)
    @RequestMapping(value = {"/callback/config"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity getCallbackConfig(@NotNull @Valid @RequestParam(value = "project_id", required = true) String projectId,
                                            @NotNull @Valid @RequestParam(value = "corp_id", required = true) String corpId,
                                            @ApiParam(value = "callback类型; CONTACTS 通讯录; EXTERNAL_USER 客户",
                                                    required = true, allowableValues =
                                                    "CONTACTS, EXTERNAL_USER") @RequestParam("config_type") @NotBlank @Valid String configType) {
        return ResponseEntity.ok(corpMessageService.getCallbackConfig(projectId, corpId, configType));
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

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "企业微信添加转发服务", nickname = "updateOrInsertForwardServer", notes = "", response =
            BaseResponse.class)
    @RequestMapping(value = {"/forward/save"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity updateOrInsertForwardServer(
            @NotNull @Valid @RequestParam(value = "project_id", required = true) String projectId,
            @NotNull @Valid @RequestParam(value = "corp_id", required = true) String corpId,
            @ApiParam(value = "callback类型; CONTACTS 通讯录; EXTERNAL_USER 客户",
                    required = true, allowableValues =
                    "CONTACTS, EXTERNAL_USER") @RequestParam(value = "config_type", defaultValue = "CONTACTS") @NotBlank @Valid String configType,
            @ApiParam(value = "企微配置转发服务信息", required = true) @RequestBody @Valid WeComForwardServerMessageRequest request) {
        return ResponseEntity.ok(corpMessageService.updateOrInsertForwardServer(projectId, corpId, configType,
                request));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComForwardServerMessageResponse.class)
    })
    @ApiOperation(value = "获取企业微信转发服务配置信息", nickname = "getForwardServer", notes = "", response =
            WeComForwardServerMessageResponse.class)
    @RequestMapping(value = {"/forward/config"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity getForwardServer(@NotNull @Valid @RequestParam(value = "project_id", required = true) String projectId,
                                           @NotNull @Valid @RequestParam(value = "corp_id", required = true) String corpId,
                                           @ApiParam(value = "callback类型; CONTACTS 通讯录; EXTERNAL_USER 客户",
                                                   required = true, allowableValues =
                                                   "CONTACTS, EXTERNAL_USER") @RequestParam(value = "config_type",
                                                   defaultValue = "CONTACTS") @NotBlank @Valid String configType) {
        return ResponseEntity.ok(corpMessageService.getForwardServer(projectId, corpId, configType));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComSidebarMessageResponse.class)
    })
    @ApiOperation(value = "获取企业微信侧边栏配置信息", nickname = "getSidebarServer", notes = "", response =
            WeComForwardServerMessageResponse.class)
    @RequestMapping(value = {"/sidebar/config"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity getSidebarServer(@NotNull @Valid @RequestParam(value = "project_id", required = true) String projectId,
                                           @NotNull @Valid @RequestParam(value = "corp_id", required = true) String corpId) {
        return ResponseEntity.ok(corpMessageService.getSidebarServer(projectId, corpId));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "上传企微的可信文件", nickname = "verifyCredFile", notes = "", response = BaseResponse.class)
    @PostMapping("/cred/upload")
    public ResponseEntity verifyCredFile(
            @ApiParam(value = "企微项目uuid", required = true) @RequestParam("project_id") @NotBlank @Valid String projectId,
            @ApiParam(value = "可信文件", required = true) @RequestParam("file") @NotNull @Valid MultipartFile multipartFile,
            @ApiParam(value = "企业id", required = true) @RequestParam("corp_id") @NotBlank @Valid String corpId) {

        return ResponseEntity.ok(corpMessageService.verifyCredFile(projectId, corpId, multipartFile));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComCorpDomainResponse.class)
    })
    @ApiOperation(value = "获取企微的可信域名", nickname = "queryDomainUrl", notes = "", response =
            WeComCorpDomainResponse.class)
    @RequestMapping(value = {"/domain/query"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity queryDomainUrl(
            @ApiParam(value = "企微项目uuid", required = true) @RequestParam("project_id") @NotBlank @Valid String projectId,
            @ApiParam(value = "企业id", required = true) @RequestParam("corp_id") @NotBlank @Valid String corpId) {

        return ResponseEntity.ok(corpMessageService.queryDomainUrl(projectId, corpId));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "删除可信文件", nickname = "deleteCredFile", notes = "", response = BaseResponse.class)
    @RequestMapping(value = {"/cred_file/delete"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity deleteCredFile(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企业id", required = true) @RequestParam("corp_id") @NotBlank @Valid String corpId,
            @ApiParam(value = "可信文件名称", required = true) @RequestParam("file_name") @NotBlank @Valid String fileName) {
        return ResponseEntity.ok(corpMessageService.deleteCredFile(projectId, corpId, fileName));
    }
}
