package com.easy.marketgo.web.service.wecom.impl;

import cn.hutool.core.date.DateUtil;
import com.easy.marketgo.api.model.request.WeComUploadMediaRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.response.WeComUploadMediaClientResponse;
import com.easy.marketgo.api.service.WeComMediaRpcService;
import com.easy.marketgo.common.enums.*;
import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.utils.FileTypeUtils;
import com.easy.marketgo.common.utils.UuidUtils;
import com.easy.marketgo.common.utils.WeComMediaUtils;
import com.easy.marketgo.core.entity.ProjectConfigEntity;
import com.easy.marketgo.core.entity.TenantConfigEntity;
import com.easy.marketgo.core.entity.WeComAgentMessageEntity;
import com.easy.marketgo.core.entity.WeComMediaResourceEntity;
import com.easy.marketgo.core.repository.media.WeComMediaResourceRepository;
import com.easy.marketgo.core.repository.wecom.ProjectConfigRepository;
import com.easy.marketgo.core.repository.wecom.TenantConfigRepository;
import com.easy.marketgo.core.repository.wecom.WeComAgentMessageRepository;
import com.easy.marketgo.web.model.request.MediaUploadRequest;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.web.model.response.media.MediaUploadResponse;
import com.easy.marketgo.web.service.wecom.MediaUploadService;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/23/22 10:29 PM
 * Describe:
 */
@Slf4j
@Service
public class MediaUploadServiceImpl implements MediaUploadService {

    @Resource
    private WeComMediaRpcService weComMediaRpcService;

    @Autowired
    private TenantConfigRepository tenantConfigRepository;

    @Autowired
    private ProjectConfigRepository projectConfigRepository;

    @Autowired
    private WeComMediaResourceRepository weComMediaResourceRepository;

    @Autowired
    private WeComAgentMessageRepository weComAgentMessageRepository;

    @Override
    public BaseResponse uploadMedia(String projectId, String corpId, MediaUploadRequest request) {
        log.info("start to upload media file.");
        MultipartFile multipartFile = request.getMultipartFile();
        String mediaType = request.getMediaType();
        String taskType = request.getTaskType();

        long fileSize = multipartFile.getSize();
        String contentType = multipartFile.getContentType();
        String filename = multipartFile.getOriginalFilename();
        String fileType = FilenameUtils.getExtension(filename);

        log.info("accept uploadWeComMedia request. projectId={}, corpId={}, mediaType={}, taskType={}, filename={}, " +
                        "fileSize={}, fileType={}, contentType={}", projectId, corpId, mediaType, taskType, filename,
                fileSize, fileType, contentType);
        MediaUploadResponse response = new MediaUploadResponse();
        WeComAgentMessageEntity entity = weComAgentMessageRepository.getWeComAgentByCorp(projectId, corpId);
        String agentId = (entity == null) ? "" : entity.getAgentId();
        WeComMediaTypeEnum mediaTypeEnum = WeComMediaTypeEnum.fromValue(mediaType);
        WeComMassTaskTypeEnum taskTypeEnum = WeComMassTaskTypeEnum.fromValue(taskType);

        checkMediaParams(mediaTypeEnum, taskTypeEnum, corpId, agentId, projectId, filename);

        checkFileLimit(projectId, corpId, agentId, filename, fileType, taskTypeEnum, mediaTypeEnum);

        // file bytes
        byte[] fileBytes;

        try {
            fileBytes = multipartFile.getBytes();
        } catch (IOException e) {
            log.error("failed to get file bytes. projectId={}, corpId={}, agentId={}, mediaType={}, taskType={}, " +
                    "filename={}, fileSize={}", projectId, corpId, agentId, mediaType, taskType, filename, fileSize, e);
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_MEDIA_UPLOAD_FAILED);
        }

        ProjectConfigEntity projectConfigEntity = projectConfigRepository.findAllByUuid(projectId);
        if (projectConfigEntity == null) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_PROJECT_IS_ILLEGAL);
        }

        TenantConfigEntity tenantConfigEntity = tenantConfigRepository.findByUuid(projectConfigEntity.getTenantUuid());
        if (tenantConfigEntity == null) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_TENANT_IS_ILLEGAL);
        }

        String mediaStorageType = tenantConfigEntity.getMediaStorageType();
        try {
            if (mediaTypeEnum == WeComMediaTypeEnum.LOGO || mediaTypeEnum == WeComMediaTypeEnum.QRCODE) {
                // 保存素材表
                WeComMediaResourceEntity weComMediaResourceEntity = new WeComMediaResourceEntity();
                weComMediaResourceEntity.setCorpId(corpId);
                weComMediaResourceEntity.setProjectUuid(projectId);
                weComMediaResourceEntity.setUuid(UuidUtils.generateUuid());
                weComMediaResourceEntity.setMediaId(weComMediaResourceEntity.getUuid());
                weComMediaResourceEntity.setMediaType(mediaTypeEnum.name());
                weComMediaResourceEntity.setName(filename);
                weComMediaResourceEntity.setFileType(FileTypeUtils.getFileType(fileBytes, filename));
                weComMediaResourceEntity.setFileSize(fileSize);
                weComMediaResourceEntity.setIsFinish(Boolean.TRUE);
                weComMediaResourceEntity.setIsTemp(Boolean.FALSE);

                weComMediaResourceEntity.setStorageType(mediaStorageType);
                if (mediaStorageType.equalsIgnoreCase(WeComMediaStorageTypeEnum.MYSQL.name())) {
                    weComMediaResourceEntity.setMediaData(multipartFile.getBytes());
                }
                weComMediaResourceRepository.save(weComMediaResourceEntity);
                response.setMediaUuid(weComMediaResourceEntity.getUuid());
                response.setImageContent(mediaToFiles(multipartFile.getBytes(), fileSize, fileType,
                        multipartFile.getOriginalFilename()));

                log.info("upload weCom media success. projectId={}, corpId={}, agentId={}, mediaUuid={}", projectId,
                        corpId, agentId, weComMediaResourceEntity.getUuid());
                return BaseResponse.success(response);
            }
            // 上传到企微
            RpcResponse<WeComUploadMediaClientResponse> rpcResponse = uploadMediaToWeCom(mediaTypeEnum, taskTypeEnum,
                    corpId, agentId, filename, fileBytes);
            if (rpcResponse == null || !rpcResponse.getCode().equals(ErrorCodeEnum.OK.getCode()) || rpcResponse.getData() == null) {
                log.info("response uploadMediaToWeCom is empty.");
                return BaseResponse.failure(rpcResponse.getCode(), ErrorCodeEnum.getMessage(rpcResponse.getCode(),
                        rpcResponse.getMessage()));
            }

            log.info("get update media resource response. response={}", rpcResponse);
            WeComUploadMediaClientResponse data = rpcResponse.getData();
            if (data == null) {
                log.info("response WeComQueryDepartmentsClientResponse is empty.");
                return BaseResponse.failure(ErrorCodeEnum.ERROR_WEB_MEDIA_UPLOAD_FAILED);
            }

            // 生成缩略图
            String thumbBase64 = "";
            if (Objects.equals(mediaTypeEnum, WeComMediaTypeEnum.IMAGE)
                    || Objects.equals(mediaTypeEnum, WeComMediaTypeEnum.LINK)
                    || Objects.equals(mediaTypeEnum, WeComMediaTypeEnum.MINIPROGRAM)) {
                thumbBase64 = mediaToFiles(multipartFile.getBytes(), fileSize, fileType,
                        multipartFile.getOriginalFilename());
                if (StringUtils.isBlank(thumbBase64)) {
                    return BaseResponse.failure(ErrorCodeEnum.ERROR_WEB_BASE64_IMAGE_DATA);
                }
            }

            // 保存素材表
            WeComMediaResourceEntity media = new WeComMediaResourceEntity();
            media.setCorpId(corpId);
            media.setProjectUuid(projectId);
            media.setUuid(UuidUtils.generateUuid());
            media.setMediaId(StringUtils.isNotBlank(data.getMediaId()) ? data.getMediaId() : data.getUrl());
            media.setMediaType(mediaTypeEnum.name());
            media.setName(filename);
            media.setFileType(FileTypeUtils.getFileType(fileBytes, filename));
            media.setFileSize(fileSize);
            media.setIsFinish(Boolean.FALSE);
            boolean isTempUpload = Objects.equals(taskTypeEnum.name(), WeComMassTaskTypeEnum.MOMENT) ||
                    (!Objects.equals(taskTypeEnum.name(), WeComMassTaskTypeEnum.MOMENT) && !Objects.equals(mediaTypeEnum.name(), WeComMediaTypeEnum.LINK.name()));
            media.setIsTemp(isTempUpload);
            Long createdAt = data.getCreatedAt();
            if (createdAt != null && createdAt > 0) {
                long expiredTime = (createdAt + (3 * 24 * 60 * 60)) * 1000;
                media.setExpireTime(DateUtil.date(expiredTime));
            }
            media.setStorageType(mediaStorageType);
            if (mediaStorageType.equalsIgnoreCase(WeComMediaStorageTypeEnum.MYSQL.name())) {
                media.setMediaData(multipartFile.getBytes());
            }
            weComMediaResourceRepository.save(media);

            response.setMediaUuid(media.getUuid());
            response.setImageContent(thumbBase64);

            log.info("upload weCom media success. projectId={}, corpId={}, agentId={}, mediaUuid={}", projectId,
                    corpId, agentId, media.getUuid());
            return BaseResponse.success(response);
        } catch (Exception e) {
            log.error("failed to upload weCom media. projectId={}, corpId={}, agentId={}, filename={}, fileSize={}, " +
                            "mediaType={}, taskType={}",
                    projectId, corpId, agentId, filename, fileSize, mediaTypeEnum.name(), taskTypeEnum.name(), e);
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_MEDIA_UPLOAD_FAILED);
        }
    }

    @Override
    public BaseResponse deleteMedia(String projectId, String corpId, String mediaUuid) {
        weComMediaResourceRepository.deleteByUuid(mediaUuid);
        return BaseResponse.success();
    }

    @Override
    public BaseResponse queryMedia(String projectId, String corpId, String mediaUuid) {
        WeComMediaResourceEntity entity = weComMediaResourceRepository.queryByUuid(mediaUuid);
        if (entity == null) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_MEDIA_IS_ILLEGAL);
        }
        MediaUploadResponse response = new MediaUploadResponse();
        if (entity.getMediaType().equalsIgnoreCase(WeComMediaTypeEnum.IMAGE.getType())) {
//            String thumbBase64 = mediaToFiles(entity.getMediaData(), entity.getName());
            response.setImageContent("");
        }

        response.setMediaUuid(entity.getUuid());
        return BaseResponse.success(response);
    }

    private void checkMediaParams(WeComMediaTypeEnum mediaTypeEnum, WeComMassTaskTypeEnum taskTypeEnum,
                                  String corpId, String agentId, String projectId, String filename) {
        if (mediaTypeEnum == null) {
            List<String> taskTypeList =
                    Arrays.stream(WeComMassTaskTypeEnum.values()).map(WeComMassTaskTypeEnum::name).collect(Collectors.toList());
            log.error("illegal argument taskType, taskType only accept enum value={}. projectId={}, corpId={}, " +
                            "agentId={}",
                    taskTypeList, projectId, corpId, agentId);
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_PARAM_IS_ILLEGAL);
        }

        // 检查 media type
        if (taskTypeEnum == null) {
            List<String> mediaTypeList = WeComMediaTypeEnum.getSupportedMediaTypeList();
            log.error("illegal argument mediaType. mediaType only accept enum value= {}.projectId={}, corpId={}, " +
                    "agentId={}", mediaTypeList, projectId, corpId, agentId);
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_PARAM_IS_ILLEGAL);
        }

        if (StringUtils.isBlank(filename)) {
            log.error("illegal argument filename, miss required argument filename. projectId={}, corpId={}, " +
                    "agentId={}", projectId, corpId, agentId);
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_PARAM_IS_ILLEGAL);
        }

        // 朋友圈素材仅支持：IMAGE、VIDEO、LINK
        if (Objects.equals(taskTypeEnum, WeComMassTaskTypeEnum.MOMENT)
                && !WeComMediaTypeEnum.supportedMomentMediaType(mediaTypeEnum.getType())) {
            log.error("illegal argument mediaType. moment mediaType only accept enum value: IMAGE, VIDEO, LINK. " +
                    "projectId={}, corpId={}, agentId={}", projectId, corpId, agentId);
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_PARAM_IS_ILLEGAL);
        }
    }

    private void checkFileLimit(String projectId, String corpId, String agentId, String filename, String fileType,
                                WeComMassTaskTypeEnum taskTypeEnum, WeComMediaTypeEnum mediaTypeEnum) {
        String formatLogMessage = String.format("unsupported file type. projectId={}, corpId={}, agentId={}, " +
                        "filename={}, " +
                        "fileType={}, taskType={}, mediaType={}", projectId, corpId, agentId, filename, fileType,
                taskTypeEnum.name(), mediaTypeEnum.name());
        if (Objects.equals(mediaTypeEnum, WeComMediaTypeEnum.IMAGE)) {
            if (!WeComMediaUtils.supportedTempMediaType(fileType, WeComMediaUtils.MediaTypeEnum.IMAGE)) {
                log.error(formatLogMessage);
                throw new CommonException(ErrorCodeEnum.ERROR_WEB_MEDIA_UNSUPPORTED_FILE_TYPE);
            }
        } else if (Objects.equals(mediaTypeEnum, WeComMediaTypeEnum.VIDEO)) {
            if (!WeComMediaUtils.supportedTempMediaType(fileType, WeComMediaUtils.MediaTypeEnum.VIDEO)) {
                log.error(formatLogMessage);
                throw new CommonException(ErrorCodeEnum.ERROR_WEB_MEDIA_UNSUPPORTED_FILE_TYPE);
            }
        } else if (Objects.equals(mediaTypeEnum, WeComMediaTypeEnum.FILE)) {
            if (!WeComMediaUtils.supportedTempMediaType(fileType, WeComMediaUtils.MediaTypeEnum.FILE)) {
                log.error(formatLogMessage);
                throw new CommonException(ErrorCodeEnum.ERROR_WEB_MEDIA_UNSUPPORTED_FILE_TYPE);
            }
        } else if (Objects.equals(mediaTypeEnum, WeComMediaTypeEnum.LINK)) {
            if (!WeComMediaUtils.supportedTempMediaType(fileType, WeComMediaUtils.MediaTypeEnum.IMAGE)) {
                log.error(formatLogMessage);
                throw new CommonException(ErrorCodeEnum.ERROR_WEB_MEDIA_UNSUPPORTED_FILE_TYPE);
            }
        }
    }

    private RpcResponse uploadMediaToWeCom(WeComMediaTypeEnum mediaTypeEnum, WeComMassTaskTypeEnum taskTypeEnum,
                                           String corpId, String agentId, String filename, byte[] fileBytes) {

        // 群发朋友圈素材为临时素材 或者 非群发朋友圈 LINK 类型的素材为临时素材
        // 群发朋友圈 LINK 类型的素材也为临时素材，与其他两种任务素材类型不同
        boolean isTempUpload = Objects.equals(taskTypeEnum.name(), WeComMassTaskTypeEnum.MOMENT) ||
                ((!Objects.equals(taskTypeEnum.name(), WeComMassTaskTypeEnum.MOMENT) || !Objects.equals(taskTypeEnum.name(), WeComMassTaskTypeEnum.LIVE_CODE)) &&
                        !Objects.equals(mediaTypeEnum.name(), WeComMediaTypeEnum.LINK.name()));

        WeComUploadMediaRequest request = new WeComUploadMediaRequest();
        request.setFilename(filename);
        request.setFileData(fileBytes);
        request.setCorpId(corpId);
        request.setAgentId(agentId);
        request.setTempUpload(isTempUpload);
        request.setMediaType(mediaTypeEnum);

        // 朋友圈素材
        if (Objects.equals(taskTypeEnum, WeComMassTaskTypeEnum.MOMENT)) {
            request.setAttachmentType(WeComAttachmentTypeEnum.MOMENT);
            return weComMediaRpcService.uploadWeComAttachment(request);
        }

        return weComMediaRpcService.uploadMediaResource(request);
    }

    private String mediaToFiles(byte[] fileData, Long fileSize, String formatType, String originalFilename) {
        String base64 = "";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData);
        log.info("start to imageToBase64 base64 fileSize={}", fileSize);
        try {
            BufferedImage output = Thumbnails.of(inputStream).size(200, 200).asBufferedImage();
            base64 = imageToBase64(output, formatType);
            log.info("imageToBase64 base64 image.base64={}", base64.length());
        } catch (Exception e) {
            log.error("failed to last base64 image.", e);
            return base64;
        }
        log.info("return to imageToBase64 base64 image.base64={}", base64.length());
        return StringUtils.isBlank(base64) ? Base64.getEncoder().encodeToString(fileData) : base64;
    }

    // BufferedImage转换成base64，在这里需要设置图片格式，如下是jpg格式图片：
    public static String imageToBase64(BufferedImage bufferedImage, String formatType) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, formatType, baos);
        } catch (IOException e) {
            log.error("failed to imageToBase64 base64 image.", e);
        }
        log.info("base64 image. bufferedImage.height={},bufferedImage.width={}, formatType={}, baos={}",
                bufferedImage.getHeight(), bufferedImage.getWidth(), formatType, baos.size());
        return Base64.getEncoder().encodeToString((baos.toByteArray()));
    }
}
