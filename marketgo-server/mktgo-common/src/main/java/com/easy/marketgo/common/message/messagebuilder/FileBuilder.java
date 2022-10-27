package com.easy.marketgo.common.message.messagebuilder;

import com.easy.marketgo.common.constants.WeComConstants;
import com.easy.marketgo.common.message.WeComMessage;

/**
 * 获得消息builder
 * <pre>
 * 用法: WxCustomMessage m = WxCustomMessage.FILE().mediaId(...).toUser(...).build();
 * </pre>
 *
 * @author Daniel Qian
 */
public final class FileBuilder extends BaseBuilder<FileBuilder> {
    private String mediaId;

    public FileBuilder() {
        this.msgType = WeComConstants.KefuMsgType.FILE;
    }

    public FileBuilder mediaId(String media_id) {
        this.mediaId = media_id;
        return this;
    }

    @Override
    public WeComMessage build() {
        WeComMessage m = super.build();
        m.setMediaId(this.mediaId);
        return m;
    }
}
