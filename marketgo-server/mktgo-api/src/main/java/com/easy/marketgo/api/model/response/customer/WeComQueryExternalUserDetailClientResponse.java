package com.easy.marketgo.api.model.response.customer;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/6/22 5:50 PM
 * Describe:
 */
@Data
public class WeComQueryExternalUserDetailClientResponse implements Serializable {
    private ExternalUserContact externalContact;

    private List<ExternalUserFollowUser> followUser = null;

    private String nextCursor = null;

    @Data
    public static class ExternalUserContact implements Serializable {
        private String externalUserId;
        private String name;
        private String position;
        private String avatar;
        private String corpName;
        private String corpFullName;
        private Integer type;
        private Integer gender;
        private String unionid;
        private ExternalProfile externalProfile = null;
    }

    @Data
    public static class ExternalProfile implements Serializable {
        private List<ExternalProfileAttr> externalAttr = null;
    }

    @Data
    public static class ExternalProfileAttr implements Serializable {
        private Integer type = null;

        private String name = null;

        private ExternalProfileAttrText text = null;

        private ExternalProfileAttrWeb web = null;

        private ExternalProfileAttrMiniProgram miniProgram = null;
    }

    @Data
    public static class ExternalProfileAttrText implements Serializable {
        private String value = null;
    }

    @Data
    public static class ExternalProfileAttrWeb implements Serializable {
        private String url = null;

        private String title = null;
    }

    @Data
    public static class ExternalProfileAttrMiniProgram implements Serializable {
        private String appId = null;

        private String pagePath = null;

        private String title = null;
    }

    @Data
    public static class ExternalUserFollowUser implements Serializable {
        private String userId = null;

        private String remark = null;

        private String description = null;

        private Long createTime = null;

        private String remarkCorpName = null;

        private List<String> remarkMobiles = null;

        private Integer addWay = null;

        private String operUserId = null;

        private String state = null;

        private List<ExternalUserFollowUserTag> tags = null;
    }

    @Data
    public static class ExternalUserFollowUserTag implements Serializable {
        private String groupName = null;

        private String tagName = null;

        private Integer type = null;

        private String tagId = null;
    }
}
