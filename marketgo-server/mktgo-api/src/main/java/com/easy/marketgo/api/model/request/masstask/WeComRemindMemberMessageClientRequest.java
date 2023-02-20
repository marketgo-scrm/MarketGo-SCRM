package com.easy.marketgo.api.model.request.masstask;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-27 19:51
 * Describe:
 */
@Data
public class WeComRemindMemberMessageClientRequest implements Serializable {
    private String corpId;
    private String agentId;
    private String msgId;

}
