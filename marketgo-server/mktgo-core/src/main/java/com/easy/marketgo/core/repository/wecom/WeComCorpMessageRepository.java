package com.easy.marketgo.core.repository.wecom;

import com.easy.marketgo.core.entity.WeComCorpMessageEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/24/22 3:24 PM
 * Describe:
 */
public interface WeComCorpMessageRepository extends CrudRepository<WeComCorpMessageEntity, Long> {
    @Modifying
    @Query("UPDATE wecom_corp_config SET contacts_secret = :contacts_secret, contacts_token = :contacts_token, " +
            "contacts_encoding_aes_key = :contacts_encoding_aes_key WHERE " +
            "corp_id = :corp_id AND project_uuid = :project_uuid")
    Integer updateSecretByCorpId(@Param("corp_id") String corpId,
                             @Param("project_uuid") String projectUuid,
                             @Param("contacts_secret") String contactsSecret,
                             @Param("contacts_token") String contactsToken,
                             @Param("contacts_encoding_aes_key") String contactsEncodingAesKey);

    @Modifying
    @Query("UPDATE wecom_corp_config SET external_user_secret = :external_user_secret, external_user_token = " +
            ":external_user_token, external_user_encoding_aes_key = :external_user_encoding_aes_key WHERE " +
            "corp_id = :corp_id AND project_uuid = :project_uuid")
    Integer updateExternalUserSecretByCorpId(@Param("corp_id") String corpId,
                                         @Param("project_uuid") String projectUuid,
                                         @Param("external_user_secret") String externalUserSecret,
                                         @Param("external_user_token") String externalUserToken,
                                         @Param("external_user_encoding_aes_key") String externalUserEncodingAesKey);

    @Query("SELECT * FROM wecom_corp_config WHERE corp_id = :corp_id AND project_uuid = :project_uuid")
    WeComCorpMessageEntity getCorpConfigByCorp(@Param("project_uuid") String projectUuid,
                                               @Param("corp_id") String corpId);

    @Query("SELECT * FROM wecom_corp_config WHERE corp_id = :corp_id")
    WeComCorpMessageEntity getCorpConfigByCorpId(@Param("corp_id") String corpId);

    @Query("SELECT * FROM wecom_corp_config WHERE cred_file_name = :fileName")
    WeComCorpMessageEntity getCorpConfigByCredFileName(String fileName);

    @Query("SELECT * FROM wecom_corp_config WHERE corp_id is not null")
    List<WeComCorpMessageEntity> getCorpConfigList();

    @Query("SELECT * FROM wecom_corp_config WHERE project_uuid = :project_uuid")
    List<WeComCorpMessageEntity> getCorpConfigListByProjectUuid(@Param("project_uuid") String projectUuid);

    @Modifying
    @Query("UPDATE wecom_corp_config SET forward_address = :forwardAddress WHERE " +
            "corp_id = :corpId AND project_uuid = :projectUuid")
    Integer updateForwardAddressByCorpId(String projectUuid, String corpId, String forwardAddress);

    @Modifying
    @Query("UPDATE wecom_corp_config SET forward_customer_address = :forwardAddress WHERE " +
            "corp_id = :corpId AND project_uuid = :projectUuid")
    Integer updateForwardCustomerAddressByCorpId(String projectUuid, String corpId, String forwardAddress);

    @Modifying
    @Query("UPDATE wecom_corp_config SET cred_file_name = :fileName, cred_file_content = :fileContent WHERE " +
            "corp_id = :corpId AND project_uuid = :projectUuid")
    Integer updateCredFileMessageByCorpId(String projectUuid, String corpId, String fileName, String fileContent);
}
