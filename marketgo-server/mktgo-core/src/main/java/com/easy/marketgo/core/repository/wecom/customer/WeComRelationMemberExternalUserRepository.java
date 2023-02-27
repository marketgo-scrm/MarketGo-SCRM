package com.easy.marketgo.core.repository.wecom.customer;

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
public interface WeComRelationMemberExternalUserRepository extends CrudRepository<WeComRelationMemberExternalUserEntity, Long>, WeComExternalUserCustomizedRepository {

    @Query("SELECT * FROM wecom_relation_member_external_user WHERE corp_id = :corp_id AND member_id = :member_id AND" +
            " external_user_id = :external_user_id")
    WeComRelationMemberExternalUserEntity queryByMemberAndExternalUser(@Param("corp_id") String corpId,
                                                                       @Param("member_id") String memberId,
                                                                       @Param("external_user_id") String externalUserId);

    @Query("SELECT * FROM wecom_relation_member_external_user WHERE corp_id = :corp_id AND" +
            " external_user_id IN (:external_user_id)")
    List<WeComRelationMemberExternalUserEntity> queryExternalUserByCorpId(@Param("corp_id") String corpId,
                                                                           @Param("external_user_id") List<String> externalUserId);

    @Modifying
    @Query("UPDATE wecom_relation_member_external_user SET tags = :tags WHERE corp_id = :corp_id AND member_id = " +
            ":member_id AND external_user_id = :external_user_id")
    int updateTagsByMemberAndExternalUser(@Param("corp_id") String corpId,
                                          @Param("member_id") String memberId,
                                          @Param("external_user_id") String externalUserId,
                                          @Param("tags") String tags);

    @Modifying
    @Query("UPDATE wecom_relation_member_external_user SET member_id = :new_member_id WHERE corp_id = :corp_id AND " +
            "member_id = :member_id")
    int updateMemberByMemberId(@Param("corp_id") String corpId,
                               @Param("member_id") String memberId,
                               @Param("new_member_id") String newMemberId);

    @Modifying
    @Query("UPDATE wecom_relation_member_external_user SET relation_type = :relation_type WHERE corp_id = :corp_id " +
            "AND member_id= :member_id")
    int updateRelationByMemberId(@Param("corp_id") String corpId,
                                 @Param("member_id") String memberId,
                                 @Param("relation_type") Integer relationType);

    @Modifying
    @Query("UPDATE wecom_relation_member_external_user SET relation_type = 1 WHERE corp_id = :corp_id AND " +
            "external_user_id = :external_user_id")
    int updateRelationByExternalUser(@Param("corp_id") String corpId,
                                     @Param("external_user_id") String externalUserId);

    @Modifying
    @Query("UPDATE wecom_relation_member_external_user SET relation_type = :relation_type WHERE corp_id = :corp_id AND " +
            "external_user_id = :external_user_id AND member_id = :member_id")
    int deleteRelationByExternalUserAndMemberId(@Param("corp_id") String corpId,
                                                @Param("member_id") String memberId,
                                                @Param("external_user_id") String externalUserId,
                                                @Param("relation_type") Integer relationType);

    @Query("SELECT * FROM wecom_relation_member_external_user WHERE corp_id = :corp_id AND" +
            " external_user_id = :external_user_id LIMIT 1")
    WeComRelationMemberExternalUserEntity queryByExternalUser(@Param("corp_id") String corpId,
                                                              @Param("external_user_id") String externalUserId);

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

    @Query("SELECT COUNT(distinct external_user_id) FROM wecom_relation_member_external_user WHERE corp_id = :corp_id" +
            " AND relation_type = 0")
    int countByCorpId(@Param("corp_id") String corpId);

    @Query("SELECT COUNT(*) FROM wecom_relation_member_external_user WHERE corp_id = :corp_id AND" +
            " member_id = :memberId")
    int countByCorpIdAndMemberId(@Param("corp_id") String corpId, @Param("memberId") String memberId);

    @Query("SELECT COUNT(*) FROM wecom_relation_member_external_user WHERE corp_id = :corp_id AND add_time <= " +
            ":addTime AND relation_type = 0")
    int countByCorpIdAndAddTime(@Param("corp_id") String corpId, String addTime);
}
