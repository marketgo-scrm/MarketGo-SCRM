package com.easy.marketgo.web.controller;

import com.easy.marketgo.web.model.request.MediaUploadRequest;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.web.model.response.media.MediaUploadResponse;
import com.easy.marketgo.web.service.wecom.MediaUploadService;
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
 * @data : 6/19/22 9:00 PM
 * Describe:
 */
@Api(value = "素材管理", tags = "素材管理")
@RestController
@RequestMapping(value = "/mktgo/wecom/media")
public class WeComMediaController {
    @Autowired
    private MediaUploadService mediaUploadService;

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = MediaUploadResponse.class)
    })
    @ApiOperation(value = "上传企微的素材信息", nickname = "uploadWeComMedia", notes = "", response = MediaUploadResponse.class)
    @PostMapping("/upload")
    public ResponseEntity uploadWeComMedia(
            @ApiParam(value = "企微项目uuid", required = true) @RequestParam("project_id") @NotBlank @Valid String projectId,
            @ApiParam(value = "素材数据", required = true) @RequestParam("file") @NotNull @Valid MultipartFile multipartFile,
            @ApiParam(value = "素材类型 IMAGE,VIDEO,FILE,MINIPROGRAM 小程序,LINK 网页,VOICE, LOGO", required = true,
                    allowableValues =
                    "IMAGE,VIDEO,FILE,MINIPROGRAM,LINK,VOICE, LOGO") @RequestParam(
                    "media_type") @NotBlank @Valid String mediaType,
            @ApiParam(value = "任务类型; SINGLE 群发好友; GROUP 群发客户群; MOMENT 群发朋友圈; , LIVE_CODE活码", required = true,
                    allowableValues =
                    "SINGLE, GROUP, MOMENT, LIVE_CODE") @RequestParam(
                    "task_type") @NotBlank @Valid String taskType,
            @ApiParam(value = "企业id", required = true) @RequestParam("corp_id") @NotBlank @Valid String corpId) {

        MediaUploadRequest request = new MediaUploadRequest();
        request.setMultipartFile(multipartFile);
        request.setMediaType(mediaType);
        request.setTaskType(taskType);
        return ResponseEntity.ok(mediaUploadService.uploadMedia(projectId, corpId, request));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "删除企微的素材信息", nickname = "uploadWeComMedia", notes = "", response = BaseResponse.class)
    @RequestMapping(value = {"/delete"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity deleteWeComMedia(
            @ApiParam(value = "企微项目uuid", required = true) @RequestParam("project_id") @NotBlank @Valid String projectId,
            @ApiParam(value = "素材uuid", required = true) @RequestParam("media_uuid") @NotBlank @Valid String mediaUuid,
            @ApiParam(value = "企业id", required = true) @RequestParam("corp_id") @NotBlank @Valid String corpId) {

        return ResponseEntity.ok(mediaUploadService.deleteMedia(projectId, corpId, mediaUuid));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = MediaUploadResponse.class)
    })
    @ApiOperation(value = "查询企微的素材信息", nickname = "getWeComMedia", notes = "", response = MediaUploadResponse.class)
    @RequestMapping(value = {"/get_media"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity getWeComMedia(
            @ApiParam(value = "企微项目uuid", required = true) @RequestParam("project_id") @NotBlank @Valid String projectId,
            @ApiParam(value = "素材uuid", required = true) @RequestParam("media_uuid") @NotBlank @Valid String mediaUuid,
            @ApiParam(value = "企业id", required = true) @RequestParam("corp_id") @NotBlank @Valid String corpId) {

        return ResponseEntity.ok(mediaUploadService.queryMedia(projectId, corpId, mediaUuid));
    }
}
