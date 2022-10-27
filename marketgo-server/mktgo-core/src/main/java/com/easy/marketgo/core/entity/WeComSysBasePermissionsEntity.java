package com.easy.marketgo.core.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-15 22:48:59
 * @description : WeComSysBasePermissionsEntity.java
 */
@Data
@Accessors(chain = true)
@Table("wecom_sys_base_permissions")
public class WeComSysBasePermissionsEntity extends UuidBaseEntity{
    private String code;
    private String name;
    private String title;
    @Column("parent_code")
    private String parentCode;
    @Column("parent_name")
    private String parentName;
    @Column("parent_title")
    private String parentTitle;
    @Column("project_uuid")
    private String projectUuid;
}
