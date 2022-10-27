package com.easy.marketgo.web.model.bo;

import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/22/22 1:08 PM
 * Describe:
 */
@Data
public class DepartmentMessage {
    private Integer id;
    private String name;
    private String nameEn;
    private List<String> departmentLeader;
    private Integer parentId;
    private Integer order;
}
