package com.easy.marketgo.api.model.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/31/22 3:26 PM
 * Describe:
 */
@Data
public class WeComAddContactWayClientResponse implements Serializable {
    private String configId;
    private String qrCode;
}
