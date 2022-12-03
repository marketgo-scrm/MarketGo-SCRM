package com.easy.marketgo.cdp.sensor.response;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/23/22 8:04 PM
 * Describe:
 */
public class CrowdMessageResponse {

    private Integer id;

    /**
     * 用户群英文名
     */
    private String name;

    /**
     * 用户群中文名
     */
    private String cname;

    @JsonProperty("user_name")
    private String userName;
    /**
     * 动态分群 1为动态分群 0为静态分群
     */
    @JsonProperty("data_type")
    private Boolean dataType;

    /**
     * 分群最后一次计算时间
     */
    private String unit;
    /**
     * 分群状态 success：成功，failed：失败，calculating：计算中
     */
    @JsonProperty("source_type")
    private String sourceType;
    /**
     * 当前分群的用户数，全部用户默认不计算
     */
    @JsonProperty("is_routine")
    private Boolean isRoutine;

    /**
     * 创建时间
     */
    @JsonProperty("create_time")
    private String createdTime;

    /**
     * 创建人
     */
    private String status;

    /**
     * 最后更新时间
     */
    private String updatedTime;
}
