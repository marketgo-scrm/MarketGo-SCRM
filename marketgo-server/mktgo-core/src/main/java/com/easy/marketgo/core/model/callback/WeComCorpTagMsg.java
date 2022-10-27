package com.easy.marketgo.core.model.callback;

import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/25/22 6:05 PM
 * Describe:
 */
@Data
public class WeComCorpTagMsg {
    private String changeType;
    private String corpId;
    private String tagId;
    private String tagType;
    private Integer strategyId;
    private Long createTime;
}
