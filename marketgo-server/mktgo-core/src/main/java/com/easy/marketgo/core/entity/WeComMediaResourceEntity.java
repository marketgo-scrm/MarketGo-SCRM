package com.easy.marketgo.core.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/27/22 12:17 PM
 * Describe:
 */
@Data
@Accessors(chain = true)
@Table("wecom_media_resource")
public class WeComMediaResourceEntity extends UuidBaseEntity {
    private String projectUuid;
    private String corpId;
    private String storageType;
    private String name;
    private Boolean isTemp;
    private String mediaType;
    private String fileType;
    private Long fileSize;
    private String mediaId;
    private Boolean isFinish;
    private byte[] mediaData;
    private Date expireTime;
}
