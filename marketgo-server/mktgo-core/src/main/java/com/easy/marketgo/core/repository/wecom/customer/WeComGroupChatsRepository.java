package com.easy.marketgo.core.repository.wecom.customer;

import com.easy.marketgo.core.entity.customer.WeComGroupChatsEntity;
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
public interface WeComGroupChatsRepository extends CrudRepository<WeComGroupChatsEntity, Long>,
        WeComGroupChatsCustomizedRepository {
    @Query("SELECT * FROM wecom_group_chats WHERE corp_id = :corp_id AND group_chat_id = :group_chat_id")
    WeComGroupChatsEntity queryByChatId(@Param("corp_id") String corpId,
                                        @Param("group_chat_id") String chatId);

    @Query("SELECT COUNT(*) FROM wecom_group_chats WHERE corp_id = :corp_id AND if" +
            "(:group_chat_name !=''," +
            "group_chat_name like concat('%'," +
            ":group_chat_name,'%'),1=1) AND if(:owner !='', owner IN(:owner), 1=1)")
    Integer countByGroupChatNameAndOwner(@Param("corp_id") String corpId,
                                         @Param("group_chat_name") String groupChatName,
                                         @Param("owner") List<String> owner);

    @Query("SELECT COUNT(*) FROM wecom_group_chats WHERE corp_id = :corp_id AND  owner IN (:owner)")
    Integer countByOwner(@Param("corp_id") String corpId, @Param("owner") List<String> owner);

    @Query("SELECT COUNT(*) FROM wecom_group_chats WHERE corp_id = :corp_id")
    Integer countByCorpId(@Param("corp_id") String corpId);


    @Query("SELECT * FROM wecom_group_chats WHERE corp_id = :corp_id AND if" +
            "(:group_chat_name !=''," +
            "group_chat_name like concat('%'," +
            ":group_chat_name,'%'),1=1) AND if(:owner !='', owner IN(:owner), 1=1) ORDER BY id ASC LIMIT :offset, " +
            ":limit")
    List<WeComGroupChatsEntity> getByGroupChatNameAndOwner(@Param("corp_id") String corpId,
                                                           @Param("group_chat_name") String groupChatName,
                                                           @Param("owner") List<String> owner,
                                                           @Param("offset") Integer offset,
                                                           @Param("limit") Integer limit);

    @Query("SELECT * FROM wecom_group_chats WHERE corp_id = :corp_id AND owner = :owner")
    List<WeComGroupChatsEntity> getGroupChatByOwner(@Param("corp_id") String corpId,
                                                    @Param("owner") String owner);

    @Query("SELECT distinct owner FROM wecom_group_chats WHERE corp_id = :corp_id")
    List<String> queryOwnersByCorpId(@Param("corp_id") String corpId);

    @Query("SELECT * FROM wecom_group_chats WHERE corp_id = :corpId AND group_chat_id IN (:groupChatId)")
    List<WeComGroupChatsEntity> queryByChatIds(String corpId, List<String> groupChatId);

    @Modifying
    @Query("DELETE FROM wecom_group_chats WHERE corp_id = :corp_id AND group_chat_id = :group_chat_id")
    int deleteByCorpIdAndChatId(@Param("corp_id") String corpId, @Param("group_chat_id") String chatId);

}
