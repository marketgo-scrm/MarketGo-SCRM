package com.easy.marketgo.core.repository.wecom.customer;

import com.easy.marketgo.core.entity.customer.WeComMemberMessageEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/7/22 7:16 PM
 * Describe:
 */
public interface WeComMemberMessageRepository extends CrudRepository<WeComMemberMessageEntity, Integer>,
        WeComMembersCustomizedRepository {

    @Query("SELECT * FROM wecom_members WHERE corp_id = :corp_id AND member_id = :member_id")
    WeComMemberMessageEntity getMemberMessgeByMemberId(@Param("corp_id") String corpId,
                                                       @Param("member_id") String memberId);

    @Query("SELECT member_name FROM wecom_members WHERE corp_id = :corp_id AND member_id = :member_id")
    String queryNameByMemberId(@Param("corp_id") String corpId,
                               @Param("member_id") String memberId);

    @Query("SELECT member_id FROM wecom_members WHERE mobile = :mobile")
    String queryMemberIdByMobile(String mobile);

    @Query("SELECT * FROM wecom_members WHERE corp_id = :corpId AND member_id IN(:memberId)")
    List<WeComMemberMessageEntity> queryNameByMemberIds(String corpId, List<String> memberId);

    @Modifying
    @Query("UPDATE wecom_members SET member_name = :member_name, avatar = :avatar, alias = :alias, status = :status " +
            "WHERE corp_id = :corp_id AND member_id = :member_id")
    int updateInfoByMemberId(@Param("corp_id") String corpId,
                             @Param("member_id") String memberId,
                             @Param("member_name") String memberName,
                             @Param("avatar") String avatar,
                             @Param("alias") String alias,
                             @Param("status") Integer status);

    @Modifying
    @Query("UPDATE wecom_members SET mobile = :mobile WHERE corp_id = :corp_id AND member_id = :member_id")
    int updateMobileByMemberId(@Param("corp_id") String corpId,
                             @Param("member_id") String memberId,
                             @Param("mobile") String mobile);

    @Modifying
    @Query("UPDATE wecom_members SET member_id = :member_id, department = :department WHERE corp_id = :corp_id AND " +
            "member_id = :old_member_id")
    int updateMemberMsgByMemberId(@Param("corp_id") String corpId,
                                  @Param("member_id") String memberId,
                                  @Param("old_member_id") String oldMemberId,
                                  @Param("department") String department);

    @Modifying
    @Query("UPDATE wecom_members SET status = 5 WHERE corp_id = :corp_id")
    int deleteByCorpId(@Param("corp_id") String corpId);

    @Modifying
    @Query("UPDATE wecom_members SET status = 5 WHERE corp_id = :corp_id AND member_id = :member_id")
    int deleteByCorpIdAAndMemberId(@Param("corp_id") String corpId, @Param("member_id") String memberId);

    boolean existsByMobile(@Param("mobile") String mobile);

    @Query("SELECT * FROM wecom_members WHERE corp_id = :corpId AND status=1")
    List<WeComMemberMessageEntity> findByCorpId(String corpId);

    List<WeComMemberMessageEntity> findTopByCorpIdAndMemberNameLike(String corpId, String memberName);

    List<WeComMemberMessageEntity> findTopByCorpIdAndMobile(String corpId, String mobile);


    @Query("SELECT * FROM wecom_members t WHERE t.corp_id = :corpId  and member_name like CONCAT('%',:memberName," +
            "'%')")
    List<WeComMemberMessageEntity> findByCorpIdAndMemberNameLike(@Param("corpId") String corpId,
                                                                 @Param("memberName") String memberName);

}
