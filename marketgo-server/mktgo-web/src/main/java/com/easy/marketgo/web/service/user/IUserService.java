package com.easy.marketgo.web.service.user;

import com.easy.marketgo.web.model.request.user.LoginUserRequest;
import com.easy.marketgo.web.model.request.user.SystemUserMessageRequest;
import com.easy.marketgo.web.model.request.user.UserChangePasswordRequest;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.web.model.response.user.LoginUserResponse;
import com.easy.marketgo.web.model.response.user.LogoutUserResponse;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-05 20:24:33
 * @description : UserLoginService.java
 */
public interface IUserService {

    /**
     * @param user
     * @return
     */
    LoginUserResponse login(LoginUserRequest user);

    /**
     * @return
     */
    LogoutUserResponse logout();

    /**
     * @param request
     */
    void changePassWord(UserChangePasswordRequest request);

    BaseResponse updateOrInsertUserMobile(String projectId, String corpId,
                                          SystemUserMessageRequest systemUserMessageRequest);

    BaseResponse updateSystemUserAuthStatus(String projectId, String corpId,
                                          SystemUserMessageRequest systemUserMessageRequest);
}
