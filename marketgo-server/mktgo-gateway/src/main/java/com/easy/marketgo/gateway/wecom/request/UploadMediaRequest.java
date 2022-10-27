package com.easy.marketgo.gateway.wecom.request;

import com.easy.marketgo.common.enums.WeComAttachmentTypeEnum;
import com.easy.marketgo.common.enums.WeComMediaTypeEnum;
import lombok.Data;
import lombok.ToString;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-06-06 10:19
 * Describe:
 */
@Data
public class UploadMediaRequest {
    /**
     * 文件名称，必需
     */
    private String filename;

    /**
     * 二进制文件
     */
    @ToString.Exclude
    private byte[] fileData;

    /**
     * 文件类型，上传临时素材时必需
     */
    private WeComMediaTypeEnum mediaType;

    /**
     * 附件类型（1：朋友圈， 2：商品图册）
     */
    private WeComAttachmentTypeEnum attachmentType;

    /**
     * true: 上传临时素材， false: 上传永久素材（图片类型）
     */
    private Boolean tempUpload;
}
