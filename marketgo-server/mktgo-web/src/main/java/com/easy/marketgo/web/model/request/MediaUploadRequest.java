package com.easy.marketgo.web.model.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/19/22 10:42 PM
 * Describe:
 */
@Data
public class MediaUploadRequest {
    private MultipartFile multipartFile;
    private String mediaType;
    private String taskType;
}
