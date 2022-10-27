package com.easy.marketgo.core.entity.customer;

import com.easy.marketgo.core.entity.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/16/22 10:40 PM
 * Describe:
 */
@Data
@Accessors(chain = true)
@Table("wecom_group_chat_members")
public class WeComGroupChatMembersEntity extends BaseEntity {
    private String corpId;
    private String groupChatId;
    private String groupNickname;
    private String userId;
    private Integer type;
    private String unionId;
    private Integer joinScene;
    private Date joinTime;
    private String name;
}
