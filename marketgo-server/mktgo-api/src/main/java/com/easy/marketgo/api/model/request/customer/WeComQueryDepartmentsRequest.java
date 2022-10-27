package com.easy.marketgo.api.model.request.customer;

import com.easy.marketgo.api.model.request.BaseRpcRequest;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/4/22 10:51 PM
 * Describe:
 */
@Data
public class WeComQueryDepartmentsRequest extends BaseRpcRequest {
    private String departmentId;
}
