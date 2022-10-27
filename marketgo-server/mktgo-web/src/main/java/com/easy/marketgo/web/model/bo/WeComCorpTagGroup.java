package com.easy.marketgo.web.model.bo;

import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/21/22 7:12 PM
 * Describe:
 */
@Data
public class WeComCorpTagGroup {
    private String groupId = null;

    private String groupName = null;

    private String createTime = null;

    private Integer order = null;

    private Boolean deleted = null;

    private List<WeComCorpTag> tag = null;
}
