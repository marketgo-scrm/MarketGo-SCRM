package com.easy.marketgo.core.model.cdp;

import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/21/22 3:03 PM
 * Describe:
 */
@Data
public class CdpCrowdListMessage {

    private Integer code;
    private String message;

    private List<CrowdMessage> crowds;

    private String cdpType;

    @Data
    public static class CrowdMessage {
        /**
         * 分群code 在查询需要用到分群时都通过code来筛选
         */
        private String code;

        /**
         * 分群名称
         */
        private String name;

        /**
         * 动态分群 1为动态分群 0为静态分群
         */
        private String dynamic;

        /**
         * 当前分群的用户数，全部用户默认不计算
         */
        private String userCount;
    }
}
