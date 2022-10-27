package com.easy.marketgo.core.model.callback;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/25/22 11:19 AM
 * Describe:
 */
@Data
public class WeComMemberMsg implements Serializable {
    private String changeType;
    private String corpId;
    private String memberId;
    private String newMemberId;
    private List<Long> departments;
    private Long createTime;
}
