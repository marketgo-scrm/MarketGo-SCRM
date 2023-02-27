package com.easy.marketgo.common.constants;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/3/22 9:52 PM
 * Describe:
 */
public class RabbitMqConstants {
    public static final String VIRTUAL_HOST_WECOM_MASS_TASK = "/wecom";

    public static final String EXCHANGE_NAME_FAILURE_QUEUE = "collect.message.failure.exchange";
    public static final String FUNNEL_ROUTING_FAILURE_MSG = "funnel.message.failure.routing.key";
    public static final String FUNNEL_QUEUE_NAME_FAILURE_MSG = "funnel.message.failure.queue";


    public static final String EXCHANGE_NAME_DEFAULT_WECOM_MASS_TASK_STATISTIC = "wecom.mass_task_statistic.exchange";

    public static final String ROUTING_KEY_WECOM_MASS_TASK_STATISTIC = "wecom.mass_task_statistic.routing.key";
    public static final String QUEUE_NAME_WECOM_MASS_TASK_STATISTIC = "wecom.mass_task_statistic.queue";


    public static final String EXCHANGE_NAME_DEFAULT_WECOM_MASS_TASK_GROUP = "wecom.mass_task_group.exchange";

    public static final String ROUTING_KEY_WECOM_MASS_TASK_GROUP = "wecom.mass_task_group.routing.key";
    public static final String QUEUE_NAME_WECOM_MASS_TASK_GROUP = "wecom.mass_task_group.queue";


    public static final String EXCHANGE_NAME_DEFAULT_WECOM_EDIT_CORP_TAGS = "wecom.edit_corp_tags.exchange";

    public static final String ROUTING_KEY_WECOM_EDIT_CORP_TAGS = "wecom.edit_corp_tags.routing.key";
    public static final String QUEUE_NAME_WECOM_EDIT_CORP_TAGS = "wecom.edit_corp_tags.queue";

    public static final String EXCHANGE_NAME_DEFAULT_WECOM_MASS_TASK_SINGLE = "wecom.mass_task_single.exchange";

    public static final String ROUTING_KEY_WECOM_MASS_TASK_SINGLE = "wecom.mass_task_single.routing.key";
    public static final String QUEUE_NAME_WECOM_MASS_TASK_SINGLE = "wecom.mass_task_single.queue";


    public static final String EXCHANGE_NAME_DEFAULT_WECOM_MASS_TASK_MOMENT = "wecom.mass_task_moment.exchange";

    public static final String ROUTING_KEY_WECOM_MASS_TASK_MOMENT = "wecom.mass_task_moment.routing.key";
    public static final String QUEUE_NAME_WECOM_MASS_TASK_MOMENT = "wecom.mass_task_moment.queue";
    /**
     *
     */
    public static final String EXCHANGE_NAME_DEFAULT_CHANGE_EXT_CONTACT = "wecom.change_ext_contact.exchange";

    public static final String ROUTING_KEY_WECOM_ADD_EXT_CONTACT = "wecom.live.code.add.ext.contact.routing.key";
    public static final String QUEUE_NAME_WECOM_ADD_EXT_CONTACT = "wecom.live.code.add.ext.contact.queue";

    /**
     * 删除企业客户队列及route
     */
    public static final String ROUTING_KEY_WECOM_DEL_EXT_CONTACT = "wecom.live.code.del.ext.contact.routing.key";
    public static final String QUEUE_NAME_WECOM_DEL_EXT_CONTACT = "wecom.live.code.del.ext.contact.queue";

    /**
     * 删除企业客户队列及route
     */
    public static final String ROUTING_KEY_WECOM_DEL_FOLLOW_USER = "wecom.live.code.del.follow.user.routing.key";
    public static final String QUEUE_NAME_WECOM_DEL_FOLLOW_USER = "wecom.live.code.ddel.follow.user.queue";


    public static final String EXCHANGE_NAME_DEFAULT_WECOM_GROUP_CHAT = "wecom.group_chat.exchange";

    public static final String ROUTING_KEY_WECOM_GROUP_CHAT = "wecom.group_chat.routing.key";
    public static final String QUEUE_NAME_WECOM_GROUP_CHAT = "wecom.group_chat.queue";


    public static final String EXCHANGE_NAME_DEFAULT_WECOM_MEMBER = "wecom.member.exchange";

    public static final String ROUTING_KEY_WECOM_MEMBER = "wecom.member.routing.key";
    public static final String QUEUE_NAME_WECOM_MEMBER = "wecom.member.queue";


    public static final String EXCHANGE_NAME_DEFAULT_WECOM_CORP_TAG = "wecom.corp_tag.exchange";

    public static final String ROUTING_KEY_WECOM_CORP_TAG = "wecom.corp_tag.routing.key";
    public static final String QUEUE_NAME_WECOM_CORP_TAG = "wecom.corp_tag.queue";


    public static final String EXCHANGE_NAME_DEFAULT_WECOM_EXTERNAL_USER = "wecom.external_user.exchange";

    public static final String ROUTING_KEY_WECOM_EXTERNAL_USER = "wecom.external_user.routing.key";
    public static final String QUEUE_NAME_WECOM_EXTERNAL_USER = "wecom.external_user.queue";

    public static final String EXCHANGE_NAME_DEFAULT_WECOM_LIVE_CODE_EXTERNAL_USER = "wecom.live_code.external_user" +
            ".exchange";

    public static final String ROUTING_KEY_WECOM_LIVE_CODE_EXTERNAL_USER = "wecom.live_code.external_user.routing.key";
    public static final String QUEUE_NAME_WECOM_LIVE_CODE_EXTERNAL_USER = "wecom.live_code.external_user.queue";

    public static final String EXCHANGE_NAME_WECOM_MEMBER_FORWARD = "wecom.member.forward.exchange";

    public static final String ROUTING_KEY_WECOM_MEMBER_FORWARD  = "wecom.member.forward.routing.key";
    public static final String QUEUE_NAME_WECOM_MEMBER_FORWARD  = "wecom.member.forward.queue";

    public static final String EXCHANGE_NAME_WECOM_EXTERNAL_USER_FORWARD = "wecom.external_user.forward.exchange";

    public static final String ROUTING_KEY_WECOM_EXTERNAL_USER_FORWARD = "wecom.external_user.forward.routing.key";
    public static final String QUEUE_NAME_WECOM_EXTERNAL_USER_FORWARD = "wecom.external_user.forward.queue";


    public static final String EXCHANGE_NAME_WECOM_TASK_CENTER_SINGLE = "wecom.task_center_single.exchange";

    public static final String ROUTING_KEY_WECOM_TASK_CENTER_SINGLE = "wecom.task_center_single.routing.key";
    public static final String QUEUE_NAME_WECOM_TASK_CENTER_SINGLE = "wecom.task_center_single.queue";


    public static final String EXCHANGE_NAME_WECOM_TASK_CENTER_STATISTIC = "wecom.task_center_statistic.exchange";

    public static final String ROUTING_KEY_WECOM_TASK_CENTER_STATISTIC = "wecom.task_center_statistic.routing.key";
    public static final String QUEUE_NAME_WECOM_TASK_CENTER_STATISTIC = "wecom.task_center_statistic.queue";


    public static final String EXCHANGE_NAME_WECOM_TASK_CENTER_GROUP = "wecom.task_center_group.exchange";

    public static final String ROUTING_KEY_WECOM_TASK_CENTER_GROUP = "wecom.task_center_group.routing.key";
    public static final String QUEUE_NAME_WECOM_TASK_CENTER_GROUP = "wecom.task_center_group.queue";


    public static final String EXCHANGE_NAME_WECOM_TASK_CENTER_MOMENT = "wecom.task_center_moment.exchange";

    public static final String ROUTING_KEY_WECOM_TASK_CENTER_MOMENT = "wecom.task_center_moment.routing.key";
    public static final String QUEUE_NAME_WECOM_TASK_CENTER_MOMENT = "wecom.task_center_moment.queue";
}
