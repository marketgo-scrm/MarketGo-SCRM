package com.easy.marketgo.common.enums;


/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-27 23:27
 * Describe:
 */
public enum ErrorCodeEnum {
    OK(0, "success"),
    /**
     * 内部错误码定义
     * 模块-web
     * 命名方式 ERROR_WEB_错误描述
     * 错误码范围1000-1999
     */

    ERROR_WEB_PARAM_IS_ILLEGAL(1001, "请求参数不合法，请检查参数正确性"),
    ERROR_WEB_AGENT_PARAM(1002, "请求应用参数错误"),
    ERROR_NOT_SUPPORT_MASS_TASK(1003, "不支持的企微群发类型"),
    ERROR_WECOM_MASS_TASK_DUPLICATE_CNAME(1004, "企微活动重名，请修改名称"),
    ERROR_WEB_WECOM_MASS_TASK_CHECK_NAME(1005, "企微群发任务名称检查失败"),
    ERROR_WECOM_PROJECT_DUPLICATE_CNAME(1006, "项目名称重名，请修改项目名称"),
    ERROR_WEB_USER_IS_NOT_CREATE_PERMISSION(1007, "用户没有创建项目的权限，只有超级管理员有创建项目权限"),

    ERROR_WEB_WECOM_MASS_TASK_NOT_SUPPORT_CHANGE(1010, "企微群发任务只有在未开始状态可以修改，其他状态只可以查看"),
    ERROR_WEB_WECOM_TASK_CENTER_NOT_SUPPORT_CHANGE(1011, "企微群发任务只有在未开始状态可以修改，其他状态只可以查看"),

    ERROR_WEB_MEDIA_UNSUPPORTED_FILE_TYPE(1100, "不支持的文件类型"),
    ERROR_WEB_MEDIA_UPLOAD_FAILED(1101, "企业微信素材文件上传失败。"),
    ERROR_WEB_PROJECT_IS_ILLEGAL(1102, "项目信息查询失败。"),
    ERROR_WEB_TENANT_IS_ILLEGAL(1103, "租户信息查询失败。"),
    ERROR_WEB_MEDIA_IS_ILLEGAL(1104, "企业微信素材文件查询失败。"),
    ERROR_WEB_MASS_TASK_IS_EMPTY(1105, "群发任务不存在，请确认此任务是否已经被删除"),
    ERROR_WEB_MASS_TASK_METRICS_TYPE_NOT_SUPPORT(1106, "群发任务查询的统计类型不支持，请确认类型是否正确"),
    ERROR_WEB_MASS_TASK_USER_GROUP_EXTERNAL_USER_IS_EMPTY(1107, "群发客户的人群计算条件缺少客户条件设置"),
    ERROR_WEB_DELETE_TAG_IS_EMPTY(1108, "删除的标签列表是空，请确认参数是否正确"),
    ERROR_WEB_EDIT_TAG_IS_EMPTY(1109, "编辑的标签列表是空，请确认参数是否正确"),
    ERROR_WEB_DATE_TIME_PARAM(1110, "时间参数错误，请确认时间区间是否正确"),
    ERROR_WEB_BASE64_IMAGE_DATA(1111, "计算缩略图失败，请联系管理员解决问题"),
    ERROR_WEB_QUERY_USER_GROUP_MESSAGE_IS_EMPTY(1112, "查询人群信息失败，人群信息不存在"),
    ERROR_WEB_UPLOAD_OFFLINE_USER_GROUP_FILE_NAME_EMPTY(1113, "上传的文件名称是空"),
    ERROR_WEB_UPLOAD_OFFLINE_USER_GROUP_FILE_SIZE_EMPTY(1114, "上传的文件中没有数据"),
    ERROR_WEB_OFFLINE_USER_GROUP_COMPUTE_FAILED(1115, "离线人群计算失败"),
    ERROR_WEB_UPLOAD_FILE_FAILED(1116, "上传文件失败"),
    ERROR_WEB_MASS_TASK_REMIND_COUNT_IS_MAX(1117, "群发提醒的次数已经超过了3次"),

    ERROR_WEB_INTERNAL_SERVICE(1151, "服务内部出现异常，请检查相关服务是否运行正常"),
    ERROR_WEB_REQUEST_USER_GROUP_IS_EMPTY(1152, "数据库中没有查询到人群信息的记录"),
    ERROR_WEB_REQUEST_USER_COMP_IS_EMPTY(1153, "数据库中没有查询到员工信息的记录"),
    ERROR_WEB_REQUEST_USER_PASS_IS_VALID(1154, "用户密码错误"),
    ERROR_WEB_TENANT_IS_EMPTY(1155, "租户信息不存在"),
    ERROR_WEB_TOKEN_IS_EXPIRED(1156, "token已经过期, 请重新登陆"),
    ERROR_WEB_USER_IS_NOT_EXISTS(1157, "用户信息不存在"),
    ERROR_WEB_ROLE_EXISTS(1158, "角色编码或角色名称已经存在"),
    ERROR_WEB_ROLE_ID_EMPTY(1159, "角色id不能为空"),
    ERROR_WEB_ROLE_UUID_EMPTY(1160, "角色id不匹配"),
    ERROR_WEB_PASS_WORD(1161, "密码不一致"),
    ERROR_WEB_BASE_ROLE_IS_EMPTY(1162, "基础角色信息不存在"),
    ERROR_NOT_SUPPORT_ROLE(1163, "不支持授权超级管理员角色"),
    ERROR_WEB_LIVE_CODE_EXISTS(1164, "活码不存在"),
    ERROR_WEB_ROLE_DESC_EMPTY(1165, "角色不能为空"),



    ERROR_WEB_CDP_FORWARD_SETTING(1200, "设置转发服务失败"),
    /**
     * 内部错误码定义
     * 模块-biz
     * 命名方式 ERROR_BIZ_错误描述
     * 错误码范围2000-4999
     */
    ERROR_BIZ_CONTENT_IS_EMPTY(2001, "发送的内容为空"),

    /**
     * 内部错误码定义
     * 模块-gateway
     * 命名方式 ERROR_GATEWAY_错误描述
     * 错误码范围5000-5999
     */
    ERROR_GATEWAY_REQUEST_WECOM(5000, "请求企微开放平台异常"),
    ERROR_GATEWAY_AGENT_PARAM(5001, "请求应用参数错误"),
    ERROR_GATEWAY_PARAM_IS_EMPTY(5002, "请求参数异常"),
    ERROR_GATEWAY_INTERNAL_SERVICE(5003, "服务内部出现异常，请检查相关服务是否运行正常"),
    ERROR_GATEWAY_NOT_SUPPORT_AGENT_MESSAGE_TYPE(5004, "不支持的应用消息类型，请重新确认消息的类型"),

    /**
     * 内部错误码定义
     * 模块-cdp
     * 命名方式 ERROR_CDP_错误描述
     * 错误码范围6000-6999
     */
    ERROR_CDP_RESPONSE_IS_EMPTY(6000, "请求CDP的接口返回的response是空"),
    ERROR_CDP_CROWD_LIST_IS_EMPTY(6001, "请求CDP的分群列表接口返回的列表为空"),

    /**
     * 内部错误码定义
     * 模块-react
     * 命名方式 ERROR_REACT_错误描述
     * 错误码范围7000-7999
     */
    ERROR_REACT_SDK_CONFIG_VERIFY(7000, "sdk config 验证失败"),
    ERROR_REACT_TASK_IS_NOT_EXIST(7001, "请求的任务列表不存在，请确认任务是否存在"),
    ERROR_REACT_TASK_CONTENT_IS_NOT_EXIST(7002, "请求的任务的内容不存在，请确认任务是否存在"),
    ERROR_REACT_TASK_CUSTOMER_LIST_IS_NOT_EXIST(7003, "请求的任务的客户列表不存在，请确认任务是否存在"),
    /**
     * 企微的错误码定义
     * 模块-WECOM
     * 命名方式 ERROR_WECOM_错误描述
     * 错误码范围按照企微的范围值
     */
    ERROR_WECOM_MEDIA_IS_ILLEGAL(48002, "调用企微接口提示：API接口无权限调用。"),
    ERROR_WECOM_AGENT_ID_IS_ILLEGAL(301002, "调用企微接口提示：应用信息错误，请检查应用信息是否配置正确。"),
    ERROR_WECOM_SECRET_IS_ILLEGAL(40001, "调用企微接口提示：应用不合法的secret参数，请检查应用信息是否配置正确。"),
    ERROR_WECOM_ACCESS_TOKEN_IS_ILLEGAL(41001, "调用企微接口提示：应用不合法的访问token，请检查应用信息是否配置正确。"),
    ERROR_WECOM_USER_ID_IS_ILLEGAL(40031, "调用企微接口提示：不合法的UserID列表，列表部分UserID不在通讯录中。"),
    ERROR_WECOM_USER_ID_IS_INVALID(40003, "调用企微接口提示：无效的UserID，保证UserID必须在通讯录中存在。"),
    ERROR_WECOM_EDIT_TAG_IS_DENIED(81011, "调用企微接口提示：无权限标记标签。"),
    ERROR_WECOM_TAG_NAME_IS_SAME(40071, "调用企微接口提示：标签或标签组名称已经存在。"),
    ERROR_WECOM_INVALID_CONFIG_ID(41044, "调用企微接口提示：无效的活码ID"),
    ;


    private Integer code;
    private String message;

    ErrorCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static String getMessage(Integer code, String defaultMessage) {
        for (ErrorCodeEnum value : ErrorCodeEnum.values()) {
            Integer item = value.getCode();
            if (code.equals(item)) {
                return value.getMessage();
            }
        }
        return defaultMessage;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
