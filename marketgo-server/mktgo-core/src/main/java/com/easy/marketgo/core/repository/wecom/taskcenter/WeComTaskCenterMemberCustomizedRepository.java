package com.easy.marketgo.core.repository.wecom.taskcenter;

import com.easy.marketgo.core.entity.taskcenter.WeComTaskCenterMemberEntity;
import com.easy.marketgo.core.model.taskcenter.QuerySubTaskCenterMemberBuildSqlParam;
import com.easy.marketgo.core.model.taskcenter.QueryTaskCenterMemberBuildSqlParam;
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
public interface WeComTaskCenterMemberCustomizedRepository {
    List<WeComTaskCenterMemberEntity> listByBuildSqlParam(QueryTaskCenterMemberBuildSqlParam param);

    Integer countByBuildSqlParam(QueryTaskCenterMemberBuildSqlParam param);

    List<WeComTaskCenterMemberEntity> listSubTaskByParam(QuerySubTaskCenterMemberBuildSqlParam param);

    Integer countSubTaskByParam(QuerySubTaskCenterMemberBuildSqlParam param);

    @Slf4j
    class WeComTaskCenterMemberCustomizedRepositoryImpl implements WeComTaskCenterMemberCustomizedRepository {
        @Resource
        private DataSource dataSource;

        @Override
        public List<WeComTaskCenterMemberEntity> listByBuildSqlParam(QueryTaskCenterMemberBuildSqlParam param) {
            BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(param);
            return new NamedParameterJdbcTemplate(this.dataSource).query(getSelectByCndSql(param, false),
                    paramSource, new BeanPropertyRowMapper(WeComTaskCenterMemberEntity.class));
        }

        @Override
        public Integer countByBuildSqlParam(QueryTaskCenterMemberBuildSqlParam param) {
            BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(param);
            return new NamedParameterJdbcTemplate(this.dataSource).queryForObject(getSelectByCndSql(param,
                    true),
                    paramSource, Integer.class);
        }

        @Override
        public List<WeComTaskCenterMemberEntity> listSubTaskByParam(QuerySubTaskCenterMemberBuildSqlParam param) {
            BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(param);
            return new NamedParameterJdbcTemplate(this.dataSource).query(getSubTaskByCndSql(param, false),
                    paramSource, new BeanPropertyRowMapper(WeComTaskCenterMemberEntity.class));
        }

        @Override
        public Integer countSubTaskByParam(QuerySubTaskCenterMemberBuildSqlParam param) {
            BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(param);
            return new NamedParameterJdbcTemplate(this.dataSource).queryForObject(getSubTaskByCndSql(param,
                    true),
                    paramSource, Integer.class);
        }

        private String getSelectByCndSql(QueryTaskCenterMemberBuildSqlParam param, boolean isCount) {
            StringBuilder sql = new StringBuilder(
                    String.format("SELECT %s FROM wecom_task_center_member WHERE corp_id = :corpId",
                            isCount ?
                                    "COUNT(DISTINCT task_uuid)" : "*"));

            if (StringUtils.isNotEmpty(param.getMemberId())) {
                sql.append(" AND member_id = :memberId");
            }
            if (CollectionUtils.isNotEmpty(param.getStatuses())) {
                sql.append(" AND task_status IN (:statuses)");
            }

            if (CollectionUtils.isNotEmpty(param.getTaskTypes())) {
                sql.append(" AND task_type IN (:taskTypes)");
            }

            if (param.getStartTime() != null) {
                sql.append(" AND create_time >= :startTime");
            }
            if (param.getEndTime() != null) {
                sql.append(" AND create_time < :endTime");
            }
            if (!isCount) {
                sql.append(String.format(" ORDER BY id %s", param.getSortOrderKey()));
                sql.append(" LIMIT :startIndex,:pageSize");
            }
            String sqlStr = sql.toString();
            log.info("select by build sql param: sql={}", sqlStr);
            return sqlStr;
        }

        private String getSubTaskByCndSql(QuerySubTaskCenterMemberBuildSqlParam param, boolean isCount) {
            StringBuilder sql = new StringBuilder(
                    String.format("SELECT %s FROM wecom_task_center_member WHERE corp_id = :corpId",
                            isCount ?
                                    "COUNT(*)" : "*"));

            if (StringUtils.isNotEmpty(param.getMemberId())) {
                sql.append(" AND member_id = :memberId");
            }
            if (StringUtils.isNotEmpty(param.getTaskUuid())) {
                sql.append(" AND task_uuid = :taskUuid");
            }
            if (!isCount) {
                sql.append(String.format(" ORDER BY id %s", param.getSortOrderKey()));
                sql.append(" LIMIT :startIndex,:pageSize");
            }
            String sqlStr = sql.toString();
            log.info("select sub task by build sql param: sql={}", sqlStr);
            return sqlStr;
        }
    }
}
