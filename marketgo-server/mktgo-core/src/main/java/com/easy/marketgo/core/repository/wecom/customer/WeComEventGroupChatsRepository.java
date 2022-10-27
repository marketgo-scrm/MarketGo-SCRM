package com.easy.marketgo.core.repository.wecom.customer;

import com.easy.marketgo.core.entity.customer.WeComEventGroupChatsEntity;
import com.easy.marketgo.core.entity.tag.WeComCorpTagEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/7/22 7:28 PM
 * Describe:
 */
public interface WeComEventGroupChatsRepository extends CrudRepository<WeComEventGroupChatsEntity, Long> {
    @Query("SELECT * FROM wecom_event_group_chat WHERE corp_id = :corpId group by event_time DESC LIMIT 1")
    WeComEventGroupChatsEntity queryByCorpId(String corpId);

    @Modifying
    @Query("DELETE FROM wecom_corp_tags WHERE corp_id = :corpId")
    int deleteByCorpId(String corpId);

    @Modifying
    @Query("UPDATE wecom_corp_tags SET deleted = 1 WHERE corp_id = :corpId AND tag_id = :tagId")
    int deleteByCorpIdAndTagId(String corpId, String tagId);

    @Query("SELECT * FROM wecom_corp_tags WHERE corp_id = :corpId AND tag_id IN (:tag_ids)")
    List<WeComCorpTagEntity> findByCorpIdAnAndTagIdIn(String corpId, @Param("tag_ids") List<String> tagIds);

    @Query("SELECT * FROM wecom_corp_tags WHERE corp_id = :corpId AND deleted = 0 AND type=:isTag")
    List<WeComCorpTagEntity> queryByCorpIdAndType(String corpId, Boolean isTag);

    @Query("SELECT * FROM wecom_corp_tags WHERE corp_id = :corpId AND deleted = 0 AND (name LIKE CONCAT('%', " +
            ":keyword, '%'))")
    List<WeComCorpTagEntity> queryByCorpIdAndKeyword(String corpId, String keyword);

    @Query("SELECT * FROM wecom_corp_tags WHERE corp_id = :corpId AND deleted = 0 AND type=:isTag AND " +
            "group_id=:groupId")
    List<WeComCorpTagEntity> queryTagByCorpIdAndType(String corpId, Boolean isTag, String groupId);

    @Query("SELECT COUNT(*) from wecom_event_group_chat WHERE corp_id = :corpId AND event_type = :eventType AND " +
            "update_detail= :updateDetail and DATE(event_time)=DATE(curdate())")
    Integer countByCorpIdAndEventType(String corpId, String eventType, String updateDetail);
}
