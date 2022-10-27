package com.easy.marketgo.common.message.messagebuilder;

import com.easy.marketgo.common.constants.WeComConstants;
import com.easy.marketgo.common.message.WeComMessage;

/**
 * <pre>
 * markdown类型的消息builder
 * Created by Binary Wang on 2019/1/20.
 * </pre>
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
public class MarkdownMsgBuilder extends BaseBuilder<MarkdownMsgBuilder> {
  private String content;

  public MarkdownMsgBuilder() {
    this.msgType = WeComConstants.KefuMsgType.MARKDOWN;
  }

  public MarkdownMsgBuilder content(String content) {
    this.content = content;
    return this;
  }

  @Override
  public WeComMessage build() {
    WeComMessage m = super.build();
    m.setContent(this.content);
    return m;
  }
}
