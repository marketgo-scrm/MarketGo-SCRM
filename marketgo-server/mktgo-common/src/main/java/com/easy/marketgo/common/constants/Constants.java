package com.easy.marketgo.common.constants;

import lombok.experimental.UtilityClass;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-30 12:28
 * Describe:
 */
@UtilityClass
public class Constants {
    //缓存时效-10秒
    public static int CACHE_EXP_TEN_SECONDS = 10;

    //缓存时效-1分钟
    public static int CACHE_EXP_MINUTE = 60;

    //缓存时效-永久有效
    public static int CACHE_EXP_FOREVER = 0;

    // 如果朋友圈的任务超过了180，需要检测是否到了关闭的窗口期；
    public static final int MAX_TIME_FOR_MOMENT_SYNC = 180;


    public static final int WECOM_MASS_TASK_SINGLE_MAX_SIZE_EXTERNAL_USER = 10000;

    public final String AGENT_KEY_FOR_CONTACTS = "contacts";

    public final String AGENT_KEY_FOR_EXTERNALUSER = "externaluser";


    public final String WECOM_CALLBACK_CONSTACTS = "/mktgo/api/wecom/callback/contacts?corp_id=";

    public final String WECOM_CALLBACK_CUSTOMER = "/mktgo/api/wecom/callback/customer?corp_id=";

    public final String WECOM_SIDEBAR_MESSAGE = "/mktgo/client/wecom/sidebar/detail?corp_id=";


    public final String WECOM_CALLBACK_MESSAGE_TYPE_CREATE_USER = "create_user";
    public final String WECOM_CALLBACK_MESSAGE_TYPE_UPDATE_USER = "update_user";
    public final String WECOM_CALLBACK_MESSAGE_TYPE_DELETE_USER = "delete_user";

    public final String WECOM_CALLBACK_MESSAGE_TYPE_ADD_CUSTOMER = "add_external_contact";
    public final String WECOM_CALLBACK_MESSAGE_TYPE_EDIT_CUSTOMER = "edit_external_contact";
    public final String WECOM_CALLBACK_MESSAGE_TYPE_CHANGE_CUSTOMER = "change_external_contact";

    public final String WECOM_CALLBACK_MESSAGE_TYPE_DELETE_CUSTOMER = "del_external_contact";
    public final String WECOM_CALLBACK_MESSAGE_TYPE_DELETE_FOLLOW_USER = "del_follow_user";
    public final String WECOM_CALLBACK_MESSAGE_TYPE_TRANSFER_FAIL = "transfer_fail";


    public final String WECOM_CALLBACK_MESSAGE_TYPE_EVENT_GROUP_CHAT = "change_external_chat";

    public final String WECOM_CALLBACK_MESSAGE_TYPE_CREATE_GROUP_CHAT = "create";
    public final String WECOM_CALLBACK_MESSAGE_TYPE_UPDATE_GROUP_CHAT = "update";
    public final String WECOM_CALLBACK_MESSAGE_TYPE_DISMISS_GROUP_CHAT = "dismiss";


    public final String WECOM_CALLBACK_MESSAGE_TYPE_EVENT_CORP_TAG = "change_external_tag";

    public final String WECOM_CALLBACK_MESSAGE_TYPE_CREATE_CORP_TAG = "create";
    public final String WECOM_CALLBACK_MESSAGE_TYPE_UPDATE_CORP_TAG = "update";
    public final String WECOM_CALLBACK_MESSAGE_TYPE_DELETE_CORP_TAG = "delete";
    public final String WECOM_CALLBACK_MESSAGE_TYPE_SHUFFLE_CORP_TAG = "shuffle";

    public final String WECOM_CALLBACK_FORWARD_URL="wecom_callback_forward_url_%s";

    public final String WECOM_CALLBACK_CUSTOMER_FORWARD_URL="wecom_callback_customer_forward_url_%s";
}
