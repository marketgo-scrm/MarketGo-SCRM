package com.easy.marketgo.api.model.response.masstask;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/6/22 10:45 PM
 * Describe:
 */
@Data
public class WeComSendMassTaskClientResponse implements Serializable {
    private List<String> failList = null;
    private String msgId = null;
}
