package com.easy.marketgo.core.repository.media;

import com.easy.marketgo.core.entity.WeComMediaResourceEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/24/22 3:29 PM
 * Describe:
 */
public interface WeComMediaResourceRepository extends CrudRepository<WeComMediaResourceEntity, Long> {

    @Modifying
    @Query("DELETE FROM wecom_media_resource WHERE uuid = :uuid")
    int deleteByUuid(String uuid);

    WeComMediaResourceEntity queryByUuid(String uuid);

    @Query("SELECT uuid FROM wecom_media_resource WHERE is_temp = 1 AND is_finish = 0 AND DATE_ADD(NOW(), INTERVAL " +
            ":hour HOUR) > expire_time")
    List<WeComMediaResourceEntity> getMediaByIsTemp(@Param("hour") Integer hour);

    @Modifying
    @Query("UPDATE wecom_media_resource SET is_finish = 1 WHERE uuid IN (:uuids)")
    int updateMediaByUuid(List<String> uuids);

    @Modifying
    @Query("DELETE FROM wecom_media_resource WHERE uuid IN (:uuids)")
    int deleteByUuids(List<String> uuids);
}
