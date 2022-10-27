package com.easy.marketgo.api.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/30/22 12:37 PM
 * Describe:
 */
@Data
public class BaseRpcRequest implements Serializable {

    private static final long serialVersionUID = 6224692831754395701L;
    private String corpId;

    private String agentId;
}
