package com.easy.marketgo.core.repository.wecom.customer;

import com.easy.marketgo.core.entity.customer.WeComGroupChatsEntity;
import com.easy.marketgo.core.model.taskcenter.QueryGroupChatsBuildSqlParam;
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
 * @data : 7/16/22 10:52 PM
 * Describe:
 */
public interface WeComGroupChatsCustomizedRepository {

    List<WeComGroupChatsEntity> listGroupChatsByCnd(QueryGroupChatsBuildSqlParam param);

    Integer countByCnd(QueryGroupChatsBuildSqlParam param);


    @Slf4j
    @Service
    class WeComGroupChatsCustomizedRepositoryImpl implements WeComGroupChatsCustomizedRepository {

        @Resource
        private DataSource dataSource;

        @Override
        public List<WeComGroupChatsEntity> listGroupChatsByCnd(QueryGroupChatsBuildSqlParam param) {
            BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(param);
            return new NamedParameterJdbcTemplate(this.dataSource).query(buildGroupChatsSqlForParam(param, false,
                    false),
                    paramSource, new BeanPropertyRowMapper(WeComGroupChatsEntity.class));
        }

        @Override
        public Integer countByCnd(QueryGroupChatsBuildSqlParam param) {
            BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(param);
            return new NamedParameterJdbcTemplate(this.dataSource).queryForObject(buildGroupChatsSqlForParam(param,
                    true, false), paramSource, Integer.class);
        }

        private String buildExcludeGroupChatsSqlForParam(QueryGroupChatsBuildSqlParam param) {
            StringBuilder builder = new StringBuilder();
            boolean startFlag = true;

            if (CollectionUtils.isNotEmpty(param.getExcludeGroupChatIds())) {
                builder.append(String.format(" %s group_chat_id NOT IN (:excludeGroupChatIds) ", startFlag ? "(" :
                        param.getExcludeRelation()));
                startFlag = false;
            }

            if (StringUtils.isNotEmpty(param.getExcludeGroupChatName())) {
                builder.append(String.format(" %s ", startFlag ? " ( " : param.getExcludeRelation()));
                builder.append(" group_chat_name NOT LIKE CONCAT('%', :excludeGroupChatName, '%')");
                startFlag = false;
            }

            if (StringUtils.isNotEmpty(param.getExcludeUserCountFunction()) && param.getExcludeUserCount() != null) {
                builder.append(String.format(" %s user_count %s :excludeUserCount ", startFlag ? " (" :
                                param.getExcludeRelation(),
                        (param.getExcludeUserCountFunction().equals("GT")) ? " < " : " > "));
                startFlag = false;
            }

            if (StringUtils.isNotBlank(param.getExcludeStartTime())) {
                builder.append(String.format(" %s (chat_create_time < :excludeStartTime", startFlag ? "(" :
                        param.getExcludeRelation()));
            }
            if (StringUtils.isNotBlank(param.getExcludeEndTime())) {
                builder.append(" OR chat_create_time > :excludeEndTime)");
                startFlag = false;
            }
            if (!startFlag) {
                builder.append(" ) ");
            }

            return builder.toString();
        }

        private String buildGroupChatsSqlForParam(QueryGroupChatsBuildSqlParam param, boolean countFlag,
                                                  boolean paging) {
            StringBuilder sql = new StringBuilder(
                    String.format("SELECT %s FROM wecom_group_chats ", countFlag ? "COUNT(*)" : "*"));
            if (StringUtils.isNotBlank(param.getCorpId())) {
                sql.append(" WHERE corp_id = :corpId ");
            }
            if (CollectionUtils.isNotEmpty(param.getMemberIds())) {
                sql.append(" AND owner IN (:memberIds) ");
            }

            boolean startFlag = true;

            if (CollectionUtils.isNotEmpty(param.getGroupChatIds())) {
                sql.append(" AND (  group_chat_id IN (:groupChatIds) ");
                startFlag = false;
            }

            if (StringUtils.isNotEmpty(param.getGroupChatName())) {
                sql.append(String.format(" %s ", startFlag ? "AND (" : param.getRelation()));
                sql.append(" group_chat_name LIKE CONCAT('%', :groupChatName, '%')");
                startFlag = false;
            }

            if (StringUtils.isNotEmpty(param.getUserCountFunction()) && param.getUserCount() != null) {
                sql.append(String.format(" %s user_count %s :userCount ", startFlag ? "AND (" : param.getRelation(),
                        (param.getUserCountFunction().equals("GT")) ? " > " : " < "));
                startFlag = false;
            }
            if (StringUtils.isNotBlank(param.getStartTime())) {
                sql.append(String.format(" %s (chat_create_time >= :startTime", startFlag ? "AND (" :
                        param.getRelation()));
                startFlag = false;
            }
            if (StringUtils.isNotBlank(param.getEndTime())) {
                sql.append(" AND chat_create_time < :endTime)");
            }
            if (!startFlag) {
                sql.append(" ) ");
            }

            String builderString = buildExcludeGroupChatsSqlForParam(param);
            if (builderString.length() > 2) {
                sql.append(String.format("AND %s", builderString));
            }
            if (!countFlag && paging) {
                sql.append(" LIMIT :startIndex,:pageSize");
            }
            String sqlStr = sql.toString();
            log.info("select group chats by param sql={}", sqlStr);
            return sqlStr;
        }
    }
}
