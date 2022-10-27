package com.easy.marketgo.core.repository.wecom.channelLivecode;

import com.easy.marketgo.core.entity.channellive.WeComChannelLiveCodeStatisticEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-31 17:19:02
 * @description : WeComChannelLiveCodeStatisticRepository.java
 */
public interface WeComChannelLiveCodeStatisticRepository extends CrudRepository<WeComChannelLiveCodeStatisticEntity,
        Long> {


    @Query("select  * ,MAX(event_date) as event_date from wecom_channel_live_code_statistic where  corp_id=:corp_id " +
            "and  channel_live_code_uuid=:channelLiveCodeUuid group by  channel_live_code_uuid , corp_id ")
    WeComChannelLiveCodeStatisticEntity findLast(@Param("corp_id") String corpId,
                                                 @Param("channelLiveCodeUuid") String channelLiveCodeUuid);

    @Query("select SUM(daily_increased_ext_user_count) from wecom_channel_live_code_statistic where  " +
            "corp_id=:corp_id and  channel_live_code_uuid=:channelLiveCodeUuid and DATE(event_date)=DATE(curdate())")
    Integer dailyIncreasedExtUserCount(@Param("corp_id") String corpId, String channelLiveCodeUuid);


    @Query("select sum(daily_decrease_ext_user_count) from wecom_channel_live_code_statistic where  " +
            "corp_id=:corp_id " +
            "and  channel_live_code_uuid=:channelLiveCodeUuid " +
            "and DATE(event_date)=DATE(curdate())")
    Integer dailyDecreaseExtUserCount(@Param("corp_id") String corpId, String channelLiveCodeUuid);

    @Query("select sum(daily_decrease_ext_user_count) from wecom_channel_live_code_statistic where  " +
            "corp_id=:corp_id and  channel_live_code_uuid=:channelLiveCodeUuid ")
    Integer totalDecreaseExtUserCount(@Param("corp_id") String corpId, String channelLiveCodeUuid);

    @Query("select sum(daily_increased_ext_user_count) from wecom_channel_live_code_statistic where  " +
            "corp_id=:corp_id and  channel_live_code_uuid=:channelLiveCodeUuid ")
    Integer totalIncreasedExtUserCount(@Param("corp_id") String corpId, String channelLiveCodeUuid);

    @Query("select sum(daily_increased_ext_user_count) as daily_increased_ext_user_count, sum" +
            "(daily_decrease_ext_user_count) as daily_decrease_ext_user_count, member_id, member_name from " +
            "wecom_channel_live_code_statistic where  corp_id=:corp_id " +
            "and  channel_live_code_uuid=:channelLiveCodeUuid  and event_date <= :endTime " +
            "AND event_date >= :startTime GROUP BY member_id ")
    List<WeComChannelLiveCodeStatisticEntity> queryByMemberAndTime(@Param("corp_id") String corpId,
                                                                   @Param("channelLiveCodeUuid") String channelLiveCodeUuid,
                                                                   String startTime, String endTime);

    @Query("select sum(daily_increased_ext_user_count) from " +
            "wecom_channel_live_code_statistic where  corp_id=:corp_id " +
            "and  channel_live_code_uuid=:channelLiveCodeUuid  and member_id = :memberId")
    Integer queryTotalCountByMember(@Param("corp_id") String corpId, String channelLiveCodeUuid,
                                                            String memberId);

    @Query("select  count(*)  from wecom_channel_live_code_statistic where  corp_id=:corp_id " +
            "and  channel_live_code_uuid=:channelLiveCodeUuid and event_date <= :endTime AND event_date >= :startTime"
    )
    Integer countByMemberAndTime(@Param("corp_id") String corpId, String channelLiveCodeUuid, String startTime,
                                 String endTime);


    @Query("select " +
            "     sum(daily_increased_ext_user_count) as daily_increased_ext_user_count " +
            "    ,sum(t.daily_decrease_ext_user_count) as daily_increased_ext_user_count " +
            "    ,DATE(event_date) as event_date,corp_id,channel_live_code_uuid" +
            " from wecom_channel_live_code_statistic t where  t.corp_id=:corp_id " +
            "and  t.channel_live_code_uuid=:channelLiveCodeUuid " +
            "and DATE_SUB(CURDATE() ,INTERVAL :statisticsPeriod DAY) <=date(create_time) " +
            " group by  DATE(t.event_date) ,t.corp_id,t.channel_live_code_uuid   LIMIT  :offset, :limit ")
    List<WeComChannelLiveCodeStatisticEntity> queryByDates(@Param("corp_id") String corpId,
                                                           @Param("channelLiveCodeUuid") String channelLiveCodeUuid,
                                                           @Param("statisticsPeriod") int statisticsPeriod,
                                                           @Param("offset") Integer offset,
                                                           @Param("limit") Integer limit);

    @Query("select sum(daily_increased_ext_user_count) as daily_increased_ext_user_count " +
            "    ,sum(t.daily_decrease_ext_user_count) as daily_decrease_ext_user_count " +
            "    ,DATE(event_date) as event_date,corp_id, channel_live_code_uuid" +
            " from wecom_channel_live_code_statistic t where  t.corp_id=:corp_id " +
            "and  t.channel_live_code_uuid=:channelLiveCodeUuid and t.event_date = :dayTime")
    WeComChannelLiveCodeStatisticEntity queryByDate(@Param("corp_id") String corpId,
                                                    @Param("channelLiveCodeUuid") String channelLiveCodeUuid,
                                                    String dayTime);

    @Query("select sum(daily_increased_ext_user_count) from wecom_channel_live_code_statistic where corp_id=:corp_id " +
            "and channel_live_code_uuid=:channelLiveCodeUuid " +
            "and event_date <= :dayTime")
    Integer getCountByDate(@Param("corp_id") String corpId,
                           @Param("channelLiveCodeUuid") String channelLiveCodeUuid, String dayTime);

    @Query("select * from wecom_channel_live_code_statistic t where  t.channel_live_code_uuid=:channelLiveCodeUuid " +
            "and  t.member_id=:memberId and DATE(t.event_date)=DATE(:eventDate)")
    WeComChannelLiveCodeStatisticEntity queryByMemberAndEventDate(@Param("channelLiveCodeUuid") String channelLiveCodeUuid,
                                                                  @Param("memberId") String memberId,
                                                                  @Param("eventDate") Date eventDate
    );

    @Modifying
    @Query("DELETE FROM wecom_channel_live_code_statistic WHERE channel_live_code_uuid = :channel_live_code_uuid")
    Integer deleteByUuid(@Param("channel_live_code_uuid") String uuid);

}
