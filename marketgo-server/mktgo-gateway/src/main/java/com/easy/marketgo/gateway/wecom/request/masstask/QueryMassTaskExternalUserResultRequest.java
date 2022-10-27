package com.easy.marketgo.gateway.wecom.request.masstask;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-26 16:29
 * Describe:
 */
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryMassTaskExternalUserResultRequest {
    private String msgid;
    private int limit;
    private String cursor;
    private String userid;
}
