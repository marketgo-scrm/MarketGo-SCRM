package com.easy.marketgo.web.service.user;

import com.easy.marketgo.web.model.request.user.RolePermissionsAuthorizationRequest;
import com.easy.marketgo.web.model.request.user.RolePermissionsRequest;
import com.easy.marketgo.web.model.response.user.RolePermissionsResponse;

import java.util.List;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-17 17:15:59
 * @description : IUserPermissionsService.java
 */
public interface IUserPermissionsService {

    List<RolePermissionsResponse> fetchUserPermissions(RolePermissionsRequest request);
    List<RolePermissionsResponse> permissionsAuthorization(RolePermissionsAuthorizationRequest request);
}
