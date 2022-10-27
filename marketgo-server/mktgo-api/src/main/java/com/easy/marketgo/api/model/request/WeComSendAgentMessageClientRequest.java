package com.easy.marketgo.api.model.request;

import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/6/22 9:59 PM
 * Describe:
 */
@Data
public class WeComSendAgentMessageClientRequest extends BaseRpcRequest {

    /**
     * 员工list,最多支持1000个
     */
    private List<String> toUser;

    private MsgTypeEnum msgType;

    private String content;

    private String msgId;

    /**
     * 支持重复消息检查，当指定 "enable_duplicate_check": 1开启: 表示在一定时间间隔内，同样内容（请求json）的消息，不会重复收到；
     * 时间间隔可通过duplicate_check_interval指定，默认1800秒
     */
    private Integer enableDuplicateCheck;

    private Integer duplicateCheckInterval;

    /**
     * 推送消息类型
     */
    public enum MsgTypeEnum {
        TEXTCARD("TEXTCARD"),

        TEXT("TEXT"),

        IMAGE("IMAGE"),

        MINI_PROGRAM_PAGE("MINI_PROGRAM_PAGE"),

        NEWS("NEWS"),

        TEMPLATE_CARD("TEMPLATE_CARD");

        private String value;

        MsgTypeEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static MsgTypeEnum fromValue(String text) {
            for (MsgTypeEnum b : MsgTypeEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }
}
