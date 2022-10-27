package com.easy.marketgo.web.service.user.impl;

import cn.hutool.core.util.PhoneUtil;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.utils.PasswordHelper;
import com.easy.marketgo.common.utils.UserInfo;
import com.easy.marketgo.common.utils.UuidUtils;
import com.easy.marketgo.core.entity.TenantConfigEntity;
import com.easy.marketgo.core.entity.WeComSysUserEntity;
import com.easy.marketgo.core.entity.WeComUserTenantLinkEntity;
import com.easy.marketgo.core.redis.RedisService;
import com.easy.marketgo.core.repository.user.WeComSysUserRepository;
import com.easy.marketgo.core.repository.wecom.TenantConfigRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import com.easy.marketgo.core.repository.wecom.WeComUserTenantLinkRepository;
import com.easy.marketgo.web.client.ClientRequestContextHolder;
import com.easy.marketgo.web.model.request.user.LoginUserRequest;
import com.easy.marketgo.web.model.request.user.UserChangePasswordRequest;
import com.easy.marketgo.web.model.response.user.LoginUserResponse;
import com.easy.marketgo.web.model.response.user.LogoutUserResponse;
import com.easy.marketgo.web.service.user.IUserService;
import com.easy.marketgo.web.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-05 20:26:04
 * @description : UserLoginServiceImpl.java
 */
@Component
@Slf4j
public class UserServiceImpl implements IUserService {

    @Autowired
    private WeComSysUserRepository sysUserRepository;
    @Autowired
    private WeComUserTenantLinkRepository userTenantLinkRepository;
    @Autowired
    private TenantConfigRepository tenantConfigRepository;
    @Autowired
    private WeComMemberMessageRepository weComMemberMessageRepository;

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private RedisService redisService;
    private static  final  String USER_TOKEN_KEY="marketgo_user_token_%s";

    @Override
    public LogoutUserResponse logout() {

        String userName = ClientRequestContextHolder.current().getUserName();
        String token = jwtUtils.generateToken(userName);
        redisService.delete(String.format(USER_TOKEN_KEY,token));
        return new LogoutUserResponse(userName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassWord(UserChangePasswordRequest request) {

        if (!StringUtils.equals(request.getRecChangePassWord(),request.getChangePassWord())){
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_PASS_WORD);
        }

        log.info("校验用户是否存在企业微信员工目录中{}", request);
        if (!checkWeComUser(request.getUserName())) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_REQUEST_USER_COMP_IS_EMPTY);
        }


        log.info("校验用户是否存在系统用户中{}", request);
        if (!checkSysUer(request.getUserName())) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_USER_IS_NOT_EXISTS);

        }
        if (!validUser(request.getUserName(),request.getPassWord())) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_REQUEST_USER_PASS_IS_VALID);
        }
        UserInfo user = UserInfo.builder()
                                .userName(request.getUserName())
                                .password(request.getChangePassWord())
                                .salt("")
                                .build();
        PasswordHelper.encryptPassword(user);
        WeComSysUserEntity entity = sysUserRepository.queryByUserName(request.getUserName());
        entity.setPassword(user.getPassword());
        entity.setSalt(user.getSalt());
        sysUserRepository.save(entity);

        String token = jwtUtils.generateToken(request.getUserName());
        redisService.delete(String.format(USER_TOKEN_KEY,token));
    }

    @Override
    @Transactional
    public LoginUserResponse login(LoginUserRequest request) {


        log.info("用户登录开始{}", request);

        log.info("校验用户是否存在企业微信员工目录中{}", request);
        if (!checkWeComUser(request.getUserName())) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_REQUEST_USER_COMP_IS_EMPTY);
        }


        log.info("校验用户是否存在系统用户中{}", request);
        if (!checkSysUer(request.getUserName())) {
            log.info("执行注册用户逻辑{}", request);
            register(request);
        }
        if (!validUser(request.getUserName(),request.getPassWord())) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_REQUEST_USER_PASS_IS_VALID);
        }
        String token = jwtUtils.generateToken(request.getUserName());
        String refreshToken = jwtUtils.generateRefreshToken(request.getUserName());
        LoginUserResponse userResponse = new LoginUserResponse()
                .setUserName(request.getUserName())
                                                                .setToken(token)
                                                                .setRefreshToken(refreshToken);
        redisService.set(String.format(USER_TOKEN_KEY,token),refreshToken,60*60*6L);
        return userResponse;
    }

    private boolean checkSysUer(String userName) {

        return Objects.nonNull(sysUserRepository.queryByUserName(userName));
    }

    private boolean checkWeComUser(String userName) {
        if (StringUtils.equals(userName,"admin")) {
            return  true;
        }else{
            boolean phone = PhoneUtil.isPhone(userName);
            if (!phone){
                throw new CommonException(ErrorCodeEnum.ERROR_WEB_USER_IS_NOT_EXISTS);
            }
        }
        return weComMemberMessageRepository.existsByMobile(userName);
    }

    private boolean validUser(String userName ,String password) {

        WeComSysUserEntity entity = sysUserRepository.queryByUserName(userName);
        UserInfo user = UserInfo.builder()
                                .userName(entity.getUserName())
                                .password(entity.getPassword())
                                .salt(entity.getSalt())
                                .build();
        return PasswordHelper.validPassword(user, password);
    }

    private void register(LoginUserRequest request) {

        TenantConfigEntity tenantConfigEntity = tenantConfigRepository.findAll().iterator().next();
        if (Objects.isNull(tenantConfigEntity)) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_TENANT_IS_EMPTY);
        }


        UserInfo user = UserInfo.builder()
                                .userName(request.getUserName())
                                .password(request.getPassWord())
                                .salt("")
                                .build();
        PasswordHelper.encryptPassword(user);
        WeComSysUserEntity entity = new WeComSysUserEntity();
        entity.setUserName(user.getUserName());
        entity.setPassword(user.getPassword());
        entity.setSalt(user.getSalt());
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        entity.setUuid(UuidUtils.generateUuid());
        WeComSysUserEntity userEntity = sysUserRepository.save(entity);


        WeComUserTenantLinkEntity userTenantLink = new WeComUserTenantLinkEntity();
        userTenantLink.setUserUuid(userEntity.getUuid());
        userTenantLink.setTenantUuid(tenantConfigEntity.getUuid());
        userTenantLinkRepository.save(userTenantLink);


    }

}
