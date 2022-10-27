package com.easy.marketgo.common.message.messagebuilder;

import com.easy.marketgo.common.constants.WeComConstants;
import com.easy.marketgo.common.message.WeComMessage;

/**
 * 视频消息builder
 * <pre>
 * 用法: WxCustomMessage m = WxCustomMessage.VOICE()
 *                              .mediaId(...)
 *                              .title(...)
 *                              .thumbMediaId(..)
 *                              .description(..)
 *                              .toUser(...)
 *                              .build();
 * </pre>
 *
 * @author Daniel Qian
 */
public final class VideoBuilder extends BaseBuilder<VideoBuilder> {
    private String mediaId;
    private String title;
    private String description;
    private String thumbMediaId;

    public VideoBuilder() {
        this.msgType = WeComConstants.KefuMsgType.VIDEO;
    }


    VideoBuilder mediaId(String mediaId) {
        this.mediaId = mediaId;
        return this;
    }

    public VideoBuilder title(String title) {
        this.title = title;
        return this;
    }

    public VideoBuilder description(String description) {
        this.description = description;
        return this;
    }

    public VideoBuilder thumbMediaId(String thumb_media_id) {
        this.thumbMediaId = thumb_media_id;
        return this;
    }

    @Override
    public WeComMessage build() {
        WeComMessage m = super.build();
        m.setMediaId(this.mediaId);
        m.setTitle(this.title);
        m.setDescription(this.description);
        m.setThumbMediaId(this.thumbMediaId);
        return m;
    }
}
