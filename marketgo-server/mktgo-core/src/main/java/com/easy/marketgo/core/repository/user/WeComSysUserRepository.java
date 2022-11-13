package com.easy.marketgo.core.repository.user;

import com.easy.marketgo.core.entity.WeComSysUserEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-05 19:45:35
 * @description : WeComUserRepository.java
 */
public interface WeComSysUserRepository extends CrudRepository<WeComSysUserEntity, Integer> {

    WeComSysUserEntity queryByUserName(@Param("userName") String userName);

    @Modifying
    @Query("DELETE FROM wecom_sys_user WHERE user_name=:user_name")
    int deleteByUserName(@Param("user_name") String userName);

    @Modifying
    @Query("UPDATE wecom_sys_user SET auth_status=:auth_status WHERE user_name=:user_name")
    int updateAuthStatusByUserName(@Param("user_name") String userName, @Param("auth_status") Boolean authStatus);
}
