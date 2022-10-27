package com.easy.marketgo.common.message.messagebuilder;


import com.easy.marketgo.common.constants.WeComConstants;
import com.easy.marketgo.common.message.WeComMessage;
import org.apache.commons.lang3.StringUtils;

public class BaseBuilder<T> {
  protected String msgType;
  protected Integer agentId;
  protected String toUser;
  protected String toParty;
  protected String toTag;
  protected String safe;

  public T agentId(Integer agentId) {
    this.agentId = agentId;
    return (T) this;
  }

  public T toUser(String toUser) {
    this.toUser = toUser;
    return (T) this;
  }

  public T toParty(String toParty) {
    this.toParty = toParty;
    return (T) this;
  }

  public T toTag(String toTag) {
    this.toTag = toTag;
    return (T) this;
  }

  public T safe(String safe) {
    this.safe = safe;
    return (T) this;
  }

  public WeComMessage build() {
    WeComMessage m = new WeComMessage();
    m.setAgentId(this.agentId);
    m.setMsgType(this.msgType);
    m.setToUser(this.toUser);
    m.setToParty(this.toParty);
    m.setToTag(this.toTag);
    m.setSafe(StringUtils.defaultIfBlank(this.safe, WeComConstants.KefuMsgSafe.NO));
    return m;
  }

}
