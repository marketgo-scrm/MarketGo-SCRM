package com.easy.marketgo.core.model.wecom;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-26 17:01
 * Describe:
 */
@Data
public class WeComBaseResponse implements Serializable {

    private Integer errcode = 0;

    private String errmsg = "ok";
}
