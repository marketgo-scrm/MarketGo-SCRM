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
public class WeComMarkCorpTagsMessage {
    private String corpId;
    private String agentId;
    private String memberId;
    private String externalUserId;
    private List<String> tagIds;
    private List<String> deleteTagIds;
}
