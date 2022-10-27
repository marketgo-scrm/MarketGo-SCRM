package com.easy.marketgo.core.entity.customer;

import com.easy.marketgo.core.entity.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/7/22 9:43 PM
 * Describe:
 */
@Data
@Accessors(chain = true)
@Table("wecom_event_external_user")
public class WeComEventExternalUserEntity extends BaseEntity {
    private String eventMd5;
    private String corpId;
    private String memberId;
    private String externalUserId;
    /**
     * 事件类型
     * add_external_contact 添加客户
     * edit_external_contact 编辑客户
     * add_half_external_contact 外部联系人免验证添加成员事件
     * del_external_contact 员工删除客户
     * del_follow_user 被客户删除好友
     * transfer_fail 客户接替失败， 企业将客户分配给新的成员接替后，客户添加失败时回调该事件
     */
    private String eventType;
    /**
     * 添加此客户的「联系我」方式配置的state参数，可用于识别添加此用户的渠道
     */
    private String state;
    /**
     * 欢迎语code，可用于发送欢迎语
     */
    private String welcomeCode;
    /**
     * 当事件类型为del_external_contact 的时候
     * 删除客户的操作来源，DELETE_BY_TRANSFER表示此客户是因在职继承自动被转接成员删除
     */
    private String source;
    /**
     * transfer_fail 的时候
     * 接替失败的原因, customer_refused-客户拒绝， customer_limit_exceed-接替成员的客户数达到上限
     */
    private String failReason;

    private Date eventTime;
}
