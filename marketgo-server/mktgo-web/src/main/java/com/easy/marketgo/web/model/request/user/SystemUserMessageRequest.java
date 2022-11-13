package com.easy.marketgo.web.model.request.user;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "系统用户信息")
public class SystemUserMessageRequest {
    private String memberId = null;

    private String mobile = null;

    private Boolean authStatus;
}
