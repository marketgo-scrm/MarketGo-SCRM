package com.easy.marketgo.core.repository.usergroup;

import com.easy.marketgo.core.entity.usergroup.UserGroupCdpEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/17/22 11:27 AM
 * Describe:
 */
public interface UserGroupCdpRepository extends CrudRepository<UserGroupCdpEntity, Long> {
    @Query("SELECT DISTINCT(member_id) FROM user_group_cdp WHERE corp_id = :corpId AND cdp_uuid = :cdpUuid AND " +
            "crowd_code IN (:crowdCode) AND cdp_type = :cdpType")
    List<String> queryMemberByCorpIdAndCdp(String corpId, String cdpUuid, List<String> crowdCode, String cdpType);

    @Query("SELECT COUNT(DISTINCT(external_user_id)) FROM user_group_cdp WHERE corp_id = :corpId AND uuid = :uuid")
    Integer queryExternalUserCountByUuid(String corpId, String uuid);

    @Query("SELECT COUNT(DISTINCT(member_id)) FROM user_group_cdp WHERE corp_id = :corpId AND uuid = :uuid")
    Integer queryMemberCountByUuid(String corpId, String uuid);

    @Query("SELECT DISTINCT(external_user_id) FROM user_group_cdp WHERE corp_id = :corpId AND " +
            "member_id = :memberId AND cdp_uuid = :cdpUuid AND crowd_code IN (:crowdCode) AND cdp_type = :cdpType")
    List<String> queryExternalUsersByMemberId(String corpId, String memberId, String cdpUuid, List<String> crowdCode
            , String cdpType);

    @Modifying
    @Query("DELETE FROM user_group_cdp WHERE corp_id = :corpId AND uuid = :uuid")
    int deleteByUuid(String corpId, String uuid);
}
