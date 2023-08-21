package com.easy.marketgo.common.constants.wecom;

import lombok.experimental.UtilityClass;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-27 21:23
 * Describe:
 */
@UtilityClass
public class WeComHttpConstants {
    /**
     * 企微官方接口URL
     */
    public final String REQUEST_PREFIX_URL = "https://qyapi.weixin.qq.com/";

    public final String AGENT_ACCESS_TOKEN_URL = REQUEST_PREFIX_URL + "cgi-bin/gettoken";

    public final String QUERY_AGENT_DETAIL_URL = REQUEST_PREFIX_URL + "cgi-bin/agent/get";

    //发送群发消息给朋友圈
    public final String SEND_MOMENT_MASS_TASK_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact/add_moment_task";

    //发送群发消息给个人和客户群
    public final String SEND_SINGLE_OR_GROUP_MASS_TASK_URL = REQUEST_PREFIX_URL + "/cgi-bin/externalcontact" +
            "/add_msg_template";

    //获取群发成员发送任务列表
    public final String QUERY_MASS_TASK_MEMBER_RESULT_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact" +
            "/get_groupmsg_task";

    //获取企业群发成员执行结果
    public final String QUERY_MASS_TASK_EXTERNAL_USER_RESULT_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact" +
            "/get_groupmsg_send_result";

    //获取任务创建结果
    public final String QUERY_MOMENT_MASS_TASK_CREATE_RESULT_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact" +
            "/get_moment_task_result";

    //获取任务创建结果
    public final String QUERY_MOMENT_MASS_TASK_COMMENTS_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact" +
            "/get_moment_comments";

    //获取任务创建结果
    public final String QUERY_MOMENT_MASS_TASK_SEND_RESULT_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact" +
            "/get_moment_send_result";

    //获取任务创建结果
    public final String QUERY_MOMENT_MASS_TASK_PUBLISH_RESULT_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact" +
            "/get_moment_task";

    //获取获取客户群列表
    public final String QUERY_GROUP_CHAT_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact/groupchat/list";

    //获取获取客户群成员列表
    public final String QUERY_GROUP_CHAT_MEMBER_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact/groupchat/get";

    /***** 企微素材上传接口 ****/
    // 临时素材
    public final String UPLOAD_TEMP_MEDIA_URL = REQUEST_PREFIX_URL + "cgi-bin/media/upload";
    // 图片素材（永久）
    public final String UPLOAD_IMAGE_URL = REQUEST_PREFIX_URL + "cgi-bin/media/uploadimg";
    // 附件资源（朋友圈素材/商品图册）
    public final String UPLOAD_ATTACHMENT_URL = REQUEST_PREFIX_URL + "cgi-bin/media/upload_attachment";

    //获取部门列表
    public static final String QUERY_DEPARTMENT_LIST_URL = REQUEST_PREFIX_URL + "cgi-bin/department/simplelist";

    //获取部门详情
    public static final String QUERY_DEPARTMENT_DETAIL_URL = REQUEST_PREFIX_URL + "cgi-bin/department/get";

    //获取部门列表
    public static final String QUERY_DEPARTMENT_MEMBER_LIST_URL = REQUEST_PREFIX_URL + "cgi-bin/user/list";

    //获取部门列表
    public static final String QUERY_MEMBER_LIST_URL = REQUEST_PREFIX_URL + "cgi-bin/user/list_id";

    //获取员工的客户列表
    public static final String QUERY_EXTERNALUSER_LIST_FOR_MEMBER_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact" +
            "/list";

    //获取的客户列表
    public static final String QUERY_EXTERNALUSER_DETAIL_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact/get";

    //获取的企业标签列表
    public final String QUERY_CORP_TAG_LIST_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact/get_corp_tag_list";

    //获取的企业标签列表
    public final String ADD_CORP_TAG_LIST_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact/add_corp_tag";

    //编辑企业标签
    public final String EDIT_CORP_TAG_LIST_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact/edit_corp_tag";

    //删除企业标签
    public final String DELETE_CORP_TAG_LIST_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact/del_corp_tag";

    public final String WECOM_CORP_TICKET_URL = REQUEST_PREFIX_URL + "cgi-bin/get_jsapi_ticket";
    public final String WECOM_AGENT_TICKET_URL = REQUEST_PREFIX_URL + "cgi-bin/ticket/get";

    public final String QUERY_MEMBER_DETAIL_URL = REQUEST_PREFIX_URL + "cgi-bin/user/get";
    public final String MEMBERID_GET_FOR_OAUTH2 = REQUEST_PREFIX_URL + "cgi-bin/user/getuserinfo";

    public final String SEND_WELCOME_MSG_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact/send_welcome_msg";

    public final String EDIT_EXTERNAL_USER_CORP_TAGS_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact/mark_tag";


    public final String ADD_CONTACT_WAY_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact/add_contact_way";

    public final String UPDATE_CONTACT_WAY_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact/update_contact_way";

    public final String DELETE_CONTACT_WAY_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact/del_contact_way";

    // 企微通知接口
    public final String SEND_AGNET_MESSAGE_URL = REQUEST_PREFIX_URL + "cgi-bin/message/send";

    // 提醒成员群发
    public final String REMIND_MESSAGE_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact/remind_groupmsg_send";

    // 停止企业群发
    public final String STOP_MASS_TASK_MESSAGE_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact/cancel_groupmsg_send";

    // 停止朋友圈群发
    public final String STOP_MOMENT_MASS_TASK_MESSAGE_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact" +
            "/cancel_moment_task";

    public final String GROUP_CHAT_WELCOME_MSG_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact" +
            "/group_welcome_template/add";

    public final String EDIT_GROUP_CHAT_WELCOME_MSG_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact" +
            "/group_welcome_template/edit";

    public final String DELETE_GROUP_CHAT_WELCOME_MSG_URL = REQUEST_PREFIX_URL + "cgi-bin/externalcontact" +
            "/group_welcome_template/del";

    /**
     *
     */
    public final String CORP_TICKET_KEY = "wecom_ticket_%s";
    public final String AGENT_TICKET_KEY = "wecom_ticket_%s_%s";
    public final String TYPE = "type";
    public final String AGENT_CONFIG = "agent_config";

    public final String PROVIDER_ACCESS_TOKEN_KEY = "wecom_provider_token_%s_%s";
    public final String SUITE_ACCESS_TOKEN_KEY = "wecom_suite_token_%s";
    public final String PROVIDER_SUITE_TICKET_KEY = "wecom_suite_ticket_%s";

    public final String MEDIA = "meida";

    public final String TICKET_TYPE = "type";
    public final String CORPID = "corpid";
    public final String AGENTID = "agentid";
    public final String AUTH_VERIFY_CODE = "code";
    public final String CORPSECRET = "corpsecret";
    public final String AGENT_ACCESS_TOKEN = "access_token";
    public final String DEPARTMENTS_ID = "id";
    public final String DEPARTMENT_MEMBER_ID = "department_id";
    public final String NONCESTR = "MARKETGO_WECOM_NONCESTR";
    public final String STATE = "state";
    public final String TEMPLATEID_LIST = "templateid_list";

    public final String WECOM_AUTH_STATE_NEW = "NEW";
    public final String WECOM_AUTH_STATE_REAUTH = "REAUTH";

    public final String WECOM_AGENT_TYPE_THIRD_PARTY = "THIRD_PARTY";
    public final String WECOM_AGENT_TYPE_SELF_BUILD = "SELF_BUILD";

    public final String WECOM_AUTH_TYPE_AUTHORIZED = "AUTHORIZED";
    public final String WECOM_AUTH_TYPE_UNAUTHORIZED = "UNAUTHORIZED";

    public final String WECOM_CALLBACK_MESSAGE_TYPE_CREATE_AUTH = "create_auth";
    public final String WECOM_CALLBACK_MESSAGE_TYPE_SUITE_TICKET = "suite_ticket";
    public final String WECOM_CALLBACK_MESSAGE_TYPE_CHANGE_AUTH = "change_auth";
    public final String WECOM_CALLBACK_MESSAGE_TYPE_CANCEL_AUTH = "cancel_auth";




    public final String WECOM_CALLBACK_MESSAGE_TYPE_RESET_SECRET = "reset_permanent_code";

    public final String PROVIDER_SECRET = "provider_secret";
    public final String PROVIDER_ACCESS_TOKEN = "provider_access_token";
    //服务商凭证接口
    public final String PROVIDER_TOKEN_URL = "cgi-bin/service/get_provider_token";
    //企业授权接口
    public final String SUITE_TOKEN_URL = "cgi-bin/service/get_suite_token";
    //授权二维码接口
    public final String CUSTOMIZED_AUTH_URL = "cgi-bin/service/get_customized_auth_url";

    //授权二维码接口
    public final String GET_PERMANENT_CODE = "cgi-bin/service/get_permanent_code";

    //授权二维码接口
    public final String GET_AGENT_DETAIL = "cgi-bin/agent/get";

    //部门列表接口
    public final String GET_DEPT_LIST_URL = "cgi-bin/department/list";


    // 请求企微平台地址


    public final String SEND_MESSAGE_TOPIC = "wecom_send_app_message_topic";
    public final String MESSAGE_SEND_DUP_KEY = "wecom_message_send_%s";
    public final Integer MESSAGE_SEND_DUP_EXPIRETIME = 3 * 60 * 60;
    public final Integer WECOM_ERRCODE_OK = 200;
    public final Integer WECOM_ERRCODE_ERROR = 400;
    public final String WECOM_ERRMSG_OK = "ok";

    public final String WECOM_CRED_FILE_KEY = "wecom_cred_file_%s";
    public final Integer WECOM_CRED_FILE_EXPIRED_TIME = 2 * 24 * 60 * 60;

    public final Integer WECOM_SUITE_TICKET_EXPIRED_TIME = 40 * 60;
    public final String MINIPROGRAM_NOTICE = "miniprogram_notice";
    public final String MPNEWS = "mpnews";
}
