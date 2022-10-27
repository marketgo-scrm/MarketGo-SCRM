package com.easy.marketgo.core.repository.wecom.customer;

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
public interface WeComCorpTagRepository extends CrudRepository<WeComCorpTagEntity, Long> {
    @Query("SELECT * FROM wecom_corp_tags WHERE corp_id = :corpId AND tag_id = :tagId")
    WeComCorpTagEntity queryByTagId(String corpId, String tagId);

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

    @Query("SELECT COUNT(*) from wecom_corp_tags WHERE corp_id = :corpId AND tag_id = :tagId AND deleted = 0 AND name" +
            " = :name")
    Integer countByCorpIdAndTagId(String corpId, String tagId, String name);
}
