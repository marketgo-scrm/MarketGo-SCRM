package com.easy.marketgo.core.entity.callback;

import com.easy.marketgo.core.entity.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/27/22 12:55 PM
 * Describe:
 */
@Data
@Accessors(chain = true)
@Table("wecom_event_group_chat")
public class WeComGroupChatEventEntity extends BaseEntity {
    private String eventMd5;
    private String corpId;
    private String groupChatId;
    private String updateDetail;
    private String eventType;
    private Integer joinScene;
    private Integer quitScene;
    private Integer memChangeCnt;
    private Date eventTime;
}
