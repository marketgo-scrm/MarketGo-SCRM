package com.easy.marketgo.core.model.callback;

import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/25/22 6:05 PM
 * Describe:
 */
@Data
public class WeComExternalUserMsg {
    private String changeType;
    private String corpId;
    private String memberId;
    private String externalUserId;
    private String state;
    private String welcomeCode;
    private String source;
    private String failReason;
    private Long createTime;
}
