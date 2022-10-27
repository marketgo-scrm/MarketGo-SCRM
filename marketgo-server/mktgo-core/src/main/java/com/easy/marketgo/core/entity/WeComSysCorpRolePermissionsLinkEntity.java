package com.easy.marketgo.core.entity;

import com.easy.marketgo.common.enums.PermissionsEnum;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-16 21:54:52
 * @description : WeComSysCropRolePermissionsLinkEntity.java
 */
@Data
@Table("wecom_sys_corp_role_permissions_link")
public class WeComSysCorpRolePermissionsLinkEntity extends BaseEntity {

    @Column("corp_id")
    private String corpId;
    @Column("role_uuid")
    private String roleUuid;
    @Column("permissions_uuid")
    private String permissionsUuid;
    @Enumerated(value= EnumType.STRING)
    private PermissionsEnum status;

}
