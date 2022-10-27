package com.easy.marketgo.core.repository.wecom.callback;

import com.easy.marketgo.core.entity.callback.WeComExternalUserEventEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/8/22 4:25 PM
 * Describe:
 */
public interface WeComExternalUserEventRepository extends CrudRepository<WeComExternalUserEventEntity, Long> {
    @Modifying
    @Query("UPDATE wecom_event_external_user set send_id = :new_send_id, send_id_type= :send_id_type where " +
            "send_id=:old_send_id")
    void updateSendIdMsg(@Param("new_send_id") String newSendId, @Param("old_send_id") String oldSendId,
                         @Param("send_id_type") String sendIdType);

    @Query("SELECT * FROM wecom_event_external_user WHERE corp_id = :corpId AND event_type = :eventType AND state = " +
            ":state AND create_time like :today%")
    List<WeComExternalUserEventEntity> queryByEventTypeAndEventType(String corpId, String eventType, String state,
                                                                    String today);
}
