package com.easy.marketgo.core.entity.customer;

import com.easy.marketgo.core.entity.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/16/22 10:40 PM
 * Describe:
 */
@Data
@Accessors(chain = true)
@Table("wecom_departments")
public class WeComDepartmentEntity extends BaseEntity {
    private String corpId;
    private Long departmentId;
    private String departmentName;
    private String departmentNameEn;
    private String departmentLeader;
    private Long parentId;
    private Long order;

}
