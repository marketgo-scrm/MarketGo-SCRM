package com.easy.marketgo.core.repository.wecom.customer;

import com.easy.marketgo.core.entity.customer.WeComGroupChatMembersEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/16/22 10:52 PM
 * Describe:
 */
public interface WeComGroupChatMembersRepository extends CrudRepository<WeComGroupChatMembersEntity, Long> {
    @Modifying
    @Query("DELETE FROM wecom_group_chat_members WHERE corp_id = :corp_id AND group_chat_id = :group_chat_id")
    int deleteByCorpIdAndChatId(@Param("corp_id") String corpId, @Param("group_chat_id") String chatId);

    @Query("SELECT COUNT(*) FROM wecom_group_chat_members WHERE corp_id = :corp_id AND group_chat_id = :group_chat_id")
    Integer countByGroupChatId(@Param("corp_id") String corpId,
                               @Param("group_chat_id") String groupChatId);

    @Query("SELECT * FROM wecom_group_chat_members WHERE corp_id = :corp_id AND group_chat_id = :group_chat_id ORDER " +
            "BY id ASC LIMIT " +
            ":offset, :limit")
    List<WeComGroupChatMembersEntity> queryByGroupChatId(@Param("corp_id") String corpId,
                                                         @Param("group_chat_id") String groupChatId,
                                                         @Param("offset") Integer offset,
                                                         @Param("limit") Integer limit);

    @Query("SELECT DISTINCT group_chat_id FROM wecom_group_chat_members WHERE corp_id = :corpId AND user_id IN " +
            "(:userId) ")
    List<String> queryByUserIds(String corpId, List<String> userId);

    @Query("SELECT DISTINCT group_chat_id FROM wecom_group_chat_members WHERE corp_id = :corpId AND user_id = " +
            ":userId")
    List<String> queryByUserId(String corpId, String userId);
}
