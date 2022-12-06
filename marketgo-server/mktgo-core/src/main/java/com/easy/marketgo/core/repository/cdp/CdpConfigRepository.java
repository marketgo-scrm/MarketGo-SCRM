package com.easy.marketgo.core.repository.cdp;

import com.easy.marketgo.core.entity.cdp.CdpConfigEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/24/22 3:29 PM
 * Describe:
 */
public interface CdpConfigRepository extends CrudRepository<CdpConfigEntity, Long> {

    @Modifying
    @Query("DELETE FROM cdp_config WHERE uuid = :uuid")
    int deleteByUuid(String uuid);

    @Query("SELECT * FROM cdp_config WHERE project_uuid = :projectId AND corp_id = :corpId AND cdp_type = :cdpType")
    CdpConfigEntity getCdpConfigByCorpIdAndCdpType(String projectId, String corpId, String cdpType);

    @Query("SELECT * FROM cdp_config WHERE id = :id")
    CdpConfigEntity getCdpConfigById(Integer id);

    @Query("SELECT * FROM cdp_config WHERE project_uuid = :projectId AND corp_id = :corpId AND status = 1")
    List<CdpConfigEntity> getCdpConfigByCorpId(String projectId, String corpId);

    @Modifying
    @Query("UPDATE cdp_config SET status = 0 WHERE project_uuid = :projectId AND corp_id = :corpId")
    int updateCdpStatusByCorpId(String projectId, String corpId);

    @Modifying
    @Query("UPDATE cdp_config SET status = :status WHERE project_uuid = :projectId AND corp_id = :corpId AND cdp_type" +
            " = :cdpType")
    int updateCdpStatusByCorpIdAndCdpType(String projectId, String corpId, String cdpType, Boolean status);

    @Modifying
    @Query("DELETE FROM cdp_config WHERE project_uuid = :projectId AND corp_id = :corpId AND cdp_type = :cdpType")
    int deleteByCorpIdAndCdpType(String projectId, String corpId, String cdpType);
}
