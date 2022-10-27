package com.easy.marketgo.core.repository.user;

import com.easy.marketgo.core.entity.WeComSysUserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-05 19:45:35
 * @description : WeComUserRepository.java
 */
public interface WeComSysUserRepository extends CrudRepository<WeComSysUserEntity, Integer> {

    WeComSysUserEntity queryByUserName(@Param("userName") String userName);


}
