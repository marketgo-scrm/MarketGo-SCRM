package com.easy.marketgo.web.controller;

import com.easy.marketgo.web.annotation.TokenIgnore;
import com.easy.marketgo.web.model.request.UserGroupAudienceRules;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.web.model.response.cdp.CdpCrowdListResponse;
import com.easy.marketgo.web.model.response.UserGroupEstimateResponse;
import com.easy.marketgo.web.service.wecom.WeComUserGroupService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/21/22 3:01 PM
 * Describe:
 */
@Api(value = "人群管理", tags = "人群管理")
@RestController
@RequestMapping(value = "/mktgo/wecom/user_group")
class WeComUserGroupController {

    @Autowired
    private WeComUserGroupService weComUserGroupService;

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = UserGroupEstimateResponse.class)
    })
    @ApiOperation(value = "预估人群的数量", nickname = "userGroupEstimate", notes = "", response =
            UserGroupEstimateResponse.class)
    @RequestMapping(value = {"/estimate"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity userGroupEstimate(@ApiParam(value = "人群计算的条件", required = true) @RequestBody @NotNull @Valid UserGroupAudienceRules audienceRules,
                                            @ApiParam(value = "企微项目id", required = true) @RequestParam(value =
                                                    "project_id", required = true) @NotBlank @Valid String projectId,
                                            @ApiParam(value = "请求的requestID", required = true) @RequestParam(value =
                                                    "request_id", required = true) @NotBlank @Valid String requestId,
                                            @ApiParam(value = "企业id", required = true) @RequestParam(value = "corp_id",
                                                    required = true) @NotBlank @Valid String corpId,
                                            @ApiParam(value = "群发类型 SINGLE 群发好友, GROUP 群发客户群, MOMENT 群发朋友圈",
                                                    required = true) @RequestParam(value = "task_type",
                                                    required = true) @NotBlank @Valid String taskType) {

        return ResponseEntity.ok(weComUserGroupService.estimate(projectId, audienceRules, requestId, corpId, taskType));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = UserGroupEstimateResponse.class)
    })
    @ApiOperation(value = "获取人群信息", nickname = "userGroupMessage", notes = "", response =
            UserGroupAudienceRules.class)
    @RequestMapping(value = {"/query"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity queryUserGroup(@ApiParam(value = "企微项目id", required = true) @RequestParam(value =
            "project_id", required = true) @NotBlank @Valid String projectId,
                                         @ApiParam(value = "人群的uuid", required = true) @RequestParam(value =
                                                 "group_uuid", required = true) @NotBlank @Valid String groupUuid,
                                         @ApiParam(value = "企业id", required = true) @RequestParam(value = "corp_id",
                                                 required = true) @NotBlank @Valid String corpId,
                                         @ApiParam(value = "群发类型 SINGLE 群发好友, GROUP 群发客户群, MOMENT 群发朋友圈",
                                                 required = true) @RequestParam(value = "task_type",
                                                 required = true) @NotBlank @Valid String taskType) {

        return ResponseEntity.ok(weComUserGroupService.queryUserGroup(projectId, corpId, taskType, groupUuid));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "上传离线人群", nickname = "uploadOfflineUserGroup", notes = "", response =
            BaseResponse.class)
    @PostMapping("/upload")
    public ResponseEntity uploadOfflineUserGroup(@ApiParam(value = "企微项目id", required = true) @RequestParam(value =
            "project_id", required = true) @NotBlank @Valid String projectId,
                                                 @ApiParam(value = "人群的uuid", required = true) @RequestParam(value =
                                                         "user_group_uuid", required = true) @NotBlank @Valid String groupUuid,
                                                 @ApiParam(value = "企业id", required = true) @RequestParam(value =
                                                         "corp_id", required = true) @NotBlank @Valid String corpId,
                                                 @ApiParam(value = "人群数据", required = true) @RequestParam("file") @NotNull @Valid MultipartFile multipartFile,
                                                 @ApiParam(value = "文件类型", required = true) @NotNull @Valid @RequestParam(value = "file_type",
                                                         required = true, defaultValue = "csv") String fileType) {

        return ResponseEntity.ok(weComUserGroupService.offlineUserGroup(projectId, corpId, groupUuid, fileType,
                multipartFile));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "下载excel格式", nickname = "downloadExcelTemplate", notes = "", response =
            BaseResponse.class)
    @RequestMapping(value = {"/download/template"}, produces = {"application/json"}, method = RequestMethod.GET)
    @TokenIgnore
    public ResponseEntity downloadExcelTemplate(@ApiParam(value = "企微项目id", required = true) @RequestParam(value =
            "project_id", required = true) @NotBlank @Valid String projectId,
                                                @ApiParam(value = "企业id", required = true) @RequestParam(value =
                                                        "corp_id", required = true) @NotBlank @Valid String corpId,
                                                HttpServletResponse httpServletResponse) {

        return ResponseEntity.ok(weComUserGroupService.getExcelTemplate(projectId, corpId, httpServletResponse));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "删除离线人群", nickname = "deleteOfflineUserGroup", notes = "", response = BaseResponse.class)
    @RequestMapping(value = {"/delete"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity deleteMassTask(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企业id", required = true) @RequestParam(value = "corp_id", required = true) @NotBlank @Valid String corpId,
            @ApiParam(value = "人群的uuid", required = true) @RequestParam("user_group_uuid") String groupUuid) {
        return ResponseEntity.ok(weComUserGroupService.deleteOfflineUserGroup(corpId, groupUuid));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = CdpCrowdListResponse.class)
    })
    @ApiOperation(value = "获取CDP人群信息", nickname = "cdpCrowdList", notes = "", response =
            UserGroupAudienceRules.class)
    @RequestMapping(value = {"/crowd"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity queryCdpCrowdList(
            @ApiParam(value = "企微项目id", required = true) @RequestParam(value = "project_id", required = true) @NotBlank @Valid String projectId,
            @ApiParam(value = "企业id", required = true) @RequestParam(value = "corp_id", required = true) @NotBlank @Valid String corpId,
            @ApiParam(value = "分群刷选状态", required = true) @RequestParam(value = "refresh", required = true,
                    defaultValue = "0") @NotBlank @Valid Boolean refresh) {

        return ResponseEntity.ok(weComUserGroupService.queryCrowdList(projectId, corpId));
    }
}
