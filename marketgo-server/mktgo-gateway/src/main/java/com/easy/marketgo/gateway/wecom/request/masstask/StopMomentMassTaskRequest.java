package com.easy.marketgo.gateway.wecom.request.masstask;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-26 16:32
 * Describe:
 */
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StopMomentMassTaskRequest {
    @JsonProperty("moment_id")
    private String momentId;
}
