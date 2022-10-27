package com.easy.marketgo.core.repository.wecom.customer;

import com.easy.marketgo.core.entity.customer.WeComEventExternalUserEntity;
import com.easy.marketgo.core.entity.customer.WeComRelationMemberExternalUserEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/7/22 9:50 PM
 * Describe:
 */
public interface WeComEventExternalUserRepository extends CrudRepository<WeComEventExternalUserEntity, Long> {

    @Query("SELECT * FROM wecom_event_external_user WHERE corp_id = :corp_id AND member_id = :member_id AND" +
            " external_user_id = :external_user_id  order by event_time desc limit 1")
    WeComEventExternalUserEntity queryByMemberAndExternalUser(@Param("corp_id") String corpId,
                                                              @Param("member_id") String memberId,
                                                              @Param("external_user_id") String externalUserId,
                                                              @Param("event_type") String eventType);

    @Modifying
    @Query("UPDATE wecom_relation_member_external_user SET tags = :tags WHERE corp_id = :corp_id AND member_id = " +
            ":member_id AND" +
            " external_user_id = :external_user_id")
    int updateTagsByMemberAndExternalUser(@Param("corp_id") String corpId,
                                          @Param("member_id") String memberId,
                                          @Param("external_user_id") String externalUserId,
                                          @Param("tags") String tags);

    @Modifying
    @Query("UPDATE wecom_relation_member_external_user SET relation_type = 0 WHERE corp_id = :corp_id AND " +
            "external_user_id = :external_user_id")
    int updateRelationByExternalUser(@Param("corp_id") String corpId,
                                     @Param("external_user_id") String externalUserId);

    @Modifying
    @Query("UPDATE wecom_relation_member_external_user SET relation_type = 0 WHERE corp_id = :corp_id AND " +
            "external_user_id = :external_user_id AND member_id = :member_id")
    int deleteRelationByExternalUserAndMemberId(@Param("corp_id") String corpId,
                                                @Param("member_id") String memberId,
                                                @Param("external_user_id") String externalUserId);

    @Query("SELECT * FROM wecom_event_external_user WHERE corp_id = :corp_id group by event_time DESC LIMIT 1")
    WeComEventExternalUserEntity queryByCorpId(@Param("corp_id") String corpId);

    @Query("SELECT * FROM wecom_relation_member_external_user WHERE corp_id = :corp_id AND" +
            " external_user_id = :external_user_id LIMIT :offset, :limit")
    List<WeComRelationMemberExternalUserEntity> queryMembersByExternalUser(@Param("corp_id") String corpId,
                                                                           @Param("external_user_id") String externalUserId,
                                                                           @Param("offset") Integer offset,
                                                                           @Param("limit") Integer limit);

    @Query("SELECT COUNT(*) FROM wecom_relation_member_external_user WHERE corp_id = :corp_id AND" +
            " external_user_id = :external_user_id")
    int countByExternalUser(@Param("corp_id") String corpId,
                            @Param("external_user_id") String externalUserId);

    @Query("SELECT COUNT(*) FROM wecom_event_external_user WHERE corp_id = :corp_id AND event_type = :eventType AND " +
            "event_time like concat('', :date, '%')")
    int countByCorpIdAndEventTypeAndEventTime(@Param("corp_id") String corpId, String eventType, String date);

    @Query("SELECT COUNT(*) FROM wecom_event_external_user WHERE corp_id = :corp_id AND event_type = :eventType and " +
            "DATE(event_time)=DATE(curdate())")
    Integer countByCorpIdAndEventType(@Param("corp_id") String corpId, String eventType);
}
