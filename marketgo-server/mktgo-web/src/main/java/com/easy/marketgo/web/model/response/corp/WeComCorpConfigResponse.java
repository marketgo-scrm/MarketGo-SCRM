package com.easy.marketgo.web.model.response.corp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/15/22 4:19 PM
 * Describe:
 */
@Data
@ApiModel(description = "企微企业信息的response")
public class WeComCorpConfigResponse {
    @ApiModelProperty(value = "微企业信息列表")
    private List<ConfigMessage> configs;

    @Data
    public static class ConfigMessage {
        @ApiModelProperty(value = "微企企业信息详情")
        private CorpConfig corp;
        @ApiModelProperty(value = "微企自建应用详情")
        private AgentConfig agent;
        @ApiModelProperty(value = "微企通讯录配置详情")
        private ContactsConfig contacts;
        @ApiModelProperty(value = "微企客户配置详情")
        private ContactsConfig externalUser;
    }

    @Data
    public static class CorpConfig {
        @ApiModelProperty(value = "微企企业名称")
        private String corpName;
        @ApiModelProperty(value = "微企企业ID")
        private String corpId;
    }

    @Data
    public static class AgentConfig {
        @ApiModelProperty(value = "微企自建应用id")
        private String agentId;
        @ApiModelProperty(value = "微企自建应用secret")
        private String secret;
    }

    @Data
    public static class ContactsConfig {
        @ApiModelProperty(value = "微企通讯录、客户的secret")
        private String secret;
        @ApiModelProperty(value = "微企通讯录、客户的配置的url")
        private String url;
        @ApiModelProperty(value = "微企通讯录、客户的token")
        private String token;
        @ApiModelProperty(value = "微企通讯录、客户的aes key")
        private String encodingAesKey;
    }
}
