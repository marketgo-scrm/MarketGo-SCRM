package com.easy.marketgo.core.repository.wecom;

import com.easy.marketgo.core.entity.customer.WeComDepartmentEntity;
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
public interface WeComDepartmentRepository extends CrudRepository<WeComDepartmentEntity, Long> {
    @Modifying
    @Query("DELETE FROM wecom_departments WHERE corp_id = :corp_id")
    Integer deleteByCorpId(@Param("corp_id") String corpId);

    List<WeComDepartmentEntity> findByCorpId(String corpId);

    List<WeComDepartmentEntity> findByCorpIdAndParentIdIn(String corpId, List<Long> parentId);

    List<WeComDepartmentEntity> findByCorpIdAndDepartmentIdIn(String corpId, List<Long> departmentId);
}
