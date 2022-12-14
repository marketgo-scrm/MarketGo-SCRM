package com.easy.marketgo.common.message;

import com.easy.marketgo.common.constants.WeComConstants;
import com.easy.marketgo.common.message.bean.article.MpnewsArticle;
import com.easy.marketgo.common.message.bean.article.NewArticle;
import com.easy.marketgo.common.message.bean.taskcard.TaskCardButton;
import com.easy.marketgo.common.message.messagebuilder.FileBuilder;
import com.easy.marketgo.common.message.messagebuilder.ImageBuilder;
import com.easy.marketgo.common.message.messagebuilder.MarkdownMsgBuilder;
import com.easy.marketgo.common.message.messagebuilder.MiniProgramNoticeMsgBuilder;
import com.easy.marketgo.common.message.messagebuilder.MpnewsBuilder;
import com.easy.marketgo.common.message.messagebuilder.NewsBuilder;
import com.easy.marketgo.common.message.messagebuilder.TaskCardBuilder;
import com.easy.marketgo.common.message.messagebuilder.TextBuilder;
import com.easy.marketgo.common.message.messagebuilder.TextCardBuilder;
import com.easy.marketgo.common.message.messagebuilder.VideoBuilder;
import com.easy.marketgo.common.message.messagebuilder.VoiceBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.easy.marketgo.common.constants.WeComConstants.KefuMsgType.*;


/**
 * 消息.
 *
 * @author Daniel Qian
 */
@Data
public class WeComMessage implements Serializable {

  private String toUser;
  private String toParty;
  private String toTag;
  private Integer agentId;
  private String msgType;
  private String content;
  private String mediaId;
  private String thumbMediaId;
  private String title;
  private String description;
  private String musicUrl;
  private String hqMusicUrl;
  private String safe;
  private String url;
  private String btnTxt;
  private List<NewArticle> articles = new ArrayList<>();
  private List<MpnewsArticle> mpnewsArticles = new ArrayList<>();
  private String appId;
  private String page;
  private Boolean emphasisFirstItem;
  private Map<String, String> contentItems;

  /**
   * enable_id_trans
   * 表示是否开启id转译，0表示否，1表示是，默认0
   */
  private Boolean enableIdTrans = false;
  /**
   * enable_duplicate_check
   * 表示是否开启重复消息检查，0表示否，1表示是，默认0
   */
  private Boolean enableDuplicateCheck = false;
  /**
   * duplicate_check_interval
   * 表示是否重复消息检查的时间间隔，默认1800s，最大不超过4小时
   */
  private Integer duplicateCheckInterval;

  /**
   * 任务卡片特有的属性.
   */
  private String taskId;
  private List<TaskCardButton> taskButtons = new ArrayList<>();

  /**
   * 获得文本消息builder.
   */
  public static TextBuilder TEXT() {
    return new TextBuilder();
  }

  /**
   * 获得文本卡片消息builder.
   */
  public static TextCardBuilder TEXTCARD() {
    return new TextCardBuilder();
  }

  /**
   * 获得图片消息builder.
   */
  public static ImageBuilder IMAGE() {
    return new ImageBuilder();
  }

  /**
   * 获得语音消息builder.
   */
  public static VoiceBuilder VOICE() {
    return new VoiceBuilder();
  }

  /**
   * 获得视频消息builder.
   */
  public static VideoBuilder VIDEO() {
    return new VideoBuilder();
  }

  /**
   * 获得图文消息builder.
   */
  public static NewsBuilder NEWS() {
    return new NewsBuilder();
  }

  /**
   * 获得mpnews图文消息builder.
   */
  public static MpnewsBuilder MPNEWS() {
    return new MpnewsBuilder();
  }

  /**
   * 获得markdown消息builder.
   */
  public static MarkdownMsgBuilder MARKDOWN() {
    return new MarkdownMsgBuilder();
  }

  /**
   * 获得文件消息builder.
   */
  public static FileBuilder FILE() {
    return new FileBuilder();
  }

  /**
   * 获得任务卡片消息builder.
   */
  public static TaskCardBuilder TASKCARD() {
    return new TaskCardBuilder();
  }

  /**
   * 获得小程序通知消息builder.
   */
  public static MiniProgramNoticeMsgBuilder newMiniProgramNoticeBuilder() {
    return new MiniProgramNoticeMsgBuilder();
  }

  /**
   * <pre>
   * 请使用.
   * {@link WeComConstants.KefuMsgType#TEXT}
   * {@link WeComConstants.KefuMsgType#IMAGE}
   * {@link WeComConstants.KefuMsgType#VOICE}
   * {@link WeComConstants.KefuMsgType#MUSIC}
   * {@link WeComConstants.KefuMsgType#VIDEO}
   * {@link WeComConstants.KefuMsgType#NEWS}
   * {@link WeComConstants.KefuMsgType#MPNEWS}
   * {@link WeComConstants.KefuMsgType#MARKDOWN}
   * {@link WeComConstants.KefuMsgType#TASKCARD}
   * {@link WeComConstants.KefuMsgType#MINIPROGRAM_NOTICE}
   * </pre>
   *
   * @param msgType 消息类型
   */
  public void setMsgType(String msgType) {
    this.msgType = msgType;
  }

  public String toJson() {
    JsonObject messageJson = new JsonObject();
    if (this.getAgentId() != null) {
      messageJson.addProperty("agentid", this.getAgentId());
    }

    if (StringUtils.isNotBlank(this.getToUser())) {
      messageJson.addProperty("touser", this.getToUser());
    }

    messageJson.addProperty("msgtype", this.getMsgType());

    if (StringUtils.isNotBlank(this.getToParty())) {
      messageJson.addProperty("toparty", this.getToParty());
    }

    if (StringUtils.isNotBlank(this.getToTag())) {
      messageJson.addProperty("totag", this.getToTag());
    }

    if (this.getEnableIdTrans()) {
      messageJson.addProperty("enable_id_trans", 1);
    }

    if (this.getEnableDuplicateCheck()) {
      messageJson.addProperty("enable_duplicate_check", 1);
    }

    if (this.getDuplicateCheckInterval() != null) {
      messageJson.addProperty("duplicate_check_interval", this.getDuplicateCheckInterval());
    }

    this.handleMsgType(messageJson);

    if (StringUtils.isNotBlank(this.getSafe())) {
      messageJson.addProperty("safe", this.getSafe());
    }

    return messageJson.toString();
  }

  private void handleMsgType(JsonObject messageJson) {
    switch (this.getMsgType()) {
      case TEXT: {
        JsonObject text = new JsonObject();
        text.addProperty("content", this.getContent());
        messageJson.add("text", text);
        break;
      }
      case MARKDOWN: {
        JsonObject text = new JsonObject();
        text.addProperty("content", this.getContent());
        messageJson.add("markdown", text);
        break;
      }
      case TEXTCARD: {
        JsonObject text = new JsonObject();
        text.addProperty("title", this.getTitle());
        text.addProperty("description", this.getDescription());
        text.addProperty("url", this.getUrl());
        text.addProperty("btntxt", this.getBtnTxt());
        messageJson.add("textcard", text);
        break;
      }
      case IMAGE: {
        JsonObject image = new JsonObject();
        image.addProperty("media_id", this.getMediaId());
        messageJson.add("image", image);
        break;
      }
      case FILE: {
        JsonObject image = new JsonObject();
        image.addProperty("media_id", this.getMediaId());
        messageJson.add("file", image);
        break;
      }
      case VOICE: {
        JsonObject voice = new JsonObject();
        voice.addProperty("media_id", this.getMediaId());
        messageJson.add("voice", voice);
        break;
      }
      case VIDEO: {
        JsonObject video = new JsonObject();
        video.addProperty("media_id", this.getMediaId());
        video.addProperty("thumb_media_id", this.getThumbMediaId());
        video.addProperty("title", this.getTitle());
        video.addProperty("description", this.getDescription());
        messageJson.add("video", video);
        break;
      }
      case NEWS: {
        JsonObject newsJsonObject = new JsonObject();
        JsonArray articleJsonArray = new JsonArray();
        for (NewArticle article : this.getArticles()) {
          JsonObject articleJson = new JsonObject();
          articleJson.addProperty("title", article.getTitle());
          articleJson.addProperty("description", article.getDescription());
          articleJson.addProperty("url", article.getUrl());
          articleJson.addProperty("picurl", article.getPicUrl());
          articleJsonArray.add(articleJson);
        }
        newsJsonObject.add("articles", articleJsonArray);
        messageJson.add("news", newsJsonObject);
        break;
      }
      case MPNEWS: {
        JsonObject newsJsonObject = new JsonObject();
        if (this.getMediaId() != null) {
          newsJsonObject.addProperty("media_id", this.getMediaId());
        } else {
          JsonArray articleJsonArray = new JsonArray();
          for (MpnewsArticle article : this.getMpnewsArticles()) {
            article2Json(articleJsonArray, article);
          }

          newsJsonObject.add("articles", articleJsonArray);
        }
        messageJson.add("mpnews", newsJsonObject);
        break;
      }
      case TASKCARD: {
        JsonObject text = new JsonObject();
        text.addProperty("title", this.getTitle());
        text.addProperty("description", this.getDescription());

        if (StringUtils.isNotBlank(this.getUrl())) {
          text.addProperty("url", this.getUrl());
        }

        text.addProperty("task_id", this.getTaskId());

        JsonArray buttonJsonArray = new JsonArray();
        for (TaskCardButton button : this.getTaskButtons()) {
          btn2Json(buttonJsonArray, button);
        }
        text.add("btn", buttonJsonArray);

        messageJson.add("taskcard", text);
        break;
      }
      case MINIPROGRAM_NOTICE: {
        JsonObject notice = new JsonObject();
        notice.addProperty("appid", this.getAppId());
        notice.addProperty("page", this.getPage());
        notice.addProperty("description", this.getDescription());
        notice.addProperty("title", this.getTitle());
        notice.addProperty("emphasis_first_item", this.getEmphasisFirstItem());
        JsonArray content = new JsonArray();
        for (Map.Entry<String, String> item : this.getContentItems().entrySet()) {
          JsonObject articleJson = new JsonObject();
          articleJson.addProperty("key", item.getKey());
          articleJson.addProperty("value", item.getValue());
          content.add(articleJson);
        }
        notice.add("content_item", content);

        messageJson.add("miniprogram_notice", notice);
        break;
      }
      default: {
        // do nothing
      }
    }
  }

  private void btn2Json(JsonArray buttonJsonArray, TaskCardButton button) {
    JsonObject buttonJson = new JsonObject();
    buttonJson.addProperty("key", button.getKey());
    buttonJson.addProperty("name", button.getName());

    if (StringUtils.isNotBlank(button.getReplaceName())) {
      buttonJson.addProperty("replace_name", button.getReplaceName());
    }

    if (StringUtils.isNotBlank(button.getColor())) {
      buttonJson.addProperty("color", button.getColor());
    }

    if (button.getBold() != null) {
      buttonJson.addProperty("is_bold", button.getBold());
    }

    buttonJsonArray.add(buttonJson);
  }

  private void article2Json(JsonArray articleJsonArray, MpnewsArticle article) {
    JsonObject articleJson = new JsonObject();
    articleJson.addProperty("title", article.getTitle());
    articleJson.addProperty("thumb_media_id", article.getThumbMediaId());
    articleJson.addProperty("author", article.getAuthor());
    articleJson.addProperty("content_source_url", article.getContentSourceUrl());
    articleJson.addProperty("content", article.getContent());
    articleJson.addProperty("digest", article.getDigest());
    articleJson.addProperty("show_cover_pic", article.getShowCoverPic());
    articleJsonArray.add(articleJson);
  }

}
