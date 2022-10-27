package com.easy.marketgo.core.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.util.Date;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-31 11:48
 * Describe:
 */
@Getter
@Setter
@ToString
public abstract class BaseEntity {
    @Id
    private Integer id;

    @ReadOnlyProperty
    private Date createTime;

    @ReadOnlyProperty
    private Date updateTime;
}