package com.easy.marketgo.api.model.request.chats;

import com.easy.marketgo.api.model.request.BaseRpcRequest;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/30/22 12:51 PM
 * Describe:
 */
@Data
public class WeComQueryGroupChatsRequest extends BaseRpcRequest {
    private List<String> memberIds;
    private String cursor;
    private Integer limit;
    private Integer statusFilter;
}
