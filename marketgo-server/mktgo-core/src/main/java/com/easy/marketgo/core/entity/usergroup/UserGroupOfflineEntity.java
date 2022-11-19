package com.easy.marketgo.core.entity.usergroup;

import com.easy.marketgo.core.entity.UuidBaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/17/22 11:27 AM
 * Describe:
 */
@Data
@Accessors(chain = true)
@Table("user_group_offline")
public class UserGroupOfflineEntity extends UuidBaseEntity {
    private String corpId;
    private String externalUserId;
    private String memberId;
}
