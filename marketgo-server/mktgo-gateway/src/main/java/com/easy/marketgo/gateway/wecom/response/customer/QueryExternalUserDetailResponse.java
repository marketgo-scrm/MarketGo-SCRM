package com.easy.marketgo.gateway.wecom.response.customer;

import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/6/22 5:32 PM
 * Describe:
 */
@Data
public class QueryExternalUserDetailResponse extends WeComBaseResponse {

    @JsonProperty("external_contact")
    private ExternalUserContact externalContact;

    @JsonProperty("follow_user")
    private List<ExternalUserFollowUser> followUser = null;

    @JsonProperty("next_cursor")
    private String nextCursor = null;

    @Data
    public static class ExternalUserContact {
        @JsonProperty("external_userid")
        private String externalUserId;
        private String name;
        private String position;
        private String avatar;
        @JsonProperty("corp_name")
        private String corpName;
        @JsonProperty("corp_full_name")
        private String corpFullName;
        private Integer type;
        private Integer gender;
        private String unionid;
        @JsonProperty("external_profile")
        private ExternalProfile externalProfile = null;
    }

    @Data
    public static class ExternalProfile {
        @JsonProperty("external_attr")
        private List<ExternalProfileAttr> externalAttr = null;
    }

    @Data
    public static class ExternalProfileAttr {
        @JsonProperty("type")
        private Integer type = null;

        @JsonProperty("name")
        private String name = null;

        @JsonProperty("text")
        private ExternalProfileAttrText text = null;

        @JsonProperty("web")
        private ExternalProfileAttrWeb web = null;

        @JsonProperty("miniprogram")
        private ExternalProfileAttrMiniProgram miniProgram = null;
    }

    @Data
    public static class ExternalProfileAttrText {
        @JsonProperty("value")
        private String value = null;
    }

    @Data
    public static class ExternalProfileAttrWeb {
        @JsonProperty("url")
        private String url = null;

        @JsonProperty("title")
        private String title = null;
    }

    @Data
    public static class ExternalProfileAttrMiniProgram {
        @JsonProperty("appid")
        private String appId = null;

        @JsonProperty("pagepath")
        private String pagePath = null;

        @JsonProperty("title")
        private String title = null;
    }

    @Data
    public static class ExternalUserFollowUser {
        @JsonProperty("userid")
        private String userId = null;

        @JsonProperty("remark")
        private String remark = null;

        @JsonProperty("description")
        private String description = null;

        @JsonProperty("createtime")
        private Long createTime = null;

        @JsonProperty("remark_corp_name")
        private String remarkCorpName = null;

        @JsonProperty("remark_mobiles")
        private List<String> remarkMobiles = null;

        @JsonProperty("add_way")
        private Integer addWay = null;

        @JsonProperty("oper_userid")
        private String operUserId = null;

        @JsonProperty("state")
        private String state = null;

        @JsonProperty("tags")
        private List<ExternalUserFollowUserTag> tags = null;
    }

    @Data
    public static class ExternalUserFollowUserTag {
        @JsonProperty("group_name")
        private String groupName = null;

        @JsonProperty("tag_name")
        private String tagName = null;

        @JsonProperty("type")
        private Integer type = null;

        @JsonProperty("tag_id")
        private String tagId = null;
    }
}
