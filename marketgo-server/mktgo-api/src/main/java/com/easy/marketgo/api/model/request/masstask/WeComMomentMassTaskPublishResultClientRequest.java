package com.easy.marketgo.api.model.request.masstask;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-27 19:53
 * Describe:
 */
@Data
public class WeComMomentMassTaskPublishResultClientRequest implements Serializable {
    private static final long serialVersionUID = -5234307232408188243L;
    private String corpId;
    private String agentId;
    private String momentId;
    private int limit;
    private String cursor;
}
