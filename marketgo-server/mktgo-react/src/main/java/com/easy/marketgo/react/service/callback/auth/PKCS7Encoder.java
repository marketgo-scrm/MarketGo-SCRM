/**
 * 对企业微信发送给企业后台的消息加解密示例代码.
 *
 * @copyright Copyright (c) 1998-2014 Tencent Inc.
 */

// ------------------------------------------------------------------------

package com.easy.marketgo.react.service.callback.auth;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * 提供基于PKCS7算法的加解密接口.
 */
class PKCS7Encoder {
  static Charset charset = Charset.forName("utf-8");
  static int blockSize = 32;

  private PKCS7Encoder() {
  }

  /**
   * 获得对明文进行补位填充的字节.
   *
   * @param count 需要进行填充补位操作的明文字节个数
   * @return 补齐用的字节数组
   */
  static byte[] encode(int count) {
    // 计算需要填充的位数
    int amountToPad = blockSize - (count % blockSize);
    if (amountToPad == 0) {
      amountToPad = blockSize;
    }
    // 获得补位所用的字符
    char padChr = chr(amountToPad);
    StringBuilder tmp = new StringBuilder();
    for (int index = 0; index < amountToPad; index++) {
      tmp.append(padChr);
    }
    return tmp.toString().getBytes(charset);
  }

  /**
   * 删除解密后明文的补位字符
   *
   * @param decrypted 解密后的明文
   * @return 删除补位字符后的明文
   */
  static byte[] decode(byte[] decrypted) {
    int pad = (int) decrypted[decrypted.length - 1];
    if (pad < 1 || pad > 32) {
      pad = 0;
    }
    return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);
  }

  /**
   * 将数字转化成ASCII码对应的字符，用于对明文进行补码
   *
   * @param a 需要转化的数字
   * @return 转化得到的字符
   */
  static char chr(int a) {
    byte target = (byte) (a & 0xFF);
    return (char) target;
  }

}
