package com.easy.marketgo.web.controller.welcomemsg;

import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.web.controller.BaseController;
import com.easy.marketgo.web.model.request.welcomemsg.WelcomeMsgGroupChatSaveRequest;
import com.easy.marketgo.web.model.response.contactway.ChannelLiveCodeDetailResponse;
import com.easy.marketgo.web.model.response.contactway.ChannelLiveCodeResponse;
import com.easy.marketgo.web.model.response.welcomemsg.WelcomeMsgGroupChatDetailResponse;
import com.easy.marketgo.web.model.response.welcomemsg.WelcomeMsgGroupChatListResponse;
import com.easy.marketgo.web.service.welcomemsg.GroupChatWelcomeMsgService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 4/21/23 6:08 PM
 * Describe:
 */

@Api(value = "客户群欢迎语", tags = "客户群欢迎语")
@RestController
@RequestMapping(value = "/mktgo/wecom/welcome/group_chat")
public class GroupChatJoinWelcomeMsgController extends BaseController {

    @Autowired
    private GroupChatWelcomeMsgService groupChatWelcomeMsgService;

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WelcomeMsgGroupChatListResponse.class)
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
            @ApiParam(value = "标题", required = false) @Valid @RequestParam(value = "keyword", required = false) String keyword) {
        return ResponseEntity.ok().body(groupChatWelcomeMsgService.queryWelcomeMsgList(projectId, corpId
                , pageNum, pageSize, keyword));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = ChannelLiveCodeResponse.class)
    })
    @ApiOperation(value = "创建客户群欢迎语", nickname = "createOrUpdate", notes = "", response =
            BaseResponse.class)
    @PostMapping(value = {"/save"}, produces = {"application/json"})
    public ResponseEntity createOrUpdate(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value =
                    "corp_id", required = true) String corpId,
            @RequestBody @Validated WelcomeMsgGroupChatSaveRequest request) {

        return ResponseEntity.ok().body(groupChatWelcomeMsgService.createOrUpdate(projectId, corpId, request));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "检查名称是否存在", nickname = "checkTitle", notes = "", response =
            BaseResponse.class)
    @RequestMapping(value = {"/check_name"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity checkTitle(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value =
                    "corp_id", required = true) String corpId,
            @ApiParam(value = "客户群欢迎语的中文名", required = true) @NotNull @Valid @RequestParam(value = "name",
                    required
                            = true) String name,
            @ApiParam(value = "客户群欢迎语ID") @Valid @RequestParam(value = "id", required = false) Integer id) {
        return ResponseEntity.ok(groupChatWelcomeMsgService.checkTitle(projectId, corpId, id, name));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WelcomeMsgGroupChatDetailResponse.class)
    })
    @ApiOperation(value = "客户群欢迎语详情", nickname = "getWelcomeMsgDetail", notes = "", response =
            ChannelLiveCodeDetailResponse.class)
    @RequestMapping(value = {"/detail"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity getWelcomeMsgDetail(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value =
                    "corp_id", required = true) String corpId,
            @ApiParam(value = "客户群欢迎语uuid", required = true) @NotNull @Valid @RequestParam(value = "uuid",
                    required
                            = true) String uuid) {
        return ResponseEntity.ok(groupChatWelcomeMsgService.getWelcomeMsgDetail(projectId, corpId, uuid));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "删除客户群欢迎语", nickname = "deleteWelcomeMsg", notes = "", response = BaseResponse.class)
    @RequestMapping(value = {"/delete"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity deleteWelcomeMsg(
            @ApiParam(value = "企微项目uuid", required = true) @NotNull @Valid @RequestParam(value = "project_id",
                    required = true) String projectId,
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value =
                    "corp_id", required = true) String corpId,
            @ApiParam(value = "客户群欢迎语uuid", required = true) @NotNull @Valid @RequestParam(value = "uuid",
                    required = true) String uuid) {
        return ResponseEntity.ok(groupChatWelcomeMsgService.deleteWelcomeMsg(projectId, corpId, uuid));
    }
}
