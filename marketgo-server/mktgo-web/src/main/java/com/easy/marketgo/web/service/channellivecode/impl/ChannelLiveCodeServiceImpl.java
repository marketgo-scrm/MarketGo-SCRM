package com.easy.marketgo.web.service.channellivecode.impl;

import cn.hutool.core.date.DateUtil;
import com.easy.marketgo.api.model.request.contactway.WeComContactWayClientRequest;
import com.easy.marketgo.api.model.request.contactway.WeComDeleteContactWayClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.response.WeComAddContactWayClientResponse;
import com.easy.marketgo.api.service.WeComContactWayRpcService;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.common.utils.UuidUtils;
import com.easy.marketgo.core.entity.WeComAgentMessageEntity;
import com.easy.marketgo.core.entity.WeComMediaResourceEntity;
import com.easy.marketgo.core.entity.channellive.WeComChannelLiveCodeEntity;
import com.easy.marketgo.core.entity.channellive.WeComChannelLiveCodeMembersEntity;
import com.easy.marketgo.core.entity.customer.WeComDepartmentEntity;
import com.easy.marketgo.core.entity.customer.WeComMemberMessageEntity;
import com.easy.marketgo.core.model.bo.QueryChannelLiveCodeBuildSqlParam;
import com.easy.marketgo.core.model.bo.QueryMemberBuildSqlParam;
import com.easy.marketgo.core.repository.media.WeComMediaResourceRepository;
import com.easy.marketgo.core.repository.wecom.WeComAgentMessageRepository;
import com.easy.marketgo.core.repository.wecom.WeComDepartmentRepository;
import com.easy.marketgo.core.repository.wecom.channelLivecode.WeComChannelLiveCodeMembersRepository;
import com.easy.marketgo.core.repository.wecom.channelLivecode.WeComChannelLiveCodeRepository;
import com.easy.marketgo.core.repository.wecom.channelLivecode.WeComChannelLiveCodeStatisticRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import com.easy.marketgo.web.model.bo.WeComCorpTag;
import com.easy.marketgo.web.model.bo.WeComMassTaskSendMsg;
import com.easy.marketgo.web.model.request.contactway.ChannelLiveCodeCreateRequest;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.web.model.response.contactway.ChannelLiveCodeDetailResponse;
import com.easy.marketgo.web.model.response.contactway.ChannelLiveCodeResponse;
import com.easy.marketgo.web.model.response.media.MediaUploadResponse;
import com.easy.marketgo.web.service.channellivecode.ChannelLiveCodeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-30 16:57:27
 * @description : ChannelContactWayServiceImpl.java
 */
@Component
@Slf4j
public class ChannelLiveCodeServiceImpl implements ChannelLiveCodeService {

    @Autowired
    private WeComChannelLiveCodeRepository weComChannelLiveCodeRepository;
    @Autowired
    private WeComChannelLiveCodeMembersRepository weComChannelLiveCodeMembersRepository;
    @Resource
    private WeComContactWayRpcService weComContactWayRpcService;
    @Autowired
    private WeComAgentMessageRepository agentMessageRepository;

    @Autowired
    private WeComDepartmentRepository weComDepartmentRepository;

    @Autowired
    private WeComMemberMessageRepository weComMemberMessageRepository;

    @Autowired
    private WeComChannelLiveCodeStatisticRepository weComChannelLiveCodeStatisticRepository;

    @Autowired
    private WeComMediaResourceRepository weComMediaResourceRepository;
    private static final float LOGO_PERCENT = 0.2f;

    private static final Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);

    static {
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 2);
    }

    @Override
    public ChannelLiveCodeResponse queryListByPage(String projectUuid, String corpId, Integer pageNum,
                                                   Integer pageSize, String keyword) {

        QueryChannelLiveCodeBuildSqlParam param = new QueryChannelLiveCodeBuildSqlParam();
        param.setProjectUuid(projectUuid);
        param.setCorpId(corpId);
        param.setKeyword(keyword);
        param.setPageNum(pageNum);
        param.setPageSize(pageSize);
        param.setSortOrderKey("DESC");
        Integer count = weComChannelLiveCodeRepository.countByBuildSqlParam(param);
        log.info("query channel live code count. count={}, param={}", count, param);

        List<WeComChannelLiveCodeEntity> entities = weComChannelLiveCodeRepository.listByBuildSqlParam(param);


        ChannelLiveCodeResponse response = ChannelLiveCodeResponse.builder().page(pageNum)
                .pageSize(pageSize).total(count == null ? 0 : count).build();

        if (CollectionUtils.isNotEmpty(entities)) {
            List<ChannelLiveCodeResponse.ChannelContactWayInfo> infoList = entities.stream().map(entity -> {
                Integer dailyIncreasedCount = weComChannelLiveCodeStatisticRepository.totalIncreasedExtUserCount(corpId,
                        entity.getUuid());

                ChannelLiveCodeResponse.ChannelContactWayInfo wayInfo =
                        new ChannelLiveCodeResponse.ChannelContactWayInfo();
                wayInfo.setId(entity.getId());
                wayInfo.setUuid(entity.getUuid());
                wayInfo.setQrCode(entity.getQrCode());
                wayInfo.setName(entity.getName());
                wayInfo.setAddExtCount(dailyIncreasedCount == null ? 0 : dailyIncreasedCount);
                wayInfo.setTags(StringUtils.isBlank(entity.getTags()) ? null : JsonUtils.toArray(entity.getTags(),
                        WeComCorpTag.class));
                wayInfo.setIsUploadLogo(Boolean.FALSE);
                String logoInfo = entity.getLogoMedia();
                if (StringUtils.isNotBlank(logoInfo)) {
                    MediaUploadResponse logo = JsonUtils.toObject(logoInfo, MediaUploadResponse.class);
                    if (logo != null && StringUtils.isNotBlank(logo.getMediaUuid())) {
                        wayInfo.setIsUploadLogo(Boolean.TRUE);
                    }
                }
                wayInfo.setMembers(JsonUtils.toObject(entity.getMembers(),
                        ChannelLiveCodeCreateRequest.MembersMessage.class));// todo ；联合查询绑定人员表获得
                wayInfo.setBackupMembers(StringUtils.isBlank(entity.getBackupMembers()) ? null :
                        JsonUtils.toObject(entity.getBackupMembers(),
                                ChannelLiveCodeCreateRequest.MembersMessage.class));
                wayInfo.setCreateTime(DateUtil.formatDateTime(entity.getCreateTime()));

                return wayInfo;
            }).collect(Collectors.toList());
            response.setInfos(infoList);
        }
        return response;
    }

    /**
     * @param request
     * @return
     */
    @Override
    @Transactional
    public BaseResponse create(String projectUuid, String corpId, ChannelLiveCodeCreateRequest request) {
        WeComChannelLiveCodeEntity weComChannelLiveCode = new WeComChannelLiveCodeEntity();

        List<String> members = getMemberList(corpId, request.getMembers());

        if (StringUtils.isNotBlank(request.getUuid())) {
            weComChannelLiveCode = weComChannelLiveCodeRepository.queryByCorpAndUuid(corpId, request.getUuid());
        } else {
            WeComAgentMessageEntity agent = agentMessageRepository.getWeComAgentByCorp(projectUuid, corpId);

            WeComContactWayClientRequest clientRequest = new WeComContactWayClientRequest();
            clientRequest.setConfigId("");
            //联系方式类型，1-单人，2-多人
            clientRequest.setType(2);
            //场景，1-在小程序中联系，2-通过二维码联系
            clientRequest.setScene(2);
            clientRequest.setStyle(0);
            clientRequest.setSkipVerify(request.getSkipVerify());
            String state = RandomStringUtils.randomAlphanumeric(24);
            clientRequest.setState(state);

            clientRequest.setUser(members);
            clientRequest.setCorpId(corpId);
            clientRequest.setAgentId(agent.getAgentId());

            RpcResponse<WeComAddContactWayClientResponse> rpcResponse =
                    weComContactWayRpcService.addContactWay(clientRequest);
            log.info("add contact way rpcResponse. rpcResponse={}", rpcResponse);
            if (!rpcResponse.getCode().equals(ErrorCodeEnum.OK.getCode())) {
                throw new CommonException(rpcResponse.getCode(), rpcResponse.getMessage());
            }

            WeComAddContactWayClientResponse rpcResponseData = rpcResponse.getData();

            weComChannelLiveCode.setQrCode(rpcResponseData.getQrCode());
            weComChannelLiveCode.setConfigId(rpcResponseData.getConfigId());
            weComChannelLiveCode.setAgentId(agent.getAgentId());
            weComChannelLiveCode.setState(state);

            weComChannelLiveCode.setUuid(UuidUtils.generateUuid());
            weComChannelLiveCode.setProjectUuid(projectUuid);
            weComChannelLiveCode.setCorpId(corpId);
        }

        weComChannelLiveCode.setName(request.getName());

        weComChannelLiveCode.setSkipVerify(request.getSkipVerify());
        if (CollectionUtils.isNotEmpty(request.getTags())) {
            weComChannelLiveCode.setTags(JsonUtils.toJSONString(request.getTags()));
        }
        if (request.getBackupMembers() != null) {
            weComChannelLiveCode.setBackupMembers(JsonUtils.toJSONString(request.getBackupMembers()));
        }
        weComChannelLiveCode.setMembers(JsonUtils.toJSONString(request.getMembers()));
        if (request.getAddExtUserLimitStatus().equals(Boolean.TRUE) &&
                CollectionUtils.isNotEmpty(request.getAddExtUserLimit())) {
            weComChannelLiveCode.setAddLimitMembers(JsonUtils.toJSONString(request.getAddExtUserLimit()));
        }
        weComChannelLiveCode.setAddLimitStatus(request.getAddExtUserLimitStatus());
        weComChannelLiveCode.setWelcomeType(request.getWelcomeType().getValue());
        weComChannelLiveCode.setOnlineType(request.getOnlineType());
        weComChannelLiveCode.setLogoMedia(JsonUtils.toJSONString(request.getLogoMedia()));
        weComChannelLiveCode.setWelcomeContent(JsonUtils.toJSONString(request.getWelcomeContent()));

        weComChannelLiveCodeRepository.save(weComChannelLiveCode);

        saveMembersMessage(weComChannelLiveCode.getUuid(), members, Boolean.FALSE, Boolean.TRUE);

        if (request.getBackupMembers() != null) {
            List<String> backupMembers = getMemberList(corpId, request.getBackupMembers());
            saveMembersMessage(weComChannelLiveCode.getUuid(), backupMembers, Boolean.TRUE, Boolean.FALSE);
        }


        if (request.getAddExtUserLimitStatus().equals(Boolean.TRUE) &&
                CollectionUtils.isNotEmpty(request.getAddExtUserLimit())) {

            for (ChannelLiveCodeCreateRequest.MemberInfo item : request.getAddExtUserLimit()) {
                weComChannelLiveCodeMembersRepository.updateLimitCountByMemberId(weComChannelLiveCode.getUuid(),
                        item.getMemberId(), item.getAddExtUserCeiling());
            }
        }
        return BaseResponse.success();
    }

    @Override
    public BaseResponse checkChannelName(String projectId, String corpId, Integer channelId, String name) {
        try {
            log.info("start to check weCom channel name. projectUuid={}, corpId={} channelId={}, name={}", projectId
                    , corpId, channelId, name);

            WeComChannelLiveCodeEntity weComChannelLiveCodeEntity =
                    weComChannelLiveCodeRepository.geChannelLiveCodeByName(projectId, corpId, name);
            // 仅当「非同一个计划且同名」的情况，认为重名
            if (weComChannelLiveCodeEntity != null && !weComChannelLiveCodeEntity.getId().equals(channelId)) {
                log.info("failed to check weCom channel name. projectUuid={}, corpId={} channelId={}, name={}, " +
                                "weComChannelLiveCodeEntity={}", projectId, corpId, channelId, name,
                        weComChannelLiveCodeEntity);
                return BaseResponse.builder().code(ErrorCodeEnum.ERROR_WECOM_MASS_TASK_DUPLICATE_CNAME.getCode()).message(ErrorCodeEnum.ERROR_WECOM_MASS_TASK_DUPLICATE_CNAME.getMessage()).build();
            }
            log.info("succeed to check weCom channel name. projectUuid={}, corpId={} channelId={}, name={}", projectId
                    , corpId, channelId, name);
            return BaseResponse.builder().code(ErrorCodeEnum.OK.getCode()).message(ErrorCodeEnum.OK.getMessage()).build();
        } catch (Exception e) {
            log.error("Failed to check weCom channel name. projectUuid={}, corpId={} channelId={}, name={}, " +
                    "weComChannelLiveCodeEntity={}", projectId, corpId, channelId, name, e);
        }
        return BaseResponse.builder().code(ErrorCodeEnum.ERROR_WEB_WECOM_MASS_TASK_CHECK_NAME.getCode()).message(ErrorCodeEnum.ERROR_WEB_WECOM_MASS_TASK_CHECK_NAME.getMessage()).build();
    }

    @Override
    public BaseResponse downloadQrCode(String projectId, String corpId, String channelId,
                                       HttpServletResponse httpServletResponse) {
        WeComChannelLiveCodeEntity weComChannelLiveCodeEntity =
                weComChannelLiveCodeRepository.queryByCorpAndUuid(corpId, channelId);
        try {
            if (weComChannelLiveCodeEntity != null) {
                String qrCodeUrl = weComChannelLiveCodeEntity.getQrCode();
                if (StringUtils.isBlank(weComChannelLiveCodeEntity.getLogoMedia())) {
                    Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
                    hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                    hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

                    BitMatrix bitMatrix = new MultiFormatWriter().encode(qrCodeUrl, BarcodeFormat.QR_CODE, 200, 200,
                            hints);
                    httpServletResponse.setHeader("Content-Type", "application/octet-stream");
                    httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + "channel_qrcode.png");
                    OutputStream outputStream = null;

                    outputStream = httpServletResponse.getOutputStream();

                    MatrixToImageWriter.writeToStream(bitMatrix, "png", outputStream);
                    outputStream.flush();
                    outputStream.close();

                } else {
                    String logoMedia = weComChannelLiveCodeEntity.getLogoMedia();
                    MediaUploadResponse logo = JsonUtils.toObject(logoMedia, MediaUploadResponse.class);
                    WeComMediaResourceEntity weComMediaResourceEntity =
                            weComMediaResourceRepository.queryByUuid(logo.getMediaUuid());
                    if (weComMediaResourceEntity != null) {
                        ByteArrayInputStream in = new ByteArrayInputStream(weComMediaResourceEntity.getMediaData());    //将b作为输入流；
                        BufferedImage image = ImageIO.read(in);     //将in作为输入流，读取图片存入image中，而这里in
                        // 可以为ByteArrayInputStream();

                        BufferedImage bufferedImage = createQrcodeWithLogo(qrCodeUrl, image, 200, 200, true);

                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        ImageIO.write(bufferedImage, "png", os);

                        httpServletResponse.setHeader("Content-Type", "application/octet-stream");
                        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + "channel_qrcode" +
                                ".png");
                        OutputStream outputStream = null;

                        outputStream = httpServletResponse.getOutputStream();

                        outputStream.write(os.toByteArray());
                        outputStream.flush();
                        outputStream.close();
                    }
                }
            }
        } catch (IOException e) {
            log.info("failed to get qrCode for qrCode.", e);
        } catch (WriterException e) {
            log.info("failed to write qrCode for qrCode.", e);
        }
        log.info("end to download weCom channel live code qrCode response. corpId={}, channelId={}", corpId, channelId);
        return null;
    }

    @Override
    public BaseResponse getLiveCodeDetail(String projectId, String corpId, String channelId) {
        log.info("start to get weCom channel live code detail. projectUuid={}, corpId={} channelId={}",
                projectId, corpId, channelId);

        ChannelLiveCodeDetailResponse response = new ChannelLiveCodeDetailResponse();
        BaseResponse baseResponse = BaseResponse.success();
        WeComChannelLiveCodeEntity weComChannelLiveCodeEntity =
                weComChannelLiveCodeRepository.queryByCorpAndUuid(corpId, channelId);
        if (weComChannelLiveCodeEntity == null) {
            return baseResponse;
        }

        BeanUtils.copyProperties(weComChannelLiveCodeEntity, response);
        response.setCreateTime(DateUtil.formatDateTime(weComChannelLiveCodeEntity.getCreateTime()));
        response.setTags(StringUtils.isBlank(weComChannelLiveCodeEntity.getTags()) ? null :
                JsonUtils.toArray(weComChannelLiveCodeEntity.getTags(), WeComCorpTag.class));

        response.setMembers(JsonUtils.toObject(weComChannelLiveCodeEntity.getMembers(),
                ChannelLiveCodeCreateRequest.MembersMessage.class));

        response.setAddExtUserLimitStatus(weComChannelLiveCodeEntity.getAddLimitStatus());
        response.setAddExtUserLimit(StringUtils.isBlank(weComChannelLiveCodeEntity.getAddLimitMembers()) ? null :
                JsonUtils.toArray(weComChannelLiveCodeEntity.getAddLimitMembers(),
                        ChannelLiveCodeCreateRequest.MemberInfo.class));
        response.setBackupMembers(StringUtils.isBlank(weComChannelLiveCodeEntity.getBackupMembers()) ? null :
                JsonUtils.toObject(weComChannelLiveCodeEntity.getBackupMembers(),
                        ChannelLiveCodeCreateRequest.MembersMessage.class));
        String content = weComChannelLiveCodeEntity.getWelcomeContent();
        if (StringUtils.isNotBlank(content)) {
            response.setWelcomeContent(JsonUtils.toArray(content, WeComMassTaskSendMsg.class));
        }

        String logoMessage = weComChannelLiveCodeEntity.getLogoMedia();
        if (StringUtils.isNotBlank(logoMessage)) {
            ChannelLiveCodeDetailResponse.LogoMessage logo = JsonUtils.toObject(logoMessage,
                    ChannelLiveCodeDetailResponse.LogoMessage.class);
            response.setLogoMedia(logo);
        }
        baseResponse.setData(response);
        log.info("finish to get weCom channel live code detail response. projectUuid={}, corpId={} channelId={}, " +
                "baseResponse={}", projectId, corpId, channelId, baseResponse);
        return baseResponse;
    }

    @Override
    public BaseResponse deleteLiveCode(String projectId, String corpId, String channelId) {
        WeComChannelLiveCodeEntity weComChannelLiveCodeEntity =
                weComChannelLiveCodeRepository.queryByCorpAndUuid(corpId, channelId);
        if (weComChannelLiveCodeEntity == null) {
            return BaseResponse.failure(ErrorCodeEnum.ERROR_WEB_LIVE_CODE_EXISTS);
        }
        BaseResponse baseResponse = BaseResponse.success();
        WeComAgentMessageEntity agent = agentMessageRepository.getWeComAgentByCorp(projectId, corpId);

        WeComDeleteContactWayClientRequest request = new WeComDeleteContactWayClientRequest();
        request.setCorpId(corpId);
        request.setAgentId(agent == null ? "" : agent.getAgentId());
        request.setConfigId(weComChannelLiveCodeEntity.getConfigId());
        RpcResponse rpcResponse = weComContactWayRpcService.deleteContactWay(request);
        log.info("delete live code from configId rpcResponse. rpcResponse={}", rpcResponse);
        if (rpcResponse.getCode().equals(ErrorCodeEnum.OK.getCode()) ||
                rpcResponse.getCode().equals(ErrorCodeEnum.ERROR_WECOM_INVALID_CONFIG_ID.getCode())) {
            weComChannelLiveCodeStatisticRepository.deleteByUuid(channelId);
            weComChannelLiveCodeMembersRepository.deleteByUuid(channelId);

            WeComChannelLiveCodeEntity entity = weComChannelLiveCodeRepository.queryByCorpAndUuid(corpId, channelId);
            String content = entity.getWelcomeContent();
            if (StringUtils.isNotEmpty(content)) {
                List<WeComMassTaskSendMsg> contentList = JsonUtils.toArray(content, WeComMassTaskSendMsg.class);
                if (CollectionUtils.isNotEmpty(contentList)) {
                    List<String> mediaUuidList = new ArrayList<>();
                    contentList.forEach(item -> {
                        if (item.getType() == WeComMassTaskSendMsg.TypeEnum.VIDEO &&
                                item.getVideo() != null &&
                                StringUtils.isNotBlank(item.getVideo().getMediaUuid())) {
                            mediaUuidList.add(item.getVideo().getMediaUuid());
                        } else if (item.getType() == WeComMassTaskSendMsg.TypeEnum.IMAGE &&
                                item.getImage() != null &&
                                StringUtils.isNotBlank(item.getImage().getMediaUuid())) {
                            mediaUuidList.add(item.getImage().getMediaUuid());
                        } else if (item.getType() == WeComMassTaskSendMsg.TypeEnum.FILE &&
                                item.getFile() != null &&
                                StringUtils.isNotBlank(item.getFile().getMediaUuid())) {
                            mediaUuidList.add(item.getFile().getMediaUuid());
                        } else if (item.getType() == WeComMassTaskSendMsg.TypeEnum.MINIPROGRAM &&
                                item.getMiniProgram() != null &&
                                StringUtils.isNotBlank(item.getMiniProgram().getMediaUuid())) {
                            mediaUuidList.add(item.getMiniProgram().getMediaUuid());
                        } else if (item.getType() == WeComMassTaskSendMsg.TypeEnum.LINK &&
                                item.getLink() != null &&
                                StringUtils.isNotBlank(item.getLink().getMediaUuid())) {
                            mediaUuidList.add(item.getLink().getMediaUuid());
                        }
                    });
                    log.info("delete media resource. uuids={}", mediaUuidList);
                    if (CollectionUtils.isNotEmpty(mediaUuidList)) {
                        weComMediaResourceRepository.deleteByUuids(mediaUuidList);
                    }
                }
            }
            weComChannelLiveCodeRepository.deleteByUuid(channelId);
        }
        baseResponse.setCode(rpcResponse.getCode());
        baseResponse.setMessage(rpcResponse.getMessage());
        return baseResponse;
    }

    private List<String> getMemberList(String corpId, ChannelLiveCodeCreateRequest.MembersMessage members) {

        List<Long> departmentList = new ArrayList<>();
        List<String> memberList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(members.getUsers())) {
            members.getUsers().forEach(user -> {
                memberList.add(user.getMemberId());
            });
        }

        if (CollectionUtils.isNotEmpty(members.getDepartments())) {
            members.getDepartments().forEach(department -> {
                departmentList.add(department.getId());
            });

            List<WeComDepartmentEntity> departmentEntities =
                    weComDepartmentRepository.findByCorpIdAndParentIdIn(corpId, departmentList);
            while (CollectionUtils.isNotEmpty(departmentEntities)) {
                List<Long> tempDepartmentList = new ArrayList<>();
                departmentEntities.forEach(departmentEntity -> {
                    tempDepartmentList.add(departmentEntity.getDepartmentId());
                });
                log.info("find department list for channel live code. tempDepartmentList={}", tempDepartmentList);
                departmentList.addAll(tempDepartmentList);
                departmentEntities = weComDepartmentRepository.findByCorpIdAndParentIdIn(corpId, tempDepartmentList);
            }

            QueryMemberBuildSqlParam queryMemberBuildSqlParam =
                    QueryMemberBuildSqlParam.builder().corpId(corpId).departments(departmentList).build();
            List<WeComMemberMessageEntity> memberTmp =
                    weComMemberMessageRepository.listByParam(queryMemberBuildSqlParam);
            log.info("query members from db count. memberTmp={}", memberTmp.size());
            if (CollectionUtils.isNotEmpty(memberTmp)) {
                log.info("query members result for channel live code. memberCount={}", memberTmp.size());
                memberTmp.forEach(item -> {
                    memberList.add(item.getMemberId());
                });
            }
        }
        return memberList.stream().distinct().collect(Collectors.toList());
    }

    private void saveMembersMessage(String uuid, List<String> members, Boolean isBackup, Boolean isOnline) {
        List<WeComChannelLiveCodeMembersEntity> entities = new ArrayList<>();

        for (String member : members) {
            WeComChannelLiveCodeMembersEntity saveEntity =
                    weComChannelLiveCodeMembersRepository.queryByMemberIdAndIsBackup(uuid, member, isBackup);
            if (saveEntity != null) {
                continue;
            }
            WeComChannelLiveCodeMembersEntity entity = new WeComChannelLiveCodeMembersEntity();

            entity.setMemberId(member);
            entity.setChannelLiveCodeUuid(uuid);
            entity.setIsBackup(isBackup);
            entity.setOnlineStatus(isOnline);

            entities.add(entity);
        }

        weComChannelLiveCodeMembersRepository.saveAll(entities);
    }

    /**
     * 生成带有 logo 的二维码
     *
     * @param url          二维码内容
     * @param logo         logo 图片
     * @param qrcodeWidth  二维码宽度
     * @param qrcodeHeight 二维码高度
     * @return
     * @throws WriterException
     */
    public BufferedImage createQrcodeWithLogo(String url, BufferedImage logo, int qrcodeWidth, int qrcodeHeight,
                                              boolean retainPadding) throws WriterException, IOException {
        BufferedImage qrCode = createQrcode(url, qrcodeWidth, qrcodeHeight, retainPadding);
        return addLogo(qrCode, logo, null, null);
    }

    /**
     * 二维码图片的生成
     *
     * @param url          链接
     * @param qrcodeWidth  二维码宽
     * @param qrcodeHeight 二维码高
     * @return
     */
    public static BufferedImage createQrcode(String url, int qrcodeWidth, int qrcodeHeight, boolean retainPadding)
            throws WriterException {
        BitMatrix bitMatrix =
                new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, qrcodeWidth, qrcodeHeight, hints);
        if (Objects.isNull(bitMatrix)) {
            log.warn("failed to createQrImage. [content='{}', qrCodeWidth='{}', qrCodeHeight='{}', retainPadding='{}']",
                    qrcodeWidth, qrcodeHeight, retainPadding);
            return null;
        }
        if (!retainPadding) {
            bitMatrix = deleteWhite(bitMatrix);
        }
        log.debug("failed to createQrImage matrix. url={}, width={}, height={}, retainPadding={}", url,
                bitMatrix.getWidth(), bitMatrix.getHeight(), retainPadding);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * 去掉二维码的白边
     *
     * @param matrix
     * @return
     */
    private static BitMatrix deleteWhite(BitMatrix matrix) {
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2];
        int resHeight = rec[3];
        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; j < resHeight; j++) {
                if (matrix.get(i + rec[0], j + rec[1])) {
                    resMatrix.set(i, j);
                }
            }
        }
        return resMatrix;
    }

    /**
     * 给二维码添加 logo
     *
     * @param background 二维码背景
     * @param logo       logo 图片
     * @param logoWidth  期望的 logo 宽度
     * @param logoHeight 期望的 logo 高度
     */
    private BufferedImage addLogo(BufferedImage background, BufferedImage logo, Integer logoWidth,
                                  Integer logoHeight) throws IOException {
        if (Objects.isNull(logo)) {
            return background;
        }
        int originWidth = logo.getWidth(null);
        int originHeight = logo.getHeight(null);
        // 指定的 logo 大小
        if (logoWidth != null && logoWidth > 0) {
            originWidth = logoWidth;
        }
        if (logoHeight != null && logoHeight > 0) {
            originWidth = logoHeight;
        }
        // 宽度和高度
        int width = scaleWidth(background.getWidth(), background.getHeight(), originWidth, originHeight);
        int height = scaleHeight(background.getWidth(), background.getHeight(), originWidth, originHeight);
        Image newLogo = zoomWithHighQuality(logo, width, height);
        int startX = (background.getWidth() - width) / 2;
        int startY = (background.getHeight() - height) / 2;
        return compositeImage(background, newLogo, startX, startY);
    }


    /**
     * logo 宽高等比例压缩
     *
     * @param backgroundImageWidth  背景图片宽度
     * @param backgroundImageHeight 背景图片高度
     * @param logoWidth             logo 宽度
     * @param logoHeight            logo 高度
     * @return
     */
    private int scaleWidth(int backgroundImageWidth, int backgroundImageHeight, int logoWidth, int logoHeight) {
        double widthScala = backgroundImageWidth * LOGO_PERCENT / logoWidth;
        double heightScala = backgroundImageHeight * LOGO_PERCENT / logoHeight;
        return (int) (widthScala > heightScala ? heightScala * logoWidth : widthScala * logoWidth);
    }


    /**
     * logo 宽高等比例压缩
     *
     * @param backgroundImageWidth  背景图片宽度
     * @param backgroundImageHeight 背景图片高度
     * @param logoWidth             log 宽度
     * @param logoHeight            logo 高度
     * @return
     */
    private int scaleHeight(int backgroundImageWidth, int backgroundImageHeight, int logoWidth, int logoHeight) {
        double widthScala = backgroundImageWidth * LOGO_PERCENT / logoWidth;
        double heightScala = backgroundImageHeight * LOGO_PERCENT / logoHeight;
        return (int) (widthScala > heightScala ? heightScala * logoHeight : widthScala * logoHeight);
    }

    /**
     * 压缩图片
     *
     * @param originalImg  原始图片
     * @param targetWidth  宽度
     * @param targetHeight 高度
     * @return
     */
    private BufferedImage zoomWithHighQuality(BufferedImage originalImg, int targetWidth, int targetHeight)
            throws IOException {
        if (Objects.isNull(originalImg)) {
            return null;
        }
        return Thumbnails.of(originalImg)
                .size(targetWidth, targetHeight)
                .outputQuality(1.0f)
                .asBufferedImage();
    }

    /**
     * 合成图片， 图片2会从图片1的(startX, startY)开始渲染
     *
     * @param backgroundImage 背景图片
     * @param floatImage      浮层图片
     * @param startX          开始绘制X轴位置
     * @param startY          开始绘制Y轴位置
     * @return {BufferedImage} 合成的图片
     */
    public BufferedImage compositeImage(BufferedImage backgroundImage, Image floatImage, int startX,
                                        int startY) {
        if (Objects.isNull(backgroundImage) || Objects.isNull(floatImage)) {
            return backgroundImage;
        }
        try {
            int alphaType = BufferedImage.TYPE_INT_RGB;
            BufferedImage result =
                    new BufferedImage(backgroundImage.getWidth(null), backgroundImage.getHeight(null), alphaType);
            // 画图
            Graphics2D g = result.createGraphics();
            g.drawImage(backgroundImage, 0, 0, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1));
            g.setStroke(new BasicStroke(3f));
            g.setColor(Color.black);
            g.drawImage(floatImage, startX, startY, null);
            g.dispose();
            return result;
        } catch (Exception e) {
            log.error("failed to compositeImage. startX={}, startY={}", startX, startY, e);
        }
        return null;
    }
}
