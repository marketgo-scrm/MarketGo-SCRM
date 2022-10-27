package com.easy.marketgo.mktgotest.callback.model;

import com.easy.marketgo.common.converter.XStreamCDataConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 9/1/22 6:05 PM
 * Describe:
 */
@Data
@Slf4j
@XStreamAlias("xml")
public class ParseXmlMessage {
    @XStreamAlias("Encrypt")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String encrypt;

    @XStreamAlias("MsgSignature")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String msgSignature;

    @XStreamAlias("TimeStamp")
    private String timeStamp;

    @XStreamAlias("Nonce")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String nonce;
}
