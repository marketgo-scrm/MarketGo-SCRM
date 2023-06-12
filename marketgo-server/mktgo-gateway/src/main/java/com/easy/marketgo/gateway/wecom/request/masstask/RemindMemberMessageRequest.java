package com.easy.marketgo.gateway.wecom.request.masstask;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-26 16:27
 * Describe:
 */
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RemindMemberMessageRequest {
    @JsonProperty("msgid")
    private String msgId;
}
