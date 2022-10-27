package com.easy.marketgo.common.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-13 12:32:54
 * @description : WxCpGsonBuilder.java
 */
public class WeComGsonBuilder {

  private static final GsonBuilder INSTANCE = new GsonBuilder();

  static {
    INSTANCE.disableHtmlEscaping();
  }

  public static Gson create() {
    return INSTANCE.create();
  }

}
