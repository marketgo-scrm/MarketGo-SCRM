package com.easy.marketgo.core.repository.wecom.channelLivecode;

import com.easy.marketgo.core.entity.channellive.WeComChannelLiveCodeMembersEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-30 16:20:03
 * @description : WechatWorkContactWayUserEntity.java 活码使用员工配置表
 */
public interface WeComChannelLiveCodeMembersRepository extends CrudRepository<WeComChannelLiveCodeMembersEntity, Long> {

    @Modifying
    @Query("UPDATE wecom_channel_live_code_members SET add_limit_count = :add_limit_count WHERE " +
            "member_id = :member_id AND channel_live_code_uuid = :channel_live_code_uuid")
    int updateLimitCountByMemberId(@Param("channel_live_code_uuid") String channelLiveCodeUuid,
                                   @Param("member_id") String memberId,
                                   @Param("add_limit_count") Integer addLimitCount);


    @Modifying
    @Query("UPDATE wecom_channel_live_code_members SET online_status = :online_status WHERE is_backup = :is_backup AND " +
            "channel_live_code_uuid = :channel_live_code_uuid")
    int updateOnlineStatusByUuidAndIsBackup(@Param("channel_live_code_uuid") String channelLiveCodeUuid,
                                            @Param("is_backup") Boolean isBackup,
                                            @Param("online_status") Boolean onlineStatus);

    @Query("select * from wecom_channel_live_code_members WHERE member_id = :member_id AND channel_live_code_uuid = " +
            ":channel_live_code_uuid")
    List<WeComChannelLiveCodeMembersEntity> queryByMemberId(@Param("channel_live_code_uuid") String channelLiveCodeUuid,
                                                            @Param("member_id") String memberId);

    @Query("select * from wecom_channel_live_code_members WHERE member_id = :member_id AND channel_live_code_uuid = " +
            ":channel_live_code_uuid AND is_backup = :is_backup")
    WeComChannelLiveCodeMembersEntity queryByMemberIdAndIsBackup(@Param("channel_live_code_uuid") String channelLiveCodeUuid,
                                                            @Param("member_id") String memberId, @Param("is_backup") Boolean isBackup);

    @Query("select * from wecom_channel_live_code_members WHERE channel_live_code_uuid = :channel_live_code_uuid")
    List<WeComChannelLiveCodeMembersEntity> queryByLiveCodeUuid(@Param("channel_live_code_uuid") String channelLiveCodeUuid);

    @Query("select * from wecom_channel_live_code_members WHERE " +
            " channel_live_code_uuid = :channel_live_code_uuid AND is_backup = :is_backup")
    List<WeComChannelLiveCodeMembersEntity> queryByLiveCodeUuidAndIsBackup(@Param("channel_live_code_uuid") String channelLiveCodeUuid, @Param("is_backup") Boolean isBackup);

    @Modifying
    @Query("DELETE FROM wecom_channel_live_code_members WHERE channel_live_code_uuid = :channel_live_code_uuid")
    int deleteByUuid(@Param("channel_live_code_uuid") String uuid);
}
