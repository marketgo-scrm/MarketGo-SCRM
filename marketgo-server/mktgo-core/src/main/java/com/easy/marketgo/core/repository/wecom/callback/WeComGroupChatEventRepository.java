package com.easy.marketgo.core.repository.wecom.callback;

import com.easy.marketgo.core.entity.callback.WeComExternalUserEventEntity;
import com.easy.marketgo.core.entity.callback.WeComGroupChatEventEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/8/22 4:25 PM
 * Describe:
 */
public interface WeComGroupChatEventRepository extends CrudRepository<WeComGroupChatEventEntity, Long> {
    @Modifying
    @Query("UPDATE wecom_group_chat_event set send_id = :new_send_id, send_id_type= :send_id_type where " +
            "send_id=:old_send_id")
    void updateSendIdMsg(@Param("new_send_id") String newSendId, @Param("old_send_id") String oldSendId,
                         @Param("send_id_type") String sendIdType);

    @Query("SELECT * FROM wecom_group_chat_event WHERE task_uuid = :taskUuid AND statistic_type = :statisticType")
    WeComExternalUserEventEntity queryByTaskUuidAAndStatisticType(String taskUuid, String statisticType);
}
