package com.easy.marketgo.core.entity.customer;

import com.easy.marketgo.core.entity.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/7/22 7:17 PM
 * Describe:
 */
@Data
@Accessors(chain = true)
@Table("wecom_members")
public class WeComMemberMessageEntity extends BaseEntity {
    private String corpId;
    private String memberName;
    private String memberId;
    private String alias;
    private String avatar;
    private String mobile;
    private String thumbAvatar;
    private String department;
    private String mainDepartment;
    /**
     * 激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业。
     */
    private Integer status;
    private String qrCode;
    private String openUserId;
}
