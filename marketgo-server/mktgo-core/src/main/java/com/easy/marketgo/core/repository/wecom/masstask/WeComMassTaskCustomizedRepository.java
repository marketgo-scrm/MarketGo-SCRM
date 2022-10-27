package com.easy.marketgo.core.repository.wecom.masstask;

import com.easy.marketgo.core.entity.masstask.WeComMassTaskEntity;
import com.easy.marketgo.core.model.bo.QueryMassTaskBuildSqlParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/28/22 2:12 PM
 * Describe:
 */
public interface WeComMassTaskCustomizedRepository {
    List<WeComMassTaskEntity> listByBuildSqlParam(QueryMassTaskBuildSqlParam massTaskParam);

    Integer countByBuildSqlParam(QueryMassTaskBuildSqlParam massTaskParam);

    @Slf4j
    class WeComMassTaskCustomizedRepositoryImpl implements WeComMassTaskCustomizedRepository {
        @Resource
        private DataSource dataSource;

        @Override
        public List<WeComMassTaskEntity> listByBuildSqlParam(QueryMassTaskBuildSqlParam massTaskParam) {
            BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(massTaskParam);
            return new NamedParameterJdbcTemplate(this.dataSource).query(getSelectByCndSql(massTaskParam, false),
                    paramSource, new BeanPropertyRowMapper(WeComMassTaskEntity.class));
        }

        @Override
        public Integer countByBuildSqlParam(QueryMassTaskBuildSqlParam massTaskParam) {
            BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(massTaskParam);
            return new NamedParameterJdbcTemplate(this.dataSource).queryForObject(getSelectByCndSql(massTaskParam,
                    true),
                    paramSource, Integer.class);
        }

        private String getSelectByCndSql(QueryMassTaskBuildSqlParam massTaskParam, boolean isCount) {
            StringBuilder sql = new StringBuilder(
                    String.format("SELECT %s FROM wecom_mass_task WHERE project_uuid = :projectUuid AND task_type = " +
                            ":weComMassTaskTypeEnum", isCount ? "COUNT(*)" : "*"));
            if (StringUtils.isNotBlank(massTaskParam.getCorpId())) {
                sql.append(" AND corp_id = :corpId");
            }
            if (CollectionUtils.isNotEmpty(massTaskParam.getStatuses())) {
                sql.append(" AND task_status IN (:statuses)");
            }
            if (StringUtils.isNotEmpty(massTaskParam.getKeyword())) {
                sql.append(" AND (name LIKE CONCAT('%', :keyword, '%')");
                if (StringUtils.isNumeric(massTaskParam.getKeyword())) {
                    sql.append(" OR id = ").append(massTaskParam.getKeyword());
                }
                sql.append(")");
            }
            if (CollectionUtils.isNotEmpty(massTaskParam.getCreatorIds())) {
                sql.append(" AND creator_id IN (:creatorIds)");
            }
            if (massTaskParam.getStartTime() != null) {
                sql.append(" AND create_time >= :startTime");
            }
            if (massTaskParam.getEndTime() != null) {
                sql.append(" AND create_time < :endTime");
            }
            if (!isCount) {
                if (StringUtils.isNotBlank(massTaskParam.getSortKey())) {
                    sql.append(String.format(" ORDER BY %s %s", massTaskParam.getSortKey(),
                            massTaskParam.getSortOrderKey()));
                } else {
                    sql.append(String.format(" ORDER BY id %s", massTaskParam.getSortOrderKey()));
                }
                sql.append(" LIMIT :startIndex,:pageSize");
            }
            String sqlStr = sql.toString();
            log.info("Select by build sql param: sql={}", sqlStr);
            return sqlStr;
        }
    }
}
