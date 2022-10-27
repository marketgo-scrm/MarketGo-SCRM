package com.easy.marketgo.web.model.response.masstask;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/23/22 11:34 PM
 * Describe:
 */
@Data
@ApiModel(description = "企微群发员工和客户的总量统计")
public class WeComQueryMassTaskStatisticResponse {

    @ApiModelProperty(value = "员工统计详情")
    private MemberStatisticDetail memberDetail = null;

    @ApiModelProperty(value = "客户统计详情")
    private ExternalUserStatisticDetail externalUserDetail = null;

    @ApiModelProperty(value = "互动统计详情")
    private CommentsStatisticDetail commentsDetail = null;
    @Data
    public static class MemberStatisticDetail {
        @ApiModelProperty(value = "群发的预计执行员工")
        private Integer memberTotalCount = null;

        @ApiModelProperty(value = "已执行员工数量")
        private Integer sendCount = null;

        @ApiModelProperty(value = "未执行员工数量")
        private Integer nonSendCount = null;

        @ApiModelProperty(value = "离职员工数量")
        private Integer sendFailedCount = null;
    }

    @Data
    public static class ExternalUserStatisticDetail {
        @ApiModelProperty(value = "群发预计送达客户")
        private Integer externalUserTotalCount = null;

        @ApiModelProperty(value = "已送达客户数量")
        private Integer deliveredCount = null;

        @ApiModelProperty(value = "未送达客户数量")
        private Integer nonDeliveredCount = null;

        @ApiModelProperty(value = "超额送达客户数量")
        private Integer receiveLimitCount = null;

        @ApiModelProperty(value = "送达失败的客户数量")
        private Integer unfriendCount = null;

    }

    @Data
    public static class CommentsStatisticDetail {
        @ApiModelProperty(value = "点赞客户数量")
        private Integer externalUserLikeCount = null;

        @ApiModelProperty(value = "评论员工数量")
        private Integer memberCommentsCount = null;

        @ApiModelProperty(value = "评论客户数量")
        private Integer externalUserCommentsCount = null;

        @ApiModelProperty(value = "点赞员工数量")
        private Integer memberLikeCount = null;
    }
}
