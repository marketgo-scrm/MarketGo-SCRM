package com.easy.marketgo.core.repository.wecom.taskcenter;

import com.easy.marketgo.core.entity.taskcenter.WeComTaskCenterEntity;
import com.easy.marketgo.core.model.taskcenter.QueryTaskCenterBuildSqlParam;
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
public interface WeComTaskCenterCustomizedRepository {
    List<WeComTaskCenterEntity> listByBuildSqlParam(QueryTaskCenterBuildSqlParam param);

    Integer countByBuildSqlParam(QueryTaskCenterBuildSqlParam param);

    @Slf4j
    class WeComTaskCenterCustomizedRepositoryImpl implements WeComTaskCenterCustomizedRepository {
        @Resource
        private DataSource dataSource;

        @Override
        public List<WeComTaskCenterEntity> listByBuildSqlParam(QueryTaskCenterBuildSqlParam param) {
            BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(param);
            return new NamedParameterJdbcTemplate(this.dataSource).query(getSelectByCndSql(param, false),
                    paramSource, new BeanPropertyRowMapper(WeComTaskCenterEntity.class));
        }

        @Override
        public Integer countByBuildSqlParam(QueryTaskCenterBuildSqlParam param) {
            BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(param);
            return new NamedParameterJdbcTemplate(this.dataSource).queryForObject(getSelectByCndSql(param,
                    true),
                    paramSource, Integer.class);
        }

        private String getSelectByCndSql(QueryTaskCenterBuildSqlParam param, boolean isCount) {
            StringBuilder sql = new StringBuilder(
                    String.format("SELECT %s FROM wecom_task_center WHERE project_uuid = :projectUuid ", isCount ?
                            "COUNT(*)" : "*"));
            if (StringUtils.isNotBlank(param.getCorpId())) {
                sql.append(" AND corp_id = :corpId");
            }
            if (CollectionUtils.isNotEmpty(param.getStatuses())) {
                sql.append(" AND task_status IN (:statuses)");
            }

            if (CollectionUtils.isNotEmpty(param.getTaskTypes())) {
                sql.append(" AND task_type IN (:taskTypes)");
            }

            if (StringUtils.isNotEmpty(param.getKeyword())) {
                sql.append(" AND (name LIKE CONCAT('%', :keyword, '%')");
                if (StringUtils.isNumeric(param.getKeyword())) {
                    sql.append(" OR id = ").append(param.getKeyword());
                }
                sql.append(")");
            }
            if (CollectionUtils.isNotEmpty(param.getCreatorIds())) {
                sql.append(" AND creator_id IN (:creatorIds)");
            }
            if (param.getStartTime() != null) {
                sql.append(" AND create_time >= :startTime");
            }
            if (param.getEndTime() != null) {
                sql.append(" AND create_time < :endTime");
            }
            if (!isCount) {
                if (StringUtils.isNotBlank(param.getSortKey())) {
                    sql.append(String.format(" ORDER BY %s %s", param.getSortKey(),
                            param.getSortOrderKey()));
                } else {
                    sql.append(String.format(" ORDER BY id %s", param.getSortOrderKey()));
                }
                sql.append(" LIMIT :startIndex,:pageSize");
            }
            String sqlStr = sql.toString();
            log.info("select by build sql param: sql={}", sqlStr);
            return sqlStr;
        }
    }
}
