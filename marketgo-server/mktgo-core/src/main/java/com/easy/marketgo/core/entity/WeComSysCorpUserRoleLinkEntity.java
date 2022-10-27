package com.easy.marketgo.core.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-16 08:16:59
 * @description : WeComSysCropUserRoleLinkEntity.java
 */
@Data
@Table("wecom_sys_corp_user_role_link")
public class WeComSysCorpUserRoleLinkEntity extends BaseEntity{
    @Column("corp_id")
    private String corpId;
    @Column("role_uuid")
    private String roleUuid;
    @Column("member_id")
    private String memberId;
    @Column("project_uuid")
    private String projectUuid;
}
