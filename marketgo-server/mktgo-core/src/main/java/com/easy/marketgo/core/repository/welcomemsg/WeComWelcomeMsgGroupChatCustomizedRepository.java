package com.easy.marketgo.core.repository.welcomemsg;

import com.easy.marketgo.core.entity.welcomemsg.WeComWelcomeMsgGroupChatEntity;
import com.easy.marketgo.core.model.bo.QueryChannelLiveCodeBuildSqlParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-30 16:21:37
 * @description : WeComContactWayEntity.java 活码基本信息表
 */

public interface WeComWelcomeMsgGroupChatCustomizedRepository {
    List<WeComWelcomeMsgGroupChatEntity> listByBuildSqlParam(QueryChannelLiveCodeBuildSqlParam codeParam);

    Integer countByBuildSqlParam(QueryChannelLiveCodeBuildSqlParam codeParam);

    @Slf4j
    class WeComWelcomeMsgGroupChatCustomizedRepositoryImpl implements WeComWelcomeMsgGroupChatCustomizedRepository {
        @Resource
        private DataSource dataSource;

        @Override
        public List<WeComWelcomeMsgGroupChatEntity> listByBuildSqlParam(QueryChannelLiveCodeBuildSqlParam codeParam) {
            BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(codeParam);
            return new NamedParameterJdbcTemplate(this.dataSource).query(getSelectByCndSql(codeParam, false),
                    paramSource, new BeanPropertyRowMapper(WeComWelcomeMsgGroupChatEntity.class));
        }

        @Override
        public Integer countByBuildSqlParam(QueryChannelLiveCodeBuildSqlParam codeParam) {
            BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(codeParam);
            return new NamedParameterJdbcTemplate(this.dataSource).queryForObject(getSelectByCndSql(codeParam,
                    true), paramSource, Integer.class);
        }

        private String getSelectByCndSql(QueryChannelLiveCodeBuildSqlParam codeParam, boolean isCount) {
            StringBuilder sql = new StringBuilder(
                    String.format("SELECT %s FROM wecom_welcome_msg_group_chat WHERE project_uuid = :projectUuid ",
                            isCount ? "COUNT(*)" : "*"));
            if (StringUtils.isNotBlank(codeParam.getCorpId())) {
                sql.append(" AND corp_id = :corpId");
            }

            if (StringUtils.isNotEmpty(codeParam.getKeyword())) {
                sql.append(" AND (name LIKE CONCAT('%', :keyword, '%')");
                if (StringUtils.isNumeric(codeParam.getKeyword())) {
                    sql.append(" OR id = ").append(codeParam.getKeyword());
                }
                sql.append(")");
            }

            if (!isCount) {
                if (StringUtils.isNotBlank(codeParam.getSortKey())) {
                    sql.append(String.format(" ORDER BY %s %s", codeParam.getSortKey(),
                            codeParam.getSortOrderKey()));
                } else {
                    sql.append(String.format(" ORDER BY id %s", codeParam.getSortOrderKey()));
                }
                sql.append(" LIMIT :startIndex,:pageSize");
            }
            String sqlStr = sql.toString();
            log.info("Select by build sql param: sql={}", sqlStr);
            return sqlStr;
        }
    }
}
