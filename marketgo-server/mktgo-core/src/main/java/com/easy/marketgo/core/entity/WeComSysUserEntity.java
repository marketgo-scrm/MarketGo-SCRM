package com.easy.marketgo.core.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;
/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-16 08:25:33
 * @description : WeComSysUserEntity.java
 */
@Data
@Accessors(chain = true)
@Table("wecom_sys_user")
public class WeComSysUserEntity extends UuidBaseEntity {
    @Column("user_config")
    private String userConfig;
    @Column("user_name")
    private String userName;
    private String password;
    private String salt;
    @Column("auth_status")
    private Boolean authStatus;


}
