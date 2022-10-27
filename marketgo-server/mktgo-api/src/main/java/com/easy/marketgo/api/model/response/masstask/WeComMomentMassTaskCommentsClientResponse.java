package com.easy.marketgo.api.model.response.masstask;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/4/22 7:52 PM
 * Describe:
 */
@Data
public class WeComMomentMassTaskCommentsClientResponse implements Serializable {
    private List<CommentMessage> commentList;
    private List<CommentMessage> likeList;

    @Data
    public static class CommentMessage implements Serializable {
        private String externalUserId;
        private String userId;
        private Long createTime;
    }
}
