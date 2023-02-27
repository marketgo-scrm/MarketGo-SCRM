package com.easy.marketgo.core.repository.wecom.customer;

import com.easy.marketgo.core.entity.customer.WeComRelationMemberExternalUserEntity;
import com.easy.marketgo.core.model.bo.QueryExternalUserBuildSqlParam;
import com.easy.marketgo.core.model.bo.QueryUserGroupBuildSqlParam;
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
public interface WeComExternalUserCustomizedRepository {
    List<WeComRelationMemberExternalUserEntity> listByCnd(QueryExternalUserBuildSqlParam param);

    Integer countByCnd(QueryExternalUserBuildSqlParam param);

    List<WeComRelationMemberExternalUserEntity> listByUserGroupCnd(QueryUserGroupBuildSqlParam param);

    Integer countByUserGroupCnd(QueryUserGroupBuildSqlParam param);

    @Slf4j
    @Service
    class WeComExternalUserCustomizedRepositoryImpl implements WeComExternalUserCustomizedRepository {

        @Resource
        private DataSource dataSource;

        @Override
        public List<WeComRelationMemberExternalUserEntity> listByCnd(QueryExternalUserBuildSqlParam param) {
            BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(param);
            return new NamedParameterJdbcTemplate(this.dataSource).query(buildSqlForParam(param, false),
                    paramSource, new BeanPropertyRowMapper(WeComRelationMemberExternalUserEntity.class));
        }

        @Override
        public Integer countByCnd(QueryExternalUserBuildSqlParam param) {
            BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(param);
            return new NamedParameterJdbcTemplate(this.dataSource).queryForObject(buildSqlForParam(param, true),
                    paramSource, Integer.class);
        }

        @Override
        public List<WeComRelationMemberExternalUserEntity> listByUserGroupCnd(QueryUserGroupBuildSqlParam param) {
            BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(param);
            return new NamedParameterJdbcTemplate(this.dataSource).query(buildExternalUserSqlForParam(param, false,
                    false),
                    paramSource, new BeanPropertyRowMapper(WeComRelationMemberExternalUserEntity.class));
        }

        @Override
        public Integer countByUserGroupCnd(QueryUserGroupBuildSqlParam param) {
            BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(param);
            return new NamedParameterJdbcTemplate(this.dataSource).queryForObject(buildExternalUserSqlForParam(param,
                    true, false),
                    paramSource, Integer.class);
        }

        private String buildExcludeExternalUserForParam(QueryUserGroupBuildSqlParam param) {
            StringBuilder builder = new StringBuilder();
            boolean startFlag = true;
            if (CollectionUtils.isNotEmpty(param.getExcludeTags())) {
                builder.append(String.format(" %s( ", startFlag ? "(" : ""));
                boolean appendFlag = true;
                for (String tag : param.getExcludeTags()) {
                    builder.append(String.format(" %s NOT find_in_set('%s', tags) ", appendFlag ? "" :
                            param.getExcludeTagRelation(), tag));
                    appendFlag = false;
                }
                builder.append(" ) ");
                startFlag = false;
            }

            if (CollectionUtils.isNotEmpty(param.getExcludeGroupChats())) {
                builder.append(String.format(" %s external_user_id NOT IN (SELECT user_id FROM " +
                        "wecom_group_chat_members WHERE group_chat_id IN (:excludeGroupChats)) ", startFlag ? "(" :
                        param.getExcludeRelation()));
                startFlag = false;
            }


            if (CollectionUtils.isNotEmpty(param.getExcludeGenders())) {
                builder.append(String.format(" %s gender NOT IN (:excludeGenders) ", startFlag ? "(" :
                        param.getExcludeRelation()));
                startFlag = false;
            }

            if (StringUtils.isNotBlank(param.getExcludeStartTime())) {
                builder.append(String.format(" %s (add_time < :excludeStartTime", startFlag ? "(" :
                        param.getExcludeRelation()));
            }
            if (StringUtils.isNotBlank(param.getExcludeEndTime())) {
                builder.append(" AND add_time > :excludeEndTime)");
            }
            if (!startFlag) {
                builder.append(" ) ");
            }

            return builder.toString();
        }

        private String buildExternalUserSqlForParam(QueryUserGroupBuildSqlParam param, boolean countFlag,
                                                    boolean paging) {
            log.info("build user group for external user param. param={}", param);
            StringBuilder sql = new StringBuilder(
                    String.format("SELECT %s FROM wecom_relation_member_external_user ", countFlag ? "COUNT(*)" : "*"));
            if (StringUtils.isNotBlank(param.getCorpId())) {
                sql.append(" WHERE corp_id = :corpId ");
            }
            if (CollectionUtils.isNotEmpty(param.getMemberIds())) {
                sql.append(" AND member_id IN (:memberIds) ");
            }

            boolean startFlag = true;

            if (CollectionUtils.isNotEmpty(param.getGenders())) {
                sql.append(" AND (  gender IN (:genders) ");

                startFlag = false;
            }

            if (CollectionUtils.isNotEmpty(param.getTags())) {
                sql.append(String.format(" %s ( ", startFlag ? "AND (" : param.getRelation()));
                boolean appendFlag = true;
                for (String tag : param.getTags()) {
                    sql.append(String.format(" %s find_in_set('%s', tags) ", appendFlag ? "" : param.getTagRelation(),
                            tag));
                    appendFlag = false;
                }
                sql.append(" ) ");
                startFlag = false;
            }

            if (CollectionUtils.isNotEmpty(param.getGroupChats())) {
                sql.append(String.format(" %s external_user_id IN (SELECT user_id FROM " +
                        "wecom_group_chat_members WHERE group_chat_id IN (:groupChats)) ", startFlag ? "AND (" :
                        param.getRelation()));
                startFlag = false;
            }
            if (StringUtils.isNotBlank(param.getStartTime())) {
                sql.append(String.format(" %s (add_time >= :startTime", startFlag ? "AND (" : param.getRelation()));
                startFlag = false;
            }
            if (StringUtils.isNotBlank(param.getEndTime())) {
                sql.append(" AND add_time < :endTime)");
            }
            if (!startFlag) {
                sql.append(" ) ");
            }

            String builderString = buildExcludeExternalUserForParam(param);
            if (builderString.length() > 2) {
                sql.append(String.format("AND %s", builderString));
            }
            if (!countFlag && paging) {
                sql.append(" LIMIT :startIndex,:pageSize");
            }
            String sqlStr = sql.toString();
            log.info("select external user by param sql={}", sqlStr);
            return sqlStr;
        }

        private String buildSqlForParam(QueryExternalUserBuildSqlParam param, boolean countFlag) {
            StringBuilder sql = new StringBuilder(
                    String.format("SELECT %s FROM wecom_relation_member_external_user ",
                            countFlag ? "COUNT(*)" : "*"));
            if (StringUtils.isNotBlank(param.getCorpId())) {
                sql.append(" WHERE corp_id = :corpId");
            }
            if (StringUtils.isNotEmpty(param.getExternalUserName())) {
                sql.append(" AND (external_user_name LIKE CONCAT('%', :externalUserName, '%')");
                sql.append(")");
            }
            if (CollectionUtils.isNotEmpty(param.getGenders())) {
                sql.append(" AND gender IN (:genders)");
            }
            if (CollectionUtils.isNotEmpty(param.getMemberIds())) {
                sql.append(" AND member_id IN (:memberIds)");
            }
            if (CollectionUtils.isNotEmpty(param.getStatuses())) {
                sql.append(" AND relation_type IN (:statuses)");
            }

            if (CollectionUtils.isNotEmpty(param.getTags())) {
                boolean appendFlag = true;
                for (String tag : param.getTags()) {
                    sql.append(String.format(" %s find_in_set('%s', tags) ", appendFlag ? "AND (" : "OR",
                            tag));
                    appendFlag = false;
                }
                sql.append(" ) ");
            }

            if (CollectionUtils.isNotEmpty(param.getChannels())) {
                sql.append(" AND add_way IN (:channels)");
            }

            if (CollectionUtils.isNotEmpty(param.getGroupChats())) {
                sql.append(" AND external_user_id IN (SELECT user_id FROM wecom_group_chat_members WHERE " +
                        "group_chat_id IN (:groupChats))");
            }
            if (param.getStartTime() != null) {
                sql.append(" AND add_time >= :startTime");
            }
            if (param.getEndTime() != null) {
                sql.append(" AND add_time < :endTime");
            }
            if (!countFlag) {
                sql.append(" LIMIT :startIndex,:pageSize");
            }
            String sqlStr = sql.toString();
            log.info("select by param sql={}", sqlStr);
            return sqlStr;
        }
    }
}
