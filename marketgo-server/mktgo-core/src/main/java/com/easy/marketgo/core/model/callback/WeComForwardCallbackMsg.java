package com.easy.marketgo.core.model.callback;

import lombok.Data;
import org.springframework.http.HttpMethod;

import java.io.Serializable;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/25/22 11:19 AM
 * Describe:
 */
@Data
public class WeComForwardCallbackMsg implements Serializable {
    private HttpMethod msgType;
    private String msgSignature;
    private String corpId;
    private String timestamp;
    private String nonce;
    private String echostr;
    private String msgEvent;

}
