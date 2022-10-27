package com.easy.marketgo.web.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/21/22 6:08 PM
 * Describe:
 */
@Setter
@Getter
@Builder
public class WeComGroupChatResponse {
    private Integer code = null;

    private String message = null;

    private Integer total = null;

    private List<WeComGroupChatMessage> data = null;
}
