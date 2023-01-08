package com.easy.marketgo.gateway.wecom.response.masstask;

import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-26 16:35
 * Describe:
 */
@Data
public class QueryMomentMassTaskForCommentsResponse extends WeComBaseResponse {
    @JsonProperty("comment_list")
    private List<ResultCommentList> commentList;

    @JsonProperty("like_list")
    private List<ResultCommentList> likeList;


    @Data
    public static class ResultCommentList implements Serializable {
        @JsonProperty("external_userid")
        private String externalUserId;
        @JsonProperty("userid")
        private String userId;
        @JsonProperty("create_time")
        private Long createTime;
    }
}
