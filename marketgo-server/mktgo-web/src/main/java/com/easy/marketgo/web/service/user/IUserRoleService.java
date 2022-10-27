package com.easy.marketgo.web.service.user;

import com.easy.marketgo.web.model.request.user.UserRoleAddRequest;
import com.easy.marketgo.web.model.request.user.UserRoleAuthorizationRequest;
import com.easy.marketgo.web.model.request.user.UserRoleInfoRequest;
import com.easy.marketgo.web.model.request.user.UserRoleListQueryRequest;
import com.easy.marketgo.web.model.request.user.UserRoleListRequest;
import com.easy.marketgo.web.model.response.user.RolePermissionsResponse;
import com.easy.marketgo.web.model.response.user.UserRoleInfo;
import com.easy.marketgo.web.model.response.user.UserRoleListQueryResponse;
import com.easy.marketgo.web.model.response.user.UserRoleListResponse;

import java.util.List;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-16 09:23:43
 * @description : IUseRoleService.java
 */
public interface IUserRoleService {


    UserRoleListResponse userRoleList(UserRoleListRequest request);

    UserRoleListResponse userRoleAdd(UserRoleAddRequest request);


    UserRoleListQueryResponse queryMembers(UserRoleListQueryRequest request);


    UserRoleListResponse userRoleChange(UserRoleAuthorizationRequest request);

    List<RolePermissionsResponse> userRolePermissionsInfo(UserRoleInfoRequest request);

}
