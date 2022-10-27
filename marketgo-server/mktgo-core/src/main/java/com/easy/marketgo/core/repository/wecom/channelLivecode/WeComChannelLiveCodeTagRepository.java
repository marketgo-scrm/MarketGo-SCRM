package com.easy.marketgo.core.repository.wecom.channelLivecode;

import com.easy.marketgo.core.entity.channellive.WeComChannelLiveCodeTagEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-30 16:01:45
 * @description : WechatWorkContactWayTagEntity.java 活码标签表
 */
public interface WeComChannelLiveCodeTagRepository extends CrudRepository<WeComChannelLiveCodeTagEntity, Long> {

    @Query("select  * from wecom_channel_live_code_tag where channel_live_code_uuid=:liveCodeUuid")
    List<WeComChannelLiveCodeTagEntity> queryByLiveCodeUuid(@Param("liveCodeUuid") String liveCodeUuid);
}
