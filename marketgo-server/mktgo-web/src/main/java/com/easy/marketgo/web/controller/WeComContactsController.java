package com.easy.marketgo.web.controller;

import com.easy.marketgo.web.model.response.customer.*;
import com.easy.marketgo.web.service.wecom.ContactsManagerService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/14/22 4:11 PM
 * Describe:
 */
@Api(value = "企业客户管理", tags = "企业客户管理")
@RestController
@RequestMapping(value = "/mktgo/wecom/contacts")
public class WeComContactsController {

    @Autowired
    private ContactsManagerService contactsManagerService;

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComExternalUsersResponse.class)
    })
    @ApiOperation(value = "获取客户列表", nickname = "getExternalUserList", notes = "", response =
            WeComExternalUsersResponse.class)
    @RequestMapping(value = {"/external_user/list"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity listExternalUsers(@ApiParam(value = "企微项目id", required = true) @NotNull @Valid @RequestParam(value = "project_id", required =
            true) String projectId,
                                            @ApiParam(value = "页数", required = true) @NotNull @Min(1) @Valid @RequestParam(value = "page_num", required = true,
                                                    defaultValue = "1") Integer pageNum,
                                            @ApiParam(value = "每页的条数", required = true) @NotNull @Min(1) @Max(1000) @Valid @RequestParam(value = "page_size",
                                                    required = true, defaultValue = "20") Integer pageSize,
                                            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value =
                                                    "corp_id", required = true) String corpId,
                                            @ApiParam(value = "搜索的客户名称", required = false) @NotNull @Valid @RequestParam(value = "keyword", required = false) String keyword,
                                            @ApiParam(value = "客户的流失状态 0 是好友; 1 员工删除客户; 2 客户删除员工", required = false) @NotNull @Valid @RequestParam(value = "statuses", required = false) List<Integer> statuses,
                                            @ApiParam(value = "是否去重", required = false) @NotNull @Valid @RequestParam(value = "duplicate", required = false) boolean duplicate,
                                            @ApiParam(value = "所属员工id列表", required = false) @NotNull @Valid @RequestParam(value = "member_ids", required = false) List<String> memberIds,
                                            @ApiParam(value = "所属员工部门id列表", required = false) @NotNull @Valid @RequestParam(value = "department_ids", required = false) List<Long> departments,
                                            @ApiParam(value = "标签id列表", required = false) @NotNull @Valid @RequestParam(value = "tags", required = false) List<String> tags,
                                            @ApiParam(value = "添加渠道列表", required = false) @NotNull @Valid @RequestParam(value = "channels", required = false) List<Integer> channels,
                                            @ApiParam(value = "所在群聊的列表", required = false) @NotNull @Valid @RequestParam(value = "group_chats", required = false) List<String> groupChats,
                                            @ApiParam(value = "性别 0-未知 1-男性 2-女性", required = false) @NotNull @Valid @RequestParam(value = "gender", required = false) List<Integer> genders,
                                            @ApiParam(value = "添加的开始时间", required = false) @NotNull @Valid @RequestParam(value = "start_time", required = false) String startTime,
                                            @ApiParam(value = "添加的结束时间", required = false) @NotNull @Valid @RequestParam(value = "end_time", required = false) String endTime) {
        return ResponseEntity.ok(contactsManagerService.getExternalUserList(projectId, pageNum, pageSize, corpId,
                keyword,
                statuses,
                duplicate,
                memberIds,
                departments,
                tags,
                channels,
                groupChats,
                genders,
                startTime,
                endTime));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComExternalUsersResponse.class)
    })
    @ApiOperation(value = "获取员工列表", nickname = "listMembers", notes = "", response =
            WeComExternalUsersResponse.class)
    @RequestMapping(value = {"/member/list"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity listMembers(@ApiParam(value = "企微项目id", required = true) @NotNull @Valid @RequestParam(value = "project_id", required =
            true) String projectId,
                                      @ApiParam(value = "页数", required = true) @NotNull @Min(1) @Valid @RequestParam(value = "page_num", required = true,
                                              defaultValue = "1") Integer pageNum,
                                      @ApiParam(value = "每页的条数", required = true) @NotNull @Min(1) @Max(1000) @Valid @RequestParam(value = "page_size",
                                              required = true, defaultValue = "20") Integer pageSize,
                                      @ApiParam(value = "企业的部门ID", required = false) @NotNull @Valid @RequestParam(value =
                                              "department_id", required = false) Long departmentId,
                                      @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value =
                                              "corp_id", required = true) String corpId,
                                      @ApiParam(value = "搜索的员工名称", required = false) @NotNull @Valid @RequestParam(value = "member_name", required = false) String memberName) {
        return ResponseEntity.ok(contactsManagerService.getMemberList(projectId, pageNum, pageSize, corpId,
                departmentId, memberName));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComGroupChatsResponse.class)
    })
    @ApiOperation(value = "获取客户群列表", nickname = "getGroupChatList", notes = "", response =
            WeComGroupChatsResponse.class)
    @RequestMapping(value = {"/group_chat/list"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity listGroupChats(@ApiParam(value = "企微项目id", required = true) @NotNull @Valid @RequestParam(value = "project_id", required =
            true) String projectId,
                                         @ApiParam(value = "页数", required = true) @NotNull @Min(1) @Valid @RequestParam(value = "page_num", required = true,
                                                 defaultValue = "1") Integer pageNum,
                                         @ApiParam(value = "每页的条数", required = true) @NotNull @Min(1) @Max(1000) @Valid @RequestParam(value = "page_size",
                                                 required = true, defaultValue = "20") Integer pageSize,
                                         @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value =
                                                 "corp_id", required = true) String corpId,
                                         @ApiParam(value = "搜索的客户群名称", required = false) @NotNull @Valid @RequestParam(value =
                                                 "group_chat", required = false) String groupChat,
                                         @ApiParam(value = "客户群主列表", required = false) @NotNull @Valid @RequestParam(value =
                                                 "members", required = false) List<String> members) {
        return ResponseEntity.ok(contactsManagerService.getGroupChatList(projectId, corpId, pageNum, pageSize,
                groupChat, members));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComExternalUserDetailResponse.class)
    })
    @ApiOperation(value = "获取客户详情", nickname = "getGroupChatList", notes = "", response =
            WeComExternalUserDetailResponse.class)
    @RequestMapping(value = {"/external_user/detail"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity queryExternalUser(@ApiParam(value = "页数", required = true) @NotNull @Min(1) @Valid @RequestParam(value = "page_num", required = true,
            defaultValue = "1") Integer pageNum,
                                            @ApiParam(value = "每页的条数", required = true) @NotNull @Min(1) @Max(1000) @Valid @RequestParam(value = "page_size",
                                                    required = true, defaultValue = "20") Integer pageSize,
                                            @ApiParam(value = "企微项目id", required = true) @NotNull @Valid @RequestParam(value = "project_id", required =
                                                    true) String projectId, @ApiParam(value = "企业的企微ID", required =
            true) @NotNull @Valid @RequestParam(value =
            "corp_id", required = true) String corpId,
                                            @ApiParam(value = "客户ID", required = true) @NotNull @RequestParam(value =
                                                    "external_user", required =
                                                    true) String externalUserId) {
        return ResponseEntity.ok(contactsManagerService.getExternalUserDetail(corpId, externalUserId, pageNum,
                pageSize));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComGroupChatOwnersResponse.class)
    })
    @ApiOperation(value = "获取客户群群主列表", nickname = "getGroupChatList", notes = "", response =
            WeComGroupChatOwnersResponse.class)
    @RequestMapping(value = {"/group_chat/owners"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity getGroupChatOwnerList(@ApiParam(value = "企微项目id", required = true) @NotNull @Valid @RequestParam(value = "project_id", required =
            true) String projectId, @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value =
            "corp_id", required = true) String corpId) {
        return ResponseEntity.ok(contactsManagerService.getGroupChatOwners(projectId, corpId));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComGroupChatMembersResponse.class)
    })
    @ApiOperation(value = "获取客户群成员详情", nickname = "getGroupChatList", notes = "", response =
            WeComGroupChatMembersResponse.class)
    @RequestMapping(value = {"/group_chat/detail"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity queryGroupChat(@ApiParam(value = "页数", required = true) @NotNull @Min(1) @Valid @RequestParam(value = "page_num", required = true,
            defaultValue = "1") Integer pageNum,
                                         @ApiParam(value = "每页的条数", required = true) @NotNull @Min(1) @Max(1000) @Valid @RequestParam(value = "page_size",
                                                 required = true, defaultValue = "20") Integer pageSize,
                                         @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value = "corp_id", required = true) String corpId,
                                         @ApiParam(value = "客户群id", required = true) @NotNull @Valid @RequestParam(value
                                                 = "group_chat_id", required = true) String groupChatId) {
        return ResponseEntity.ok(contactsManagerService.getGroupChatDetail(corpId, groupChatId, pageNum, pageSize));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComGroupChatStatisticResponse.class)
    })
    @ApiOperation(value = "获取客户群详情和统计", nickname = "queryGroupChatStatistic", notes = "", response =
            WeComGroupChatStatisticResponse.class)
    @RequestMapping(value = {"/group_chat/statistic"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity queryGroupChatStatistic(@ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value = "corp_id", required = true) String corpId,
                                                  @ApiParam(value = "客户群id", required = true) @NotNull @Valid @RequestParam(value
                                                          = "group_chat_id", required = true) String groupChatId) {
        return ResponseEntity.ok(contactsManagerService.getGroupChatStatistic(corpId, groupChatId));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComCustomerStatisticResponse.class)
    })
    @ApiOperation(value = "获取客户统计", nickname = "queryGroupChatStatistic", notes = "", response =
            WeComCustomerStatisticResponse.class)
    @RequestMapping(value = {"/statistic"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity queryStatistic(@ApiParam(value = "企微项目id", required = true) @NotNull @Valid @RequestParam(value = "project_id", required = true) String projectId,
                                         @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value = "corp_id", required = true) String corpId) {
        return ResponseEntity.ok(contactsManagerService.getStatistic(projectId, corpId));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComCustomerStatisticDetailResponse.class)
    })
    @ApiOperation(value = "获取客户统计详情", nickname = "queryGroupChatStatistic", notes = "", response =
            WeComCustomerStatisticDetailResponse.class)
    @RequestMapping(value = {"/statistic/detail"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity queryStatisticDetail(@ApiParam(value = "企微项目id", required = true) @NotNull @Valid @RequestParam(value = "project_id", required = true) String projectId,
                                               @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value = "corp_id", required = true) String corpId,
                                               @ApiParam(value = "统计日期的类型, day 天;", required = true) @NotNull @Valid @RequestParam(value = "date_type", required = true, defaultValue = "day") String dateType,
                                               @ApiParam(value = "是否包含节假日, ;", required = true) @NotNull @Valid @RequestParam(value = "holiday_switch", required = true, defaultValue = "1") Boolean holidaySwitch,
                                               @ApiParam(value = "统计的类型, total 总客户数 add 新增客户数 delete 流失客户数",
                                                       required = true) @NotNull @Valid @RequestParam(value =
                                                       "metric_type", required = true, defaultValue = "total") String metricType,
                                               @ApiParam(value = "开始时间", required = false) @Valid @RequestParam(value = "start_time", required = false) String startTime,
                                               @ApiParam(value = "结束时间", required = false) @Valid @RequestParam(value = "end_time", required = false) String endTime) {
        return ResponseEntity.ok(contactsManagerService.getStatisticDetail(projectId, corpId, dateType, holidaySwitch
                , metricType, startTime, endTime));
    }
}
