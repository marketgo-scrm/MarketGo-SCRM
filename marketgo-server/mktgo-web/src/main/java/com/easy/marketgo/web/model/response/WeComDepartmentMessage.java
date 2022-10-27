package com.easy.marketgo.web.model.response;

import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/21/22 5:58 PM
 * Describe:
 */
@Data
public class WeComDepartmentMessage {
    private String name;
    private int parentId;
    private String id;
    private int order;

    /** 前端使用对象 */
    private List<WeComDepartmentMessage> children;
}
