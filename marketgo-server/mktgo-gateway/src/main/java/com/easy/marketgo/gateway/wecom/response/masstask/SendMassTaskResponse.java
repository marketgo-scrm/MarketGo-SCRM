package com.easy.marketgo.gateway.wecom.response.masstask;

import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-26 16:20
 * Describe:
 */
@Data
public class SendMassTaskResponse extends WeComBaseResponse {
    @JsonProperty("fail_list")
    private List<String> failList = null;
    @JsonProperty("msgid")
    private String msgId = null;
}
