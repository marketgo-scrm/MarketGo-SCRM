package com.easy.marketgo.core.repository.welcomemsg;

import com.easy.marketgo.core.entity.welcomemsg.WeComWelcomeMsgGroupChatStatisticEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 4/21/23 6:08 PM
 * Describe:
 */


public interface WeComWelcomeMsgGroupChatStatisticRepository extends CrudRepository<WeComWelcomeMsgGroupChatStatisticEntity,
        Long> {

    @Query("select  * ,MAX(event_date) as event_date from wecom_welcome_msg_group_chat_statistic where  " +
            "corp_id=:corp_id and  welcome_msg_uuid=:uuid group by welcome_msg_uuid , corp_id ")
    WeComWelcomeMsgGroupChatStatisticEntity findLast(@Param("corp_id") String corpId, String uuid);

    @Query("select SUM(daily_send_user_count) from wecom_welcome_msg_group_chat_statistic where  " +
            "corp_id=:corp_id and welcome_msg_uuid=:uuid and DATE(event_date)=DATE(curdate())")
    Integer dailySendUserCount(@Param("corp_id") String corpId, String uuid);


    @Query("select sum(daily_send_user_count) from wecom_welcome_msg_group_chat_statistic where  " +
            "corp_id=:corp_id and  welcome_msg_uuid=:uuid ")
    Integer totalSendUserCount(@Param("corp_id") String corpId, String uuid);

    @Query("select sum(daily_send_user_count) from wecom_welcome_msg_group_chat_statistic where corp_id=:corp_id " +
            "and welcome_msg_uuid=:uuid  and member_id = :memberId")
    Integer queryTotalCountByMember(@Param("corp_id") String corpId, String uuid, String memberId);

    @Query("select sum(daily_send_user_count) from wecom_welcome_msg_group_chat_statistic where corp_id=:corp_id " +
            "and welcome_msg_uuid=:uuid  and member_id = :memberId and event_date <= :endTime AND event_date >= :startTime")
    Integer queryTotalCountByMemberAndTime(@Param("corp_id") String corpId, String uuid, String memberId,
                                           String startTime,
                                           String endTime);

    @Query("select  count(*)  from wecom_welcome_msg_group_chat_statistic where  corp_id=:corp_id " +
            "and welcome_msg_uuid=:uuid and event_date <= :endTime AND event_date >= :startTime"
    )
    Integer countByMemberAndTime(@Param("corp_id") String corpId, String uuid, String startTime,
                                 String endTime);

    @Query("select " +
            "     sum(daily_send_user_count) as daily_send_user_count " +
            "    ,DATE(event_date) as event_date,corp_id,welcome_msg_uuid" +
            " from wecom_welcome_msg_group_chat_statistic t where  t.corp_id=:corp_id " +
            "and  t.welcome_msg_uuid=:uuid " +
            "and DATE_SUB(CURDATE() ,INTERVAL :statisticsPeriod DAY) <=date(create_time) " +
            " group by  DATE(t.event_date) ,t.corp_id,t.welcome_msg_uuid   LIMIT  :offset, :limit ")
    List<WeComWelcomeMsgGroupChatStatisticEntity> queryByDates(@Param("corp_id") String corpId, String uuid,
                                                               @Param("statisticsPeriod") int statisticsPeriod,
                                                               @Param("offset") Integer offset,
                                                               @Param("limit") Integer limit);

    @Query("select sum(daily_send_user_count) as daily_send_user_count " +
            "    ,DATE(event_date) as event_date,corp_id, welcome_msg_uuid" +
            " from wecom_welcome_msg_group_chat_statistic t where  t.corp_id=:corp_id " +
            "and  t.welcome_msg_uuid=:uuid and t.event_date = :dayTime")
    WeComWelcomeMsgGroupChatStatisticEntity queryByDate(@Param("corp_id") String corpId, String uuid, String dayTime);

    @Query("select sum(daily_send_user_count) from wecom_welcome_msg_group_chat_statistic where " +
            "corp_id=:corp_id and welcome_msg_uuid=:uuid and event_date <= :dayTime")
    Integer getCountByDate(@Param("corp_id") String corpId, String uuid, String dayTime);

    @Query("select * from wecom_welcome_msg_group_chat_statistic t where  t.welcome_msg_uuid=:uuid and  t" +
            ".member_id=:memberId and DATE(t.event_date)=DATE(:eventDate)")
    WeComWelcomeMsgGroupChatStatisticEntity queryByMemberAndEventDate(String uuid,
                                                                      @Param("memberId") String memberId,
                                                                      @Param("eventDate") Date eventDate
    );

    @Modifying
    @Query("DELETE FROM wecom_welcome_msg_group_chat_statistic WHERE welcome_msg_uuid = :uuid")
    Integer deleteByUuid(String uuid);

}
