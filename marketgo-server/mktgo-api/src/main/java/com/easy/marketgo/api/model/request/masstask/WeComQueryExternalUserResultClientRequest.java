package com.easy.marketgo.api.model.request.masstask;

import com.easy.marketgo.api.model.request.BaseRpcRequest;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-27 19:52
 * Describe:
 */
@Data
public class WeComQueryExternalUserResultClientRequest extends BaseRpcRequest {
    private static final long serialVersionUID = 1386948085799828020L;
    private String msgid;
    private int limit;
    private String cursor;
    private String userid;
}
