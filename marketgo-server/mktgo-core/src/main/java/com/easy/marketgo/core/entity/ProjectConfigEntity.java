package com.easy.marketgo.core.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;
/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-16 08:51:08
 * @description : ProjectConfigEntity.java
 */
@Data
@Table("project_config")
public class ProjectConfigEntity  extends UuidBaseEntity{
    private String name;
    private String tenantUuid;
    private String status;
    private String desc;
    private String type;
}
