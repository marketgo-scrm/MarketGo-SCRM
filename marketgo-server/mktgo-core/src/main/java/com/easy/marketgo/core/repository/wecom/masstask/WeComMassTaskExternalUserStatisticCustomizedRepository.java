package com.easy.marketgo.core.repository.wecom.masstask;

import com.easy.marketgo.core.entity.masstask.WeComMassTaskExternalUserStatisticEntity;
import com.easy.marketgo.core.model.bo.QueryMassTaskExternalUserMetricsBuildSqlParam;
import lombok.extern.slf4j.Slf4j;
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
 * @data : 7/8/22 4:24 PM
 * Describe:
 */
public interface WeComMassTaskExternalUserStatisticCustomizedRepository {

    List<WeComMassTaskExternalUserStatisticEntity> listByBuildSqlParam(QueryMassTaskExternalUserMetricsBuildSqlParam param);

    Integer countByBuildSqlParam(QueryMassTaskExternalUserMetricsBuildSqlParam param);

    @Slf4j
    class WeComMassTaskExternalUserStatisticCustomizedRepositoryImpl implements WeComMassTaskExternalUserStatisticCustomizedRepository {
        @Resource
        private DataSource dataSource;

        @Override
        public List<WeComMassTaskExternalUserStatisticEntity> listByBuildSqlParam(QueryMassTaskExternalUserMetricsBuildSqlParam param) {
            BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(param);
            return new NamedParameterJdbcTemplate(this.dataSource).query(getSelectByCndSql(param, false),
                    paramSource, new BeanPropertyRowMapper(WeComMassTaskExternalUserStatisticEntity.class));
        }

        @Override
        public Integer countByBuildSqlParam(QueryMassTaskExternalUserMetricsBuildSqlParam param) {
            BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(param);
            return new NamedParameterJdbcTemplate(this.dataSource).queryForObject(getSelectByCndSql(param,
                    true), paramSource, Integer.class);
        }

        private String getSelectByCndSql(QueryMassTaskExternalUserMetricsBuildSqlParam param, boolean isCount) {
            StringBuilder sql = new StringBuilder(
                    String.format("SELECT %s FROM wecom_mass_task_statistic_external_user WHERE project_uuid = :projectUuid"
                            , isCount ? "COUNT(*)" : "*"));

            if (StringUtils.isNotEmpty(param.getKeyword())) {
                sql.append(" AND (external_user_name LIKE CONCAT('%', :keyword, '%')");
                if (StringUtils.isNumeric(param.getKeyword())) {
                    sql.append(" OR id = ").append(param.getKeyword());
                }
                sql.append(")");
            }

            if (StringUtils.isNotEmpty(param.getStatus())) {
                sql.append(" AND status = :status");
            }

            if (StringUtils.isNotEmpty(param.getTaskUuid())) {
                sql.append(" AND task_uuid = :taskUuid");
            }

            if (!isCount) {
                sql.append(" ORDER BY add_comments_time desc LIMIT :startIndex,:pageSize");
            }
            String sqlStr = sql.toString();
            log.info("Select by build sql param: sql={}", sqlStr);
            return sqlStr;
        }
    }
}
