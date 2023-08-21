package com.easy.marketgo.core.entity.welcomemsg;

import com.easy.marketgo.core.entity.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 4/21/23 6:08 PM
 * Describe:
 */

@Data
@Accessors(chain = true)
@Table("wecom_welcome_msg_group_chat_statistic")
public class WeComWelcomeMsgGroupChatStatisticEntity extends BaseEntity {
    private String corpId;
    private String groupChatId;
    private Date eventDate;
    private Integer dailySendUserCount;
    private String welcomeMsgUuid;
}
