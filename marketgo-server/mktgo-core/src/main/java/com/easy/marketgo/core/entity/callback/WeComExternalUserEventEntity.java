package com.easy.marketgo.core.entity.callback;

import com.easy.marketgo.core.entity.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/27/22 12:55 PM
 * Describe:
 */
@Data
@Accessors(chain = true)
@Table("wecom_event_external_user")
public class WeComExternalUserEventEntity extends BaseEntity {
    private String eventMd5;
    private String corpId;
    private String memberId;
    private String externalUserId;
    private String eventType;
    private String state;
    private String welcomeCode;
    private String source;
    private String failReason;
    private Date eventTime;
}
