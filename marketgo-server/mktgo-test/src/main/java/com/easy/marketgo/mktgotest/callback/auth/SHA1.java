/**
 * 对企业微信发送给企业后台的消息加解密示例代码.
 *
 * @copyright Copyright (c) 1998-2014 Tencent Inc.
 */

// ------------------------------------------------------------------------

package com.easy.marketgo.mktgotest.callback.auth;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.util.Arrays;

/**
 * SHA1 class
 * <p>
 * 计算消息签名接口.
 */
@Slf4j
class SHA1 {

  private SHA1() {
  }

  /**
   * 用SHA1算法生成安全签名
   *
   * @param token     票据
   * @param timestamp 时间戳
   * @param nonce     随机字符串
   * @param encrypt   密文
   * @return 安全签名
   * @throws AesException
   */
  public static String getSHA1(String token, String timestamp, String nonce, String encrypt) throws AesException {
    try {
      String[] array = new String[] {token, timestamp, nonce, encrypt};
      StringBuilder sb = new StringBuilder();
      // 字符串排序
      Arrays.sort(array);
      for (int i = 0; i < 4; i++) {
        sb.append(array[i]);
      }
      String str = sb.toString();
      // SHA1签名生成
      MessageDigest md = MessageDigest.getInstance("SHA-1");
      md.update(str.getBytes());
      byte[] digest = md.digest();

      StringBuilder hexstr = new StringBuilder();
      String shaHex = "";
      for (int i = 0; i < digest.length; i++) {
        shaHex = Integer.toHexString(digest[i] & 0xFF);
        if (shaHex.length() < 2) {
          hexstr.append(0);
        }
        hexstr.append(shaHex);
      }
      return hexstr.toString();
    } catch (Exception e) {
      log.error("failed to decode message for getSHA1 function.", e);
      throw new AesException(AesException.COMPUTE_SIGNATURE_ERROR);
    }
  }
}
