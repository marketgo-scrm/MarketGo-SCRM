package com.easy.marketgo.api.model.request.masstask;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-27 19:54
 * Describe:
 */
@Data
public class WeComMomentMassTaskCommentsClientRequest implements Serializable {
    private static final long serialVersionUID = 2132336905352007233L;
    private String corpId;
    private String agentId;
    private String momentId;
    private String userId;
}
