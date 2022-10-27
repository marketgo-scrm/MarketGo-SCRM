package com.easy.marketgo.web.model.request;

import com.easy.marketgo.web.model.response.corp.WeComCorpConfigResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/22/22 11:16 PM
 * Describe:
 */
@Data
@ApiModel(description = "企微配置企业信息request")
public class WeComCorpMessageRequest {
    @ApiModelProperty(value = "配置类型；AGNET是自建应用， CONTACTS 通讯录， EXTERNAL_USER  客户联系")
    private String configType;
    @ApiModelProperty(value = "企微配置企业信息")
    private WeComCorpConfigResponse.CorpConfig corp;
    @ApiModelProperty(value = "企微配置自建应用信息")
    private WeComCorpConfigResponse.AgentConfig agent;
    @ApiModelProperty(value = "企微配置通讯录应用信息")
    private WeComCorpConfigResponse.ContactsConfig contacts;
    @ApiModelProperty(value = "企微配置外部联系人应用信息")
    private WeComCorpConfigResponse.ContactsConfig externalUser;

    @Data
    public static class CorpConfig {
        @ApiModelProperty(value = "企业名称，不能为空")
        private String corpName;
        @ApiModelProperty(value = "企业ID，不能为空")
        private String corpId;
    }

    @Data
    public static class AgentConfig {
        @ApiModelProperty(value = "应用ID，不能为空")
        private String agentId;
        @ApiModelProperty(value = "应用secret，不能为空")
        private String secret;
    }

    @Data
    public static class ContactsConfig {
        @ApiModelProperty(value = "通讯录，外部联系人的secret")
        private String secret;
        @ApiModelProperty(value = "通讯录，外部联系人的要配置的url")
        private String url;
        @ApiModelProperty(value = "通讯录，外部联系人的要配置的token")
        private String token;
        @ApiModelProperty(value = "通讯录，外部联系人的要配置的encodingAesKey")
        private String encodingAesKey;
    }
}
