package com.easy.marketgo.gateway.wecom.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-06-06 10:28
 * Describe:
 */
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendAgentMessageRequest {
    @JsonProperty("touser")
    private String touser;
    @JsonProperty("toparty")
    private String toparty;
    @JsonProperty("totag")
    private String totag;
    @JsonProperty("msgtype")
    private String msgtype;
    @JsonProperty("agentid")
    private Integer agentid;
    @JsonProperty("textcard")
    private TextCard textcard;
    @JsonProperty("text")
    private Text text;
    @JsonProperty("image")
    private Image image;
    @JsonProperty("miniprogram_notice")
    private MiniprogramNotice miniprogramNotice;
    @JsonProperty("news")
    private News news;
    @JsonProperty("template_card")
    private TemplateCard templateCard;
    @JsonProperty("enable_id_trans")
    private Integer enableIdTrans;
    @JsonProperty("enable_duplicate_check")
    private Integer enableDuplicateCheck;
    @JsonProperty("duplicate_check_interval")
    private Integer duplicateCheckInterval;

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TextCard {
        private String title;
        private String description;
        private String url;
        private String btntxt;
    }

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Text {
        private String content;
    }

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Image {
        private String mediaId;
    }

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class News {
        private List<MpnewsArticle> articles;

        @Data
        @ToString
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        static class MpnewsArticle {
            private String title;
            private String description;
            private String url;
            private String picurl;
            private String appid;
            private String pagepath;
        }
    }



    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MiniprogramNotice {
        private String appid;
        private String page;
        private String title;
        private String description;
        private Boolean emphasisFirstItem;
        private List<ContentItem> contentItem;
        @AllArgsConstructor
        @Data
        static class ContentItem {
            private String key;
            private String value;
        }
    }

    @NoArgsConstructor
    @Data
    public static class TemplateCard {
        @JsonProperty("card_type")
        private String cardType;
        @JsonProperty("source")
        private Source source;
        @JsonProperty("action_menu")
        private ActionMenu actionMenu;
        @JsonProperty("task_id")
        private String taskId;
        @JsonProperty("main_title")
        private MainTitle mainTitle;
        @JsonProperty("quote_area")
        private QuoteArea quoteArea;
        @JsonProperty("emphasis_content")
        private EmphasisContent emphasisContent;
        @JsonProperty("sub_title_text")
        private String subTitleText;
        @JsonProperty("horizontal_content_list")
        private List<HorizontalContentList> horizontalContentList;
        @JsonProperty("jump_list")
        private List<JumpList> jumpList;
        @JsonProperty("card_action")
        private CardAction cardAction;


        @NoArgsConstructor
        @Data
        public static class Source {
            @JsonProperty("icon_url")
            private String iconUrl;
            @JsonProperty("desc")
            private String desc;
            @JsonProperty("desc_color")
            private Integer descColor;
        }


        @NoArgsConstructor
        @Data
        public static class ActionMenu {
            @JsonProperty("desc")
            private String desc;
            @JsonProperty("action_list")
            private List<ActionList> actionList;


            @NoArgsConstructor
            @Data
            public static class ActionList {
                @JsonProperty("text")
                private String text;
                @JsonProperty("key")
                private String key;
            }
        }


        @NoArgsConstructor
        @Data
        public static class MainTitle {
            @JsonProperty("title")
            private String title;
            @JsonProperty("desc")
            private String desc;
        }


        @NoArgsConstructor
        @Data
        public static class QuoteArea {
            @JsonProperty("type")
            private Integer type;
            @JsonProperty("url")
            private String url;
            @JsonProperty("title")
            private String title;
            @JsonProperty("quote_text")
            private String quoteText;
        }


        @NoArgsConstructor
        @Data
        public static class EmphasisContent {
            @JsonProperty("title")
            private String title;
            @JsonProperty("desc")
            private String desc;
        }


        @NoArgsConstructor
        @Data
        public static class CardAction {
            @JsonProperty("type")
            private Integer type;
            @JsonProperty("url")
            private String url;
            @JsonProperty("appid")
            private String appid;
            @JsonProperty("pagepath")
            private String pagepath;
        }


        @NoArgsConstructor
        @Data
        public static class HorizontalContentList {
            @JsonProperty("keyname")
            private String keyname;
            @JsonProperty("value")
            private String value;
            @JsonProperty("type")
            private Integer type;
            @JsonProperty("url")
            private String url;
            @JsonProperty("media_id")
            private String mediaId;
            @JsonProperty("userid")
            private String userid;
        }


        @NoArgsConstructor
        @Data
        public static class JumpList {
            @JsonProperty("type")
            private Integer type;
            @JsonProperty("title")
            private String title;
            @JsonProperty("url")
            private String url;
            @JsonProperty("appid")
            private String appid;
            @JsonProperty("pagepath")
            private String pagepath;
        }
    }
}
