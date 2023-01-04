package com.easy.marketgo.web.controller;

import com.easy.marketgo.web.model.request.CdpManufacturerMessageRequest;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.web.model.response.cdp.*;
import com.easy.marketgo.web.service.wecom.CdpManufacturerSettingService;
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
 * @data : 11/21/22 3:01 PM
 * Describe:
 */
@Api(value = "CDP厂商设置", tags = "CDP厂商设置")
@RestController
@RequestMapping(value = "/mktgo/wecom/cdp")
class CdpManufacturerSettingController {

    @Autowired
    private CdpManufacturerSettingService cdpManufacturerSettingService;


    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = CdpManufactureListResponse.class)
    })
    @ApiOperation(value = "获取cdp列表信息", nickname = "queryCdpList", notes = "", response =
            CdpManufactureListResponse.class)
    @RequestMapping(value = {"/list"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity queryCdpList(@ApiParam(value = "企微项目id", required = true) @RequestParam(value =
            "project_id", required = true) @NotBlank @Valid String projectId,
                                          @ApiParam(value = "企业id", required = true) @RequestParam(value = "corp_id",
                                                  required = true) @NotBlank @Valid String corpId) {

        return ResponseEntity.ok(cdpManufacturerSettingService.queryCdpList(projectId, corpId));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "保存和修改cdp信息", nickname = "saveOrUpdateCdpMessage", notes = "", response =
            BaseResponse.class)
    @RequestMapping(value = {"/save"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity saveOrUpdateCdpMessage(
            @ApiParam(value = "企微项目id", required = true) @RequestParam(value = "project_id", required = true) @NotBlank @Valid String projectId,
            @ApiParam(value = "企业id", required = true) @RequestParam(value = "corp_id", required = true) @NotBlank @Valid String corpId,
            @ApiParam(value = "cdp的信息", required = true) @RequestBody @NotNull @Valid CdpManufacturerMessageRequest request,
            @ApiParam(value = "CDP厂商类型 SENSORS 神策, ANALYSYS 易观, LINKFLOW LinkFlow, CONVERTLAB ConvertLab, HYPERS " +
                    "hypers, GROWINGIO GrowingIO, ALICLOUD 阿里云, TENCENTCLOUD 腾讯云 ", required = true) @RequestParam(value = "cdp_type", required = true) @NotBlank @Valid String cdpType) {

        return ResponseEntity.ok(cdpManufacturerSettingService.saveOrUpdateCdpMessage(projectId, corpId, cdpType,
                request));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = CdpManufacturerMessageResponse.class)
    })
    @ApiOperation(value = "获取cdp信息", nickname = "queryCdpMessage", notes = "", response =
            CdpManufacturerMessageResponse.class)
    @RequestMapping(value = {"/query"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity queryCdpMessage(@ApiParam(value = "企微项目id", required = true) @RequestParam(value =
            "project_id", required = true) @NotBlank @Valid String projectId,
                                          @ApiParam(value = "企业id", required = true) @RequestParam(value = "corp_id",
                                                  required = true) @NotBlank @Valid String corpId,
                                          @ApiParam(value = "CDP厂商类型 SENSORS 神策, ANALYSYS 易观, LINKFLOW " +
                                                  "LinkFlow, CONVERTLAB ConvertLab, HYPERS hypers, GROWINGIO " +
                                                  "GrowingIO, ALICLOUD 阿里云, TENCENTCLOUD 腾讯云 ",
                                                  required = true) @RequestParam(value = "cdp_type",
                                                  required = true) @NotBlank @Valid String cdpType) {

        return ResponseEntity.ok(cdpManufacturerSettingService.queryCdpMessage(projectId, corpId, cdpType));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "修改cdp的状态", nickname = "changeCdpStatus", notes = "", response =
            BaseResponse.class)
    @PostMapping("/status")
    public ResponseEntity changeCdpStatus(@ApiParam(value = "企微项目id", required = true) @RequestParam(value =
            "project_id", required = true) @NotBlank @Valid String projectId,
                                          @ApiParam(value = "企业id", required = true) @RequestParam(value =
                                                  "corp_id", required = true) @NotBlank @Valid String corpId,
                                          @ApiParam(value = "CDP厂商类型 SENSORS 神策, ANALYSYS 易观, LINKFLOW " +
                                                  "LinkFlow, CONVERTLAB ConvertLab, HYPERS hypers, GROWINGIO " +
                                                  "GrowingIO, ALICLOUD 阿里云, TENCENTCLOUD 腾讯云 ",
                                                  required = true) @RequestParam(value = "cdp_type",
                                                  required = true) @NotBlank @Valid String cdpType,
                                          @ApiParam(value = "cdp状态", required = true) @RequestParam(value =
                                                  "status", required = true) @NotBlank @Valid Boolean status) {

        return ResponseEntity.ok(cdpManufacturerSettingService.changeCdpStatus(projectId, corpId, cdpType, status));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "删除cdp的状态", nickname = "deleteCdpMessage", notes = "", response = BaseResponse.class)
    @RequestMapping(value = {"/delete"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity deleteCdpMessage(
            @ApiParam(value = "企微项目id", required = true) @RequestParam(value = "project_id", required = true) @NotBlank @Valid String projectId,
            @ApiParam(value = "企业id", required = true) @RequestParam(value = "corp_id", required = true) @NotBlank @Valid String corpId,
            @ApiParam(value = "CDP厂商类型 SENSORS 神策, ANALYSYS 易观, LINKFLOW " +
                    "LinkFlow, CONVERTLAB ConvertLab, HYPERS hypers, GROWINGIO " +
                    "GrowingIO, ALICLOUD 阿里云, TENCENTCLOUD 腾讯云 ",
                    required = true) @RequestParam(value = "cdp_type",
                    required = true) @NotBlank @Valid String cdpType) {
        return ResponseEntity.ok(cdpManufacturerSettingService.deleteCdpMessage(projectId, corpId, cdpType));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = CdpSettingTestStatusResponse.class)
    })
    @ApiOperation(value = "测试CDP信息", nickname = "CdpSettingTest", notes = "", response =
            CdpSettingTestStatusResponse.class)
    @RequestMapping(value = {"/test"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity CdpSettingTest(
            @ApiParam(value = "企微项目id", required = true) @RequestParam(value = "project_id", required = true) @NotBlank @Valid String projectId,
            @ApiParam(value = "企业id", required = true) @RequestParam(value = "corp_id", required = true) @NotBlank @Valid String corpId,
            @ApiParam(value = "cdp的信息", required = true) @RequestBody @NotNull @Valid CdpManufacturerMessageRequest request,
            @ApiParam(value = "CDP厂商类型 SENSORS 神策, ANALYSYS 易观, LINKFLOW LinkFlow, CONVERTLAB ConvertLab, HYPERS " +
                    "hypers, GROWINGIO GrowingIO, ALICLOUD 阿里云, TENCENTCLOUD 腾讯云 ", required = true) @RequestParam(value = "cdp_type", required = true) @NotBlank @Valid String cdpType) {

        return ResponseEntity.ok(cdpManufacturerSettingService.cdpSettingTest(projectId, corpId, cdpType,
                request));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = CdpSwitchStatusResponse.class)
    })
    @ApiOperation(value = "获取CDP设置是否开启的状态", nickname = "queryCdpCrowdStatus", notes = "", response =
            CdpSwitchStatusResponse.class)
    @RequestMapping(value = {"/switch/status"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity queryCdpCrowdStatus(
            @ApiParam(value = "企微项目id", required = true) @RequestParam(value = "project_id", required = true) @NotBlank @Valid String projectId,
            @ApiParam(value = "企业id", required = true) @RequestParam(value = "corp_id",
                    required = true) @NotBlank @Valid String corpId) {

        return ResponseEntity.ok(cdpManufacturerSettingService.cdpSettingStatus(projectId, corpId));
    }
}
