package com.easy.marketgo.gateway.wecom.response;

import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-06-08 17:06
 * Describe:
 */
@Data
public class UploadMediaResponse extends WeComBaseResponse {

    private String type;

    @JsonProperty("media_id")
    private String mediaId;

    @JsonProperty("created_at")
    private Long createdAt;

    private String url;
}
