package com.easy.marketgo.core.repository.wecom.channelLivecode;

import com.easy.marketgo.core.entity.channellive.WeComChannelLiveCodeEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-30 16:21:37
 * @description : WeComContactWayEntity.java 活码基本信息表
 */

public interface WeComChannelLiveCodeRepository extends CrudRepository<WeComChannelLiveCodeEntity, Integer>,
        WeComChannelLiveCodeCustomizedRepository {

    @Query("SELECT count(*) FROM wecom_channel_live_code WHERE name like concat('%':title'%') and  corp_id=:corp_id ")
    long countByTitle(@Param("corp_id") String corpId, @Param("title") String title);

    @Query("SELECT count(*) FROM wecom_channel_live_code WHERE corp_id=:corp_id ")
    long countByCorp(@Param("corp_id") String corpId);

    @Query("SELECT * FROM wecom_channel_live_code WHERE corp_id=:corp_id ")
    List<WeComChannelLiveCodeEntity> queryByCorp(@Param("corp_id") String corpId);

    @Query("SELECT * FROM wecom_channel_live_code WHERE name like concat('%':title'%') and  corp_id=:corp_id LIMIT " +
            ":offset, :limit"
    )
    List<WeComChannelLiveCodeEntity> queryByTitle(@Param("corp_id") String corpId, @Param("title") String title,
                                                  @Param("offset") Integer offset, @Param("limit") Integer limit);

    @Query("SELECT * FROM wecom_channel_live_code WHERE corp_id=:corp_id  limit  " +
            ":offset, :limit")
    List<WeComChannelLiveCodeEntity> queryByCorp(@Param("corp_id") String corpId, @Param("offset") Integer offset,
                                                 @Param("limit") Integer limit);

    @Query("SELECT * FROM wecom_channel_live_code WHERE name like concat('%':title'%') and  corp_id=:corp_id LIMIT " +
            ":offset, :limit"
    )
    WeComChannelLiveCodeEntity queryByChannelName(@Param("project_uuid") String projectUuid,
                                                  @Param("corp_id") String corpId, String name);


    @Query("SELECT * FROM wecom_channel_live_code WHERE project_uuid = :project_uuid  and  corp_id=:corp_id AND name " +
            "= :name")
    WeComChannelLiveCodeEntity geChannelLiveCodeByName(@Param("project_uuid") String projectUuid,
                                                       @Param("corp_id") String corpId,
                                                       @Param("name") String name);

    @Query("SELECT * FROM wecom_channel_live_code WHERE   corp_id=:corp_id and state=:state ")
    WeComChannelLiveCodeEntity queryByCorpAndSate(
            @Param("corp_id") String corpId, @Param("state") String state);

    @Query("SELECT * FROM wecom_channel_live_code WHERE corp_id=:corp_id and uuid=:uuid ")
    WeComChannelLiveCodeEntity queryByCorpAndUuid(
            @Param("corp_id") String corpId, @Param("uuid") String uuid);

    @Modifying
    @Query("DELETE FROM wecom_channel_live_code WHERE uuid = :uuid")
    int deleteByUuid(String uuid);
}
