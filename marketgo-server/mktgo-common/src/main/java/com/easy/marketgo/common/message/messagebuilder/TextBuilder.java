package com.easy.marketgo.common.message.messagebuilder;

import com.easy.marketgo.common.constants.WeComConstants;
import com.easy.marketgo.common.message.WeComMessage;

/**
 * 文本消息builder
 * <pre>
 * 用法: WxCustomMessage m = WxCustomMessage.TEXT().content(...).toUser(...).build();
 * </pre>
 *
 * @author Daniel Qian
 */
public final class TextBuilder extends BaseBuilder<TextBuilder> {
    private String content;

    public TextBuilder() {
        this.msgType = WeComConstants.KefuMsgType.TEXT;
    }

    public TextBuilder content(String content) {
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
