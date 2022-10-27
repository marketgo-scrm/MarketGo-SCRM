package com.easy.marketgo.common.message;

import com.easy.marketgo.common.converter.XStreamCDataConverter;
import com.easy.marketgo.common.crypto.WeComCryptUtil;
import com.easy.marketgo.common.message.messagebuilder.ImageBuilder;
import com.easy.marketgo.common.message.messagebuilder.NewsBuilder;
import com.easy.marketgo.common.message.messagebuilder.TextBuilder;
import com.easy.marketgo.common.message.messagebuilder.VideoBuilder;
import com.easy.marketgo.common.message.messagebuilder.VoiceBuilder;
import com.easy.marketgo.common.storage.WeComConfigStorage;
import com.easy.marketgo.common.xml.XStreamTransformer;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import lombok.Data;

import java.io.Serializable;

/**
 * 被动回复消息.
 * https://work.weixin.qq.com/api/doc#12975
 *
 * @author Daniel Qian
 */
@XStreamAlias("xml")
@Data
public abstract class WeComXmlOutMessage implements Serializable {
  private static final long serialVersionUID = 1418629839964153110L;

  @XStreamAlias("ToUserName")
  @XStreamConverter(value = XStreamCDataConverter.class)
  protected String toUserName;

  @XStreamAlias("FromUserName")
  @XStreamConverter(value = XStreamCDataConverter.class)
  protected String fromUserName;

  @XStreamAlias("CreateTime")
  protected Long createTime;

  @XStreamAlias("MsgType")
  @XStreamConverter(value = XStreamCDataConverter.class)
  protected String msgType;

  /**
   * 获得文本消息builder.
   */
  public static TextBuilder TEXT() {
    return new TextBuilder();
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

  protected String toXml() {
    return XStreamTransformer.toXml((Class) this.getClass(), this);
  }

  /**
   * 转换成加密的xml格式.
   */
  public String toEncryptedXml(WeComConfigStorage storage) {
    String plainXml = toXml();
    WeComCryptUtil pc = new WeComCryptUtil(storage);
    return pc.encrypt(plainXml);
  }
}
