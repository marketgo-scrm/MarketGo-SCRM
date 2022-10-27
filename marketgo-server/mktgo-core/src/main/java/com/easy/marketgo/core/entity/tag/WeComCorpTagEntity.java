package com.easy.marketgo.core.entity.tag;

import com.easy.marketgo.core.entity.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/7/22 7:24 PM
 * Describe:
 */
@Data
@Accessors(chain = true)
@Table("wecom_corp_tags")
public class WeComCorpTagEntity extends BaseEntity {
    private String corpId;
    private String groupId;
    private String tagId;
    private String name;
    private Integer order;
    private Boolean deleted;
    private Boolean type;
    private Date addTime;
}
