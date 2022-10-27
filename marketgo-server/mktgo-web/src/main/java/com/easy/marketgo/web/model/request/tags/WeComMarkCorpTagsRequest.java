package com.easy.marketgo.web.model.request.tags;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/19/22 6:28 PM
 * Describe:
 */
@Data
@ApiModel(description = "给客户设置标签的request")
public class WeComMarkCorpTagsRequest {
    @ApiModelProperty(value = "是否是全部客户")
    private Boolean isAll;

    @ApiModelProperty(value = "搜索的客户名称")
    private String keyword;
    @ApiModelProperty(value = "客户的流失状态 0 是好友; 1 员工删除客户; 2 客户删除员工")
    private List<Integer> statuses;
    @ApiModelProperty(value = "是否去重")
    private boolean duplicate;
    @ApiModelProperty(value = "所属员工id列表")
    private List<String> memberIds;
    @ApiModelProperty(value = "所属员工部门id列表")
    private List<Integer> departments;
    @ApiModelProperty(value = "标签id列表")
    private List<String> tags;
    @ApiModelProperty(value = "添加渠道列表")
    private List<Integer> channels;

    @ApiModelProperty(value = "所在群聊的列表")
    private List<String> groupChats;
    @ApiModelProperty(value = "性别 0-未知 1-男性 2-女性")
    private List<Integer> genders;
    @ApiModelProperty(value = "添加的开始时间")
    private String startTime;
    @ApiModelProperty(value = "添加客户的结束时间")
    private String endTime;
    @ApiModelProperty(value = "客户和员工的列表")
    List<ExternalUserAndMember> externalUsers;

    @ApiModelProperty(value = "设置的标签的列表")
    List<String> markCorpTags;

    @Data
    @ApiModel(description = "客户和员工的详情")
    public static class ExternalUserAndMember {
        @ApiModelProperty(value = "员工id")
        private String memberId;
        @ApiModelProperty(value = "客户id")
        private String externalUserId;
    }
}
