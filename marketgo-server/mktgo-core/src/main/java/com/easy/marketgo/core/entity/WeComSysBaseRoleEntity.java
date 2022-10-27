package com.easy.marketgo.core.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-15 22:48:59
 * @description : WeComSysBaseRoleEntity.java
 */
@Data
@Accessors(chain = true)
@Table("wecom_sys_base_role")
public class WeComSysBaseRoleEntity extends UuidBaseEntity{
    private String code;
    private String desc;
    @Column("corp_id")
    private String corpId;
    @Column("project_uuid")
    private String projectUuid;
}
