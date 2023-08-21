package com.easy.marketgo.core.entity.welcomemsg;

import com.easy.marketgo.core.entity.UuidBaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 4/21/23 6:08 PM
 * Describe:
 */

@Data
@Table(name = "wecom_welcome_msg_group_chat")
@Accessors(chain = true)
public class WeComWelcomeMsgGroupChatEntity extends UuidBaseEntity {

    private static final long serialVersionUID = -3233844559393888372L;

    /**
     * 关联项目ID
     */
    private String projectUuid;

    /**
     * 对应企业微信ID
     */
    private String corpId;

    private String name;

    /**
     * 创建人ID
     */
    private String creatorId;
    /**
     * 创建人姓名
     */
    private String creatorName;

    private String members;

    private Integer notifyType;

    private String welcomeContent;

    private String templateId;
}
