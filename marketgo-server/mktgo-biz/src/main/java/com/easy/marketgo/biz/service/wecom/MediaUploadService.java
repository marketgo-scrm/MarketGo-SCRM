package com.easy.marketgo.biz.service.wecom;

import cn.hutool.core.date.DateUtil;
import com.easy.marketgo.api.model.request.WeComUploadMediaRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.response.WeComUploadMediaClientResponse;
import com.easy.marketgo.api.service.WeComMediaRpcService;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.enums.WeComMediaTypeEnum;
import com.easy.marketgo.core.entity.WeComAgentMessageEntity;
import com.easy.marketgo.core.entity.WeComMediaResourceEntity;
import com.easy.marketgo.core.repository.media.WeComMediaResourceRepository;
import com.easy.marketgo.core.repository.wecom.WeComAgentMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/5/22 10:19 PM
 * Describe:
 */
@Slf4j
@Service
public class MediaUploadService {

    private static final Integer MEDIA_EXPIRE_TIME = 24;

    @Autowired
    private WeComMediaResourceRepository weComMediaResourceRepository;

    @Resource
    private WeComMediaRpcService weComMediaRpcService;

    @Autowired
    private WeComAgentMessageRepository weComAgentMessageRepository;

    public void uploadWeComMedia() {
        log.info("check temp media resource.");
        List<WeComMediaResourceEntity> entities = weComMediaResourceRepository.getMediaByIsTemp(MEDIA_EXPIRE_TIME);
        if (CollectionUtils.isEmpty(entities)) {
            log.info("query temp media resource is empty from db.");
            return;
        }
        log.info("query upload media resource message. count={}", entities.size());
        for (WeComMediaResourceEntity entity : entities) {
            WeComMediaResourceEntity item = weComMediaResourceRepository.queryByUuid(entity.getUuid());
            WeComMediaTypeEnum mediaTypeEnum = WeComMediaTypeEnum.fromValue(item.getMediaType());

            WeComAgentMessageEntity weComAgentMessageEntity =
                    weComAgentMessageRepository.getWeComAgentByCorp(item.getProjectUuid(), item.getCorpId());
            if (weComAgentMessageEntity == null) {
                log.info("query agent message is empty from db. projectUuid={}, corpId={}", item.getProjectUuid(),
                        item.getCorpId());
                continue;
            }
            // 上传到企微
            RpcResponse<WeComUploadMediaClientResponse> rpcResponse = uploadMediaToWeCom(mediaTypeEnum,
                    item.getCorpId(), weComAgentMessageEntity.getAgentId(), item.getName(), item.getMediaData());
            log.info("timer to update media resource response. response={}", rpcResponse);
            if (rpcResponse == null || !rpcResponse.getCode().equals(ErrorCodeEnum.OK.getCode()) || rpcResponse.getData() == null) {
                log.info("response uploadMediaToWeCom is empty.mediaTypeEnum={}, mediaName={}", mediaTypeEnum,
                        item.getName());
                continue;
            }
            WeComUploadMediaClientResponse data = rpcResponse.getData();
            // 保存素材表
            item.setMediaId(data.getMediaId());

            Long createdAt = data.getCreatedAt();
            if (createdAt != null && createdAt > 0) {
                long expiredTime = (createdAt + (3 * 24 * 60 * 60)) * 1000;
                item.setExpireTime(DateUtil.date(expiredTime));
            }
            weComMediaResourceRepository.save(item);
        }
    }

    private RpcResponse uploadMediaToWeCom(WeComMediaTypeEnum mediaTypeEnum, String corpId, String agentId,
                                           String filename, byte[] fileBytes) {

        WeComUploadMediaRequest request = new WeComUploadMediaRequest();
        request.setFilename(filename);
        request.setFileData(fileBytes);
        request.setCorpId(corpId);
        request.setAgentId(agentId);
        request.setTempUpload(true);
        request.setMediaType(mediaTypeEnum);

        return weComMediaRpcService.uploadMediaResource(request);
    }
}
