package com.easy.marketgo.core.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/24/22 3:18 PM
 * Describe:
 */
@Data
@Accessors(chain = true)
@Table("wecom_corp_config")
public class WeComCorpMessageEntity extends BaseEntity {

    private String projectUuid;

    private String corpName;

    private String corpId;

    private String contactsSecret;

    private String contactsToken;

    private String contactsEncodingAesKey;

    private String externalUserSecret;

    private String externalUserToken;

    private String externalUserEncodingAesKey;

    private String forwardAddress;

    private String forwardCustomerAddress;

    private String credFileName;

    private String credFileContent;
}
