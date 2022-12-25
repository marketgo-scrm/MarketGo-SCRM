package com.easy.marketgo.cdp.analysys.response;


/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/23/22 8:04 PM
 * Describe:
 */
public class CrowdMessageResponse {

    private String id;
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
     * 分群最后一次计算时间
     */
    private String calculatedTime;
    /**
     * 分群状态 success：成功，failed：失败，calculating：计算中
     */
    private String status;
    /**
     * 当前分群的用户数，全部用户默认不计算
     */
    private String userNumber;

    /**
     * 创建时间
     */
    private String createdTime;

    /**
     * 创建人
     */
    private String createdUser;

    /**
     * 最后更新时间
     */
    private String updatedTime;
}
