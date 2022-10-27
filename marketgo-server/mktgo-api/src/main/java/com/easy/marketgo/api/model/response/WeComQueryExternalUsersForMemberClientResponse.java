package com.easy.marketgo.api.model.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/6/22 5:08 PM
 * Describe:
 */
@Data
public class WeComQueryExternalUsersForMemberClientResponse implements Serializable {
    private List<String> externalUserId;
}
