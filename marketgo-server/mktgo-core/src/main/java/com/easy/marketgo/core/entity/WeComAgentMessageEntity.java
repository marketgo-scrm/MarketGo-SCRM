package com.easy.marketgo.core.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-30 22:55
 * Describe:
 */
@Data
@Accessors(chain = true)
@Table("wecom_agent_config")
public class WeComAgentMessageEntity extends BaseEntity {
    private String projectUuid;

    private String name;

    private String agentId;

    private String secret;

    private String corpId;

    private Boolean isChief;

    private String fileName;

    private String fileContent;

    private String homePage;

    private String enableStatus;

    private String authStatus;
}
