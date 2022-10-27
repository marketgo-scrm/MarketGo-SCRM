package com.easy.marketgo.core.repository.wecom.customer;

import com.easy.marketgo.core.entity.customer.WeComMemberMessageEntity;
import com.easy.marketgo.core.model.bo.QueryMemberBuildSqlParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/20/22 5:31 PM
 * Describe:
 */
public interface WeComMembersCustomizedRepository {
    List<WeComMemberMessageEntity> listByParam(QueryMemberBuildSqlParam param);

    Integer countByParam(QueryMemberBuildSqlParam param);

    @Slf4j
    @Service
    class WeComMembersCustomizedRepositoryImpl implements WeComMembersCustomizedRepository {

        @Resource
        private DataSource dataSource;

        @Override
        public List<WeComMemberMessageEntity> listByParam(QueryMemberBuildSqlParam param) {
            BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(param);
            return new NamedParameterJdbcTemplate(this.dataSource).query(buildSqlForParam(param, false),
                    paramSource, new BeanPropertyRowMapper(WeComMemberMessageEntity.class));
        }

        @Override
        public Integer countByParam(QueryMemberBuildSqlParam param) {
            BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(param);
            return new NamedParameterJdbcTemplate(this.dataSource).queryForObject(buildSqlForParam(param, true),
                    paramSource, Integer.class);
        }

        private String buildSqlForParam(QueryMemberBuildSqlParam param, boolean countFlag) {
            StringBuilder sql = new StringBuilder(
                    String.format("SELECT %s FROM wecom_members ",
                            countFlag ? "COUNT(*)" : "*"));

            if (StringUtils.isNotBlank(param.getCorpId())) {
                sql.append(" WHERE corp_id = :corpId  AND status=1 ");
            }

            if (StringUtils.isNotEmpty(param.getKeyword())) {
                sql.append(" AND (member_name LIKE CONCAT('%', :keyword, '%')");
                if (StringUtils.isNumeric(param.getKeyword())) {
                    sql.append(" OR id = ").append(param.getKeyword());
                }
                sql.append(")");
            }
            if (CollectionUtils.isNotEmpty(param.getDepartments())) {
                boolean appendFlag = true;
                for (Long department : param.getDepartments()) {
                    sql.append(String.format(" %s find_in_set(%s, department) ", appendFlag ? "AND (" : "OR",
                            department));
                    appendFlag = false;
                }
                sql.append(" ) ");
            }
            if (!countFlag && param.getStartIndex() != null && param.getPageSize() != null) {
                sql.append(" LIMIT :startIndex,:pageSize");
            }
            String sqlBuilder = sql.toString();
            log.info("select member by param sql={}", sqlBuilder);
            return sqlBuilder;
        }
    }
}
