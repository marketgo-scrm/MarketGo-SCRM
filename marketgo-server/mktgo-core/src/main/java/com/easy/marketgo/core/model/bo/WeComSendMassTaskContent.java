package com.easy.marketgo.core.model.bo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/2/22 6:06 PM
 * Describe:
 */
@Data
public class WeComSendMassTaskContent {
    public enum TypeEnum {
        TEXT("TEXT"),

        IMAGE("IMAGE"),

        VOICE("VOICE"),

        VIDEO("VIDEO"),

        FILE("FILE"),

        MINIPROGRAM("MINIPROGRAM"),

        LINK("LINK");

        private String value;

        TypeEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static TypeEnum fromValue(String text) {
            for (TypeEnum b : TypeEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    private TypeEnum type;

    private WeComSendMassTaskContentForTextMsg text;

    private WeComSendMassTaskContentForImageMsg image;

    private WeComSendMassTaskContentForVideoMsg video;

    private WeComSendMassTaskContentForFileMsg file;

    private WeComSendMassTaskContentForMiniProgramMsg miniProgram;

    private WeComSendMassTaskContentForLinkMsg link;


    @Data
    public static class WeComSendMassTaskContentForImageMsg {
        private String mediaUuid;

        private String imageContent;
    }

    @Data
    public static class WeComSendMassTaskContentForFileMsg {
        private String mediaUuid;

        private String title;

        private String fileName;

        private TypeEnum type;

        private Long size;

        /**
         * 文件类型
         */
        public enum TypeEnum {
            WORD("WORD"),

            EXCEL("EXCEL"),

            PPT("PPT"),

            PDF("PDF");

            private String value;

            TypeEnum(String value) {
                this.value = value;
            }

            @JsonValue
            public String getValue() {
                return value;
            }

            @Override
            public String toString() {
                return String.valueOf(value);
            }

            @JsonCreator
            public static WeComSendMassTaskContentForFileMsg.TypeEnum fromValue(String text) {
                for (WeComSendMassTaskContentForFileMsg.TypeEnum b :
                        WeComSendMassTaskContentForFileMsg.TypeEnum.values()) {
                    if (String.valueOf(b.value).equals(text)) {
                        return b;
                    }
                }
                return null;
            }
        }
    }

    @Data
    public static class WeComSendMassTaskContentForLinkMsg {
        private String mediaUuid;

        private String title;

        private String desc;

        private String url;

        private String imageContent;
    }

    @Data
    public static class WeComSendMassTaskContentForMiniProgramMsg {
        private String mediaUuid;

        private String appId;

        private String page;

        private String title;

        private String imageContent;
    }

    @Data
    public static class WeComSendMassTaskContentForTextMsg {
        private String content;
    }

    @Data
    public static class WeComSendMassTaskContentForVideoMsg {
        private String mediaUuid;

        private String title;

        private String imageContent;
    }
}
