package com.easy.marketgo.web.controller.channellivecode;

import com.easy.marketgo.web.annotation.TokenIgnore;
import com.easy.marketgo.web.controller.BaseController;
import com.easy.marketgo.web.model.request.contactway.ChannelLiveCodeCreateRequest;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.web.model.response.contactway.ChannelLiveCodeDetailResponse;
import com.easy.marketgo.web.model.response.contactway.ChannelLiveCodeResponse;
import com.easy.marketgo.web.model.response.contactway.ChannelLiveCodeStatisticsResponse;
import com.easy.marketgo.web.model.response.contactway.ChannelLiveCodeStatisticsSummaryResponse;
import com.easy.marketgo.web.service.channellivecode.ChannelLiveCodeService;
import com.easy.marketgo.web.service.channellivecode.ChannelLiveCodeStatisticsService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-23 13:42:41
 * @description : ChannelContactWayController.java
 */
@Api(value = "渠道活码", tags = "渠道活码")
@RestController
@RequestMapping(value = "/mktgo/wecom/live_code")
public class ChannelLiveCodeController extends BaseController {

    @Autowired
    private ChannelLiveCodeService channelLiveCodeService;
    @Autowired
    private ChannelLiveCodeStatisticsService statisticsService;

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = ChannelLiveCodeResponse.class)
    })
    @ApiOperation(value = "列表", nickname = "list", notes = "", response =
            BaseResponse.class)
    @GetMapping(value = {"/list"}, produces = {"application/json"})
    public ResponseEntity list(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value =
                    "corp_id", required = true) String corpId,
            @ApiParam(value = "页码", required = true) @NotNull @Min(1) @Valid @RequestParam(value = "page_num",
                    required = true, defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页条数", required = true) @NotNull @Min(1) @Max(1000) @Valid @RequestParam(value =
                    "page_size", required = true, defaultValue = "20") Integer pageSize,
            @ApiParam(value = "活码名称", required = false) @Valid @RequestParam(value = "keyword", required = false) String keyword) {
        return ResponseEntity.ok().body(BaseResponse.success(channelLiveCodeService.queryListByPage(projectId, corpId
                , pageNum, pageSize, keyword)));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = ChannelLiveCodeResponse.class)
    })
    @ApiOperation(value = "创建活码", nickname = "create", notes = "", response =
            BaseResponse.class)
    @PostMapping(value = {"/create"}, produces = {"application/json"})
    public ResponseEntity create(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value =
                    "corp_id", required = true) String corpId,
            @RequestBody @Validated ChannelLiveCodeCreateRequest request) {

        return ResponseEntity.ok().body(channelLiveCodeService.create(projectId, corpId, request));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = ChannelLiveCodeStatisticsSummaryResponse.class)
    })
    @ApiOperation(value = "活码数据详情汇总", nickname = "statisticsSummary", notes = "", response =
            BaseResponse.class)
    @GetMapping(value = {"/statistics/summary"}, produces = {"application/json"})
    public ResponseEntity statisticsSummary(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value =
                    "corp_id", required = true) String corpId,
            @ApiParam(value = "渠道活码的uuid", required = true) @NotNull @Valid @RequestParam(value = "channel_uuid",
                    required = true) String channelUuid) {

        return ResponseEntity.ok().body(statisticsService.summary(projectId, corpId,
                channelUuid));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = ChannelLiveCodeStatisticsResponse.class)
    })
    @ApiOperation(value = "活码数据统计详情", nickname = "statisticsList", notes = "", response =
            BaseResponse.class)
    @GetMapping(value = {"/statistics/list"}, produces = {"application/json"})
    public ResponseEntity statisticsList(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value =
                    "corp_id", required = true) String corpId,
            @ApiParam(value = "渠道活码的uuid", required = true) @NotNull @Valid @RequestParam(value = "channel_uuid",
                    required = true) String channelUuid,
            @ApiParam(value = "页码", required = true) @NotNull @Min(1) @Valid @RequestParam(value = "page_num",
                    required = true, defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页条数", required = true) @NotNull @Min(1) @Max(1000) @Valid @RequestParam(value =
                    "page_size", required = true, defaultValue = "20") Integer pageSize,
            @ApiParam(value = "统计类型。 member 按照员工； date 日期", required = true) @NotNull @Valid @RequestParam(value =
                    "statistics_type", required = true) String statisticsType,
            @ApiParam(value = "开始时间", required = false) @Valid @RequestParam(value = "start_time", required = false) String startTime,
            @ApiParam(value = "结束时间", required = false) @Valid @RequestParam(value = "end_time", required = false) String endTime) {

        return ResponseEntity.ok().body(statisticsService.statisticsList(projectId, corpId, channelUuid, pageNum,
                pageSize, statisticsType, startTime, endTime));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "渠道活码检查名称是否存在", nickname = "checkMassTaskName", notes = "", response =
            BaseResponse.class)
    @RequestMapping(value = {"/check_name"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity checkChannelName(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value =
                    "corp_id", required = true) String corpId,
            @ApiParam(value = "渠道活码的中文名", required = true) @NotNull @Valid @RequestParam(value = "channel_name",
                    required
                            = true) String channelName,
            @ApiParam(value = "渠道ID") @Valid @RequestParam(value = "channel_id", required = false) Integer channelId) {
        return ResponseEntity.ok(channelLiveCodeService.checkChannelName(projectId, corpId, channelId, channelName));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "下载二维码", nickname = "downloadQrcode", notes = "", response =
            BaseResponse.class)
    @RequestMapping(value = {"/qrcode_download"}, produces = {"application/json"}, method = RequestMethod.GET)
    @TokenIgnore
    public ResponseEntity downloadQrCode(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value =
                    "corp_id", required = true) String corpId,
            @ApiParam(value = "渠道活码的uuid", required = true) @NotNull @Valid @RequestParam(value = "channel_uuid",
                    required
                            = true) String channelUuid, HttpServletResponse httpServletResponse) {
        return ResponseEntity.ok(channelLiveCodeService.downloadQrCode(projectId, corpId, channelUuid,
                httpServletResponse));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = ChannelLiveCodeDetailResponse.class)
    })
    @ApiOperation(value = "活码详情", nickname = "getLiveCodeDetail", notes = "", response =
            ChannelLiveCodeDetailResponse.class)
    @RequestMapping(value = {"/detail"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity getLiveCodeDetail(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value =
                    "corp_id", required = true) String corpId,
            @ApiParam(value = "渠道活码的uuid", required = true) @NotNull @Valid @RequestParam(value = "channel_uuid",
                    required
                            = true) String channelUuid) {
        return ResponseEntity.ok(channelLiveCodeService.getLiveCodeDetail(projectId, corpId, channelUuid));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "删除活码", nickname = "deleteLiveCode", notes = "", response = BaseResponse.class)
    @RequestMapping(value = {"/delete"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity deleteLiveCode(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value =
                    "corp_id", required = true) String corpId,
            @ApiParam(value = "渠道活码的uuid", required = true) @NotNull @Valid @RequestParam(value = "channel_uuid",
                    required = true) String channelId) {
        return ResponseEntity.ok(channelLiveCodeService.deleteLiveCode(projectId, corpId, channelId));
    }
}
