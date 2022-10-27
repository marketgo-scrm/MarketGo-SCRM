package com.easy.marketgo.core.model.bo;

import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 9/4/22 2:55 PM
 * Describe:
 */

@Data
public class WeComMarkCorpTags {
    private String corpId;
    private String agentId;
    private List<String> tagIds;
    private List<String> deleteTagIds;
    List<ExternalUserAndMember> externalUsers;

    @Data
    public static class ExternalUserAndMember {
        private String memberId;
        private String externalUserId;
    }
}
