package com.easy.marketgo.web.service.channellivecode.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.easy.marketgo.common.enums.ChannelContactWayStatisticsEnum;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.core.entity.channellive.WeComChannelLiveCodeEntity;
import com.easy.marketgo.core.entity.channellive.WeComChannelLiveCodeMembersEntity;
import com.easy.marketgo.core.entity.channellive.WeComChannelLiveCodeStatisticEntity;
import com.easy.marketgo.core.repository.wecom.callback.WeComExternalUserEventRepository;
import com.easy.marketgo.core.repository.wecom.channelLivecode.WeComChannelLiveCodeMembersRepository;
import com.easy.marketgo.core.repository.wecom.channelLivecode.WeComChannelLiveCodeRepository;
import com.easy.marketgo.core.repository.wecom.channelLivecode.WeComChannelLiveCodeStatisticRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComRelationMemberExternalUserRepository;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.web.model.response.contactway.ChannelLiveCodeStatisticsResponse;
import com.easy.marketgo.web.model.response.contactway.ChannelLiveCodeStatisticsSummaryResponse;
import com.easy.marketgo.web.service.channellivecode.ChannelLiveCodeStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.easy.marketgo.common.enums.ErrorCodeEnum.ERROR_WEB_LIVE_CODE_EXISTS;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-31 17:03:03
 * @description : ChannelLiveCodeStatisticsServiceImpl.java
 */
@Component
@Slf4j
public class ChannelLiveCodeStatisticsServiceImpl implements ChannelLiveCodeStatisticsService {

    @Autowired
    private WeComChannelLiveCodeStatisticRepository weComChannelLiveCodeStatisticRepository;
    @Autowired
    private WeComChannelLiveCodeRepository liveCodeRepository;
    @Autowired
    private WeComMemberMessageRepository memberMessageRepository;
    @Autowired
    private WeComRelationMemberExternalUserRepository relationMemberExternalUserRepository;

    @Autowired
    private WeComChannelLiveCodeMembersRepository weComChannelLiveCodeMembersRepository;

    @Autowired
    private WeComExternalUserEventRepository weComExternalUserEventRepository;

    @Override
    public BaseResponse summary(String projectId, String corpId, String channelId) {
        WeComChannelLiveCodeEntity codeEntity = liveCodeRepository.queryByCorpAndUuid(corpId, channelId);
        if (codeEntity == null) {
            return BaseResponse.failure(ERROR_WEB_LIVE_CODE_EXISTS);
        }

        Integer dailyDecreaseCount = weComChannelLiveCodeStatisticRepository.dailyDecreaseExtUserCount(corpId,
                codeEntity.getUuid());
        Integer dailyIncreasedCount = weComChannelLiveCodeStatisticRepository.dailyIncreasedExtUserCount(corpId,
                codeEntity.getUuid());
        Integer totalDecreaseExtUserCount = weComChannelLiveCodeStatisticRepository.totalDecreaseExtUserCount(corpId,
                codeEntity.getUuid());
        Integer totalIncreasedExtUserCount = weComChannelLiveCodeStatisticRepository.totalIncreasedExtUserCount(corpId,
                codeEntity.getUuid());
        ChannelLiveCodeStatisticsSummaryResponse build =
                ChannelLiveCodeStatisticsSummaryResponse
                        .builder()
                        .lastStatisticsTime(DateUtil.now())
                        .dailyDecreaseExtUserCount(dailyDecreaseCount == null ? 0 : dailyDecreaseCount)
                        .dailyIncreasedExtUserCount(dailyIncreasedCount == null ? 0 : dailyIncreasedCount)
                        .totalExtUserCount(totalIncreasedExtUserCount == null ? 0 : totalIncreasedExtUserCount)
                        .totalDecreaseExtUserCount(totalDecreaseExtUserCount == null ? 0 : totalDecreaseExtUserCount)
                        .build();
        return BaseResponse.success(build);
    }

    @Override
    public BaseResponse statisticsList(String projectId, String corpId, String channelId, Integer pageNum,
                                       Integer pageSize, String StatisticsType, String startTime, String endTime) {
        WeComChannelLiveCodeEntity codeEntity = liveCodeRepository.queryByCorpAndUuid(corpId, channelId);
        if (codeEntity == null) {
            return BaseResponse.failure(ERROR_WEB_LIVE_CODE_EXISTS);
        }
        Date createTime = codeEntity.getCreateTime();
        DateTime dtStartTime, dtEndTime;
        String strStartTime, strEndTime;
        if (StringUtils.isBlank(startTime)) {
            dtStartTime = DateUtil.lastWeek();
            dtStartTime = DateUtil.offsetDay(dtStartTime, 1);

            long betweenDay = DateUtil.between(createTime, dtStartTime, DateUnit.DAY, false);
            log.info("query start time. betweenDay={}", betweenDay);
            if (betweenDay < 0) {
                dtStartTime = new DateTime(createTime);
            }
        } else {
            dtStartTime = DateUtil.parse(startTime);
        }

        if (StringUtils.isBlank(endTime)) {
            dtEndTime = DateUtil.parse(DateUtil.now());
        } else {
            dtEndTime = DateUtil.parse(endTime);
        }
        Long currentTime = System.currentTimeMillis();
        if (dtStartTime.getTime() > currentTime || dtEndTime.getTime() > currentTime ||
                DateUtil.beginOfDay(dtStartTime).getTime() > DateUtil.beginOfDay(dtEndTime).getTime()) {
            return BaseResponse.failure(ErrorCodeEnum.ERROR_WEB_DATE_TIME_PARAM);
        }

        strStartTime = DateUtil.formatDate(dtStartTime);
        strEndTime = DateUtil.formatDate(dtEndTime);
        List<String> dateList = new ArrayList<>();

        while (!DateUtil.formatDate(dtStartTime).equals(DateUtil.formatDate(dtEndTime))) {
            String strTime = DateUtil.formatDate(dtStartTime);
            log.info("format date. strTime={}", strTime);
            dtStartTime = DateUtil.offsetDay(dtStartTime, 1);
            dateList.add(strTime);
        }
        dateList.add(DateUtil.formatDate(dtEndTime));
        Collections.reverse(dateList);
        ChannelLiveCodeStatisticsResponse response = new ChannelLiveCodeStatisticsResponse();

        if (ChannelContactWayStatisticsEnum.DATE.getDesc().equals(StatisticsType)) {
            response = ChannelLiveCodeStatisticsResponse
                    .builder()
                    .total(dateList.size())
                    .detail(dateList.stream().map(e -> {
                        Integer totalCount = weComChannelLiveCodeStatisticRepository.getCountByDate(corpId, channelId,
                                e);
                        WeComChannelLiveCodeStatisticEntity entity =
                                weComChannelLiveCodeStatisticRepository.queryByDate(corpId, channelId, e);
                        log.info("query statistic for date. totalCount={}, entity={}", totalCount, entity);
                        return ChannelLiveCodeStatisticsResponse
                                .ContactWayStatistics.builder()
                                .addExtUserDate(e)
                                .extUserCount(totalCount == null ? 0 : totalCount)
                                .decreaseExtUserCount(entity.getDailyDecreaseExtUserCount() == null ? 0 :
                                        entity.getDailyDecreaseExtUserCount())
                                .increasedExtUserCount(entity.getDailyIncreasedExtUserCount() == null ? 0 :
                                        entity.getDailyIncreasedExtUserCount())
                                .build();
                    }).collect(Collectors.toList())).build();
        } else {
            Integer count = weComChannelLiveCodeStatisticRepository.countByMemberAndTime(corpId, channelId,
                    strStartTime, strEndTime);
            log.info("query statistic count for member. channelId={}, count={}, strStartTime={}, strEndTime={}",
                    channelId
                    , count, strStartTime,
                    strEndTime);
            if (count != null && count > 0) {
                List<WeComChannelLiveCodeStatisticEntity> entities =
                        weComChannelLiveCodeStatisticRepository.queryByMemberAndTime(corpId, channelId,
                                strStartTime, strEndTime);
                log.info("query statistic for member. channelId={}, entities={}", channelId, entities);

                if (CollectionUtils.isNotEmpty(entities)) {
                    entities.forEach(entity -> {
                    });
                }
                response = ChannelLiveCodeStatisticsResponse
                        .builder()
                        .total(entities.size())
                        .detail(entities.stream().map(e -> {
                            String name = memberMessageRepository.queryNameByMemberId(corpId, e.getMemberId());
                            Integer totalCount =
                                    weComChannelLiveCodeStatisticRepository.queryTotalCountByMember(corpId, channelId
                                            , e.getMemberId());
                            return ChannelLiveCodeStatisticsResponse
                                    .ContactWayStatistics.builder()
                                    .memberId(e.getMemberId())
                                    .memberName(name)
                                    .extUserCount(totalCount == null ? 0 : totalCount)
                                    .decreaseExtUserCount(e.getDailyDecreaseExtUserCount())
                                    .increasedExtUserCount(e.getDailyIncreasedExtUserCount())
                                    .build();
                        }).collect(Collectors.toList())).build();
            } else {
                List<WeComChannelLiveCodeMembersEntity> entities =
                        weComChannelLiveCodeMembersRepository.queryByLiveCodeUuidAndIsBackup(codeEntity.getUuid(),
                                Boolean.FALSE);
                response = ChannelLiveCodeStatisticsResponse
                        .builder()
                        .total(entities.size())
                        .detail(entities.stream().map(e -> {
                            String name = memberMessageRepository.queryNameByMemberId(corpId, e.getMemberId());
                            return ChannelLiveCodeStatisticsResponse
                                    .ContactWayStatistics.builder()
                                    .memberId(e.getMemberId())
                                    .memberName(name)
                                    .extUserCount(0)
                                    .decreaseExtUserCount(0)
                                    .increasedExtUserCount(0)
                                    .build();
                        }).collect(Collectors.toList())).build();
            }
        }
        log.info("query statistic message to response. channelId={}, response={}, strStartTime={}, strEndTime={}",
                channelId, response, strStartTime, strEndTime);
        return BaseResponse.success(response);
    }
}
