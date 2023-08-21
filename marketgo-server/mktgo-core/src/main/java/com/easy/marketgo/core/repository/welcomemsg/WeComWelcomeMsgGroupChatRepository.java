package com.easy.marketgo.core.repository.welcomemsg;

import com.easy.marketgo.core.entity.welcomemsg.WeComWelcomeMsgGroupChatEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 4/21/23 6:08 PM
 * Describe:
 */

public interface WeComWelcomeMsgGroupChatRepository extends CrudRepository<WeComWelcomeMsgGroupChatEntity, Integer>,
        WeComWelcomeMsgGroupChatCustomizedRepository {


    @Query("SELECT * FROM wecom_welcome_msg_group_chat WHERE project_uuid = :project_uuid  and  corp_id=:corp_id AND name " +
            "= :name")
    WeComWelcomeMsgGroupChatEntity geWelComeMsgByName(@Param("project_uuid") String projectUuid,
                                                      @Param("corp_id") String corpId,
                                                      @Param("name") String name);

    @Query("SELECT * FROM wecom_welcome_msg_group_chat WHERE corp_id=:corp_id and uuid=:uuid ")
    WeComWelcomeMsgGroupChatEntity queryByCorpAndUuid(
            @Param("corp_id") String corpId, @Param("uuid") String uuid);

    @Modifying
    @Query("DELETE FROM wecom_welcome_msg_group_chat WHERE uuid = :uuid")
    int deleteByUuid(String uuid);
}
