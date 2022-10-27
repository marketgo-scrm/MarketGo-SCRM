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
@Table("wecom_event_group_chat")
public class WeComEventGroupChatsEntity extends BaseEntity {
    private String eventMd5;
    private String corpId;
    private String groupChatId;
    /**
     * 当eventType未update的时候。updateDetail描述变更的详情
     * add_member : 成员入群
     * del_member : 成员退群
     * change_owner : 群主变更
     * change_name : 群名变更
     * change_notice : 群公告变更
     */
    private String updateDetail;
    /**
     * 事件类型
     * create 创建客户群
     * update 变更客户群
     * dismiss 客户群解散
     */
    private String eventType;
    /**
     * 当是成员入群时有值。表示成员的入群方式
     * 0 - 由成员邀请入群（包括直接邀请入群和通过邀请链接入群）
     * 3 - 通过扫描群二维码入群
     */
    private Integer joinScene;

    /**
     * 当是成员入群时有值。表示成员的入群方式
     * 0 - 自己退群
     * 1 - 群主/群管理员移出
     */
    private Integer quitScene;
    /**
     * 当是成员入群或退群时有值。表示成员变更数量
     */
    private Integer memChangeCnt;

    private Date eventTime;
}
