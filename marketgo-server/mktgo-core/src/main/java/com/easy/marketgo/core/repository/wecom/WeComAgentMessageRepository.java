package com.easy.marketgo.core.repository.wecom;

import com.easy.marketgo.core.entity.WeComAgentMessageEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-30 21:56
 * Describe:
 */
public interface WeComAgentMessageRepository extends CrudRepository<WeComAgentMessageEntity, Long> {

    @Query("SELECT * FROM wecom_agent_config WHERE corp_id = :corp_id AND agent_id = :agent_id")
    WeComAgentMessageEntity getWeComAgentByCorpAndAgent(@Param("corp_id") String corpId,
                                                        @Param("agent_id") String agentId);

    @Modifying
    @Query("UPDATE wecom_agent_config SET agent_id = :agent_id, secret = :secret WHERE corp_id = :corp_id AND " +
            "project_uuid = :project_uuid")
    int updateAgentMessageByCorpId(@Param("corp_id") String corpId,
                                   @Param("project_uuid") String projectUuid,
                                   @Param("agent_id") String agentId,
                                   @Param("secret") String secret);

    @Query("SELECT * FROM wecom_agent_config WHERE corp_id = :corp_id AND project_uuid = :project_uuid AND is_chief=1")
    WeComAgentMessageEntity getWeComAgentByCorp(@Param("project_uuid") String projectUuid,
                                                @Param("corp_id") String corpId);

    @Query("SELECT * FROM wecom_agent_config WHERE corp_id = :corp_id AND is_chief=1")
    WeComAgentMessageEntity getAgentMessageByCorpId(@Param("corp_id") String corpId);
}
