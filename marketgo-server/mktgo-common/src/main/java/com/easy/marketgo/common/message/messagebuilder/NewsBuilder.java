package com.easy.marketgo.common.message.messagebuilder;

import com.easy.marketgo.common.constants.WeComConstants;
import com.easy.marketgo.common.message.WeComMessage;
import com.easy.marketgo.common.message.bean.article.NewArticle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 图文消息builder
 * <pre>
 * 用法:
 * WxCustomMessage m = WxCustomMessage.NEWS().addArticle(article).toUser(...).build();
 * </pre>
 *
 * @author Daniel Qian
 */
public final class NewsBuilder extends BaseBuilder<NewsBuilder> {

    private List<NewArticle> articles = new ArrayList<>();

    public NewsBuilder() {
        this.msgType = WeComConstants.KefuMsgType.NEWS;
    }

    public NewsBuilder addArticle(NewArticle... articles) {
        Collections.addAll(this.articles, articles);
        return this;
    }

    public NewsBuilder articles(List<NewArticle> articles) {
        this.articles = articles;
        return this;
    }

    @Override
    public WeComMessage build() {
        WeComMessage m = super.build();
        m.setArticles(this.articles);
        return m;
    }
}
