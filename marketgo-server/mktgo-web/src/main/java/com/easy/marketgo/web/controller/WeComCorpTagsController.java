package com.easy.marketgo.web.controller;

import com.easy.marketgo.web.model.request.tags.WeComAddCorpTagRequest;
import com.easy.marketgo.web.model.request.tags.WeComDeleteCorpTagRequest;
import com.easy.marketgo.web.model.request.tags.WeComEditCorpTagRequest;
import com.easy.marketgo.web.model.request.tags.WeComMarkCorpTagsRequest;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.web.model.response.WeComCoreTagsResponse;
import com.easy.marketgo.web.service.wecom.CorpTagsManagerService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/21/22 3:01 PM
 * Describe:
 */
@Api(value = "企业标签管理", tags = "企业标签管理")
@RestController
@RequestMapping(value = "/mktgo/wecom/corp_tags")
class WeComCorpTagsController {

    @Autowired
    private CorpTagsManagerService corpTagsManagerService;

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComCoreTagsResponse.class)
    })
    @ApiOperation(value = "获取企业标签信息", nickname = "getCorpTags", notes = "", response = WeComCoreTagsResponse.class)
    @RequestMapping(value = {"/list"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity getCorpTags(@ApiParam(value = "企微项目id", required = true) @NotNull @Valid @RequestParam(value = "project_id", required = true) String projectId,
                                      @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value = "corp_id", required = true) String corpId,
                                      @ApiParam(value = "标签名称", required = false) @Valid @RequestParam(value =
                                              "keyword", required = false) String keyword,
                                      @ApiParam(value = "同步状态,async异步， sync 同步从企微请求", required = true) @NotNull @Valid @RequestParam(value =
                                              "sync_state", required = true, defaultValue = "async") String syncStatus) {
        return ResponseEntity.ok(corpTagsManagerService.listCorpTags(projectId, corpId, keyword, syncStatus));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "添加企业标签", nickname = "addCorpTags", notes = "", response =
            BaseResponse.class)
    @RequestMapping(value = {"/add"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity addCorpTags(
            @ApiParam(value = "企微项目id", required = true) @RequestParam(value =
                    "project_id", required = true) @NotBlank @Valid String projectId,
            @ApiParam(value = "企业id", required = true) @RequestParam(value = "corp_id",
                    required = true) @NotBlank @Valid String corpId,
            @ApiParam(value = "添加标签组和标签", required = true) @RequestBody @NotNull @Valid WeComAddCorpTagRequest request) {

        return ResponseEntity.ok(corpTagsManagerService.addCorpTags(projectId, corpId, request));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "标记企业标签", nickname = "markCorpTags", notes = "", response =
            BaseResponse.class)
    @RequestMapping(value = {"/mark"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity markCorpTags(
            @ApiParam(value = "企微项目id", required = true) @RequestParam(value =
                    "project_id", required = true) @NotBlank @Valid String projectId,
            @ApiParam(value = "企业id", required = true) @RequestParam(value = "corp_id",
                    required = true) @NotBlank @Valid String corpId,
            @ApiParam(value = "客户标记标签", required = true) @RequestBody @NotNull @Valid WeComMarkCorpTagsRequest request) {

        return ResponseEntity.ok(corpTagsManagerService.markCorpTags(projectId, corpId, request));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "编辑企业标签", nickname = "editCorpTags", notes = "", response =
            BaseResponse.class)
    @RequestMapping(value = {"/edit"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity editCorpTags(
            @ApiParam(value = "企微项目id", required = true) @RequestParam(value =
                    "project_id", required = true) @NotBlank @Valid String projectId,
            @ApiParam(value = "企业id", required = true) @RequestParam(value = "corp_id",
                    required = true) @NotBlank @Valid String corpId,
            @ApiParam(value = "编辑标签组和标签", required = true) @RequestBody @NotNull @Valid WeComEditCorpTagRequest request) {

        return ResponseEntity.ok(corpTagsManagerService.editCorpTags(projectId, corpId, request));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "删除企业标签", nickname = "deleteCorpTags", notes = "", response =
            BaseResponse.class)
    @RequestMapping(value = {"/delete"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity deleteCorpTags(
            @ApiParam(value = "企微项目id", required = true) @RequestParam(value =
                    "project_id", required = true) @NotBlank @Valid String projectId,
            @ApiParam(value = "企业id", required = true) @RequestParam(value = "corp_id",
                    required = true) @NotBlank @Valid String corpId,
            @ApiParam(value = "删除的标签组和标签", required = true) @RequestBody @NotNull @Valid WeComDeleteCorpTagRequest request) {

        return ResponseEntity.ok(corpTagsManagerService.deleteCorpTags(projectId, corpId, request));
    }
}
