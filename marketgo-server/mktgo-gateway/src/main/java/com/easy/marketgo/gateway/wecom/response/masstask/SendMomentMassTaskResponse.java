package com.easy.marketgo.gateway.wecom.response.masstask;

import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-26 16:20
 * Describe:
 */
@Data
public class SendMomentMassTaskResponse extends WeComBaseResponse {
    @JsonProperty("jobid")
    private String jobId = null;
}
