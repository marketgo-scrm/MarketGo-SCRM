package com.easy.marketgo.common.crypto;

import com.easy.marketgo.common.storage.WeComConfigStorage;
import com.google.common.base.CharMatcher;
import com.google.common.io.BaseEncoding;
/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-13 12:36:35
 * @description : WeComCryptUtil.java
 */
public class WeComCryptUtil extends BaseCryptUtil {
  public WeComCryptUtil(WeComConfigStorage wxCpConfigStorage) {
    /*
     * @param token          公众平台上，开发者设置的token
     * @param encodingAesKey 公众平台上，开发者设置的EncodingAESKey
     * @param appidOrCorpid          公众平台appid
     */
    String encodingAesKey = wxCpConfigStorage.getAesKey();
    String token = wxCpConfigStorage.getToken();
    String corpId = wxCpConfigStorage.getCorpId();

    this.token = token;
    this.appidOrCorpid = corpId;
    this.aesKey = BaseEncoding.base64().decode(CharMatcher.whitespace().removeFrom(encodingAesKey));
  }

}
