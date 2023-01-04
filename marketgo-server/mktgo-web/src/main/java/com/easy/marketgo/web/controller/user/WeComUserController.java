package com.easy.marketgo.web.controller.user;

import com.easy.marketgo.web.annotation.TokenIgnore;
import com.easy.marketgo.web.model.request.user.*;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.web.model.response.customer.WeComGroupChatsResponse;
import com.easy.marketgo.web.model.response.user.*;
import com.easy.marketgo.web.service.user.IOrganizationalStructureService;
import com.easy.marketgo.web.service.user.IUserPermissionsService;
import com.easy.marketgo.web.service.user.IUserRoleService;
import com.easy.marketgo.web.service.user.IUserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

import static com.easy.marketgo.common.constants.ApiConstants.API_REFRESH_TOKEN;
import static com.easy.marketgo.common.constants.ApiConstants.API_TOKEN;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-05 19:42:56
 * @description : weComUserLoginController.java
 */
@Api(value = "用户角色管理", tags = "用户角色管理")
@RestController
@RequestMapping(value = "/mktgo/wecom/user")
public class WeComUserController {

    @Autowired
    private IUserService userLoginService;
    @Autowired
    private IUserRoleService useRoleService;
    @Autowired
    private IUserPermissionsService userPermissionsService;
    @Autowired
    private IOrganizationalStructureService organizationalStructureService;

    @ApiOperation(value = "用户登录", nickname = "loginOrRegisterUser", notes = "", response =
            BaseResponse.class)
    @PostMapping(value = {"/login"}, produces = {"application/json"})
    @TokenIgnore
    public ResponseEntity<BaseResponse<LoginUserResponse>> login(@RequestBody @Validated LoginUserRequest request) {

        LoginUserResponse response = userLoginService.login(request);

        return ResponseEntity.ok().headers(httpHeaders -> {
            if (Objects.nonNull(response)) {
                httpHeaders.add(API_TOKEN, response.getToken());
                httpHeaders.add(API_REFRESH_TOKEN, response.getRefreshToken());
            }

        }).body(BaseResponse.success(response));
    }

    @ApiOperation(value = "用户登出", nickname = "logout", notes = "", response =
            BaseResponse.class)
    @PostMapping(value = {"/logout"}, produces = {"application/json"})
    public ResponseEntity<BaseResponse<LogoutUserResponse>> logout() {

        LogoutUserResponse response = userLoginService.logout();

        return ResponseEntity.ok().body(BaseResponse.success(response));
    }

    @ApiOperation(value = "用户修改密码", nickname = "changePassWord", notes = "", response =
            BaseResponse.class)
    @PostMapping(value = {"/changePassWord"}, produces = {"application/json"})
    public ResponseEntity<BaseResponse<Void>> changePassWord(@RequestBody @Validated UserChangePasswordRequest request) {

        userLoginService.changePassWord(request);

        return ResponseEntity.ok().body(BaseResponse.success());
    }


    @ApiOperation(value = "角色列表", nickname = "userRoleList", notes = "", response =
            BaseResponse.class)
    @PostMapping(value = {"/role/list"}, produces = {"application/json"})
    public ResponseEntity<BaseResponse<UserRoleListResponse>> roleList(@RequestBody @Validated UserRoleListRequest request) {

        UserRoleListResponse userResponse = useRoleService.userRoleList(request);

        return ResponseEntity.ok(BaseResponse.success(userResponse));
    }

    @ApiOperation(value = "根据角色查询成员列表", nickname = "queryUserList", notes = "", response =
            BaseResponse.class)
    @PostMapping(value = {"/role/list/query"}, produces = {"application/json"})
    public ResponseEntity<BaseResponse<UserRoleListQueryResponse>> queryUserList(@RequestBody @Validated UserRoleListQueryRequest request) {

        UserRoleListQueryResponse response = useRoleService.queryMembers(request);

        return ResponseEntity.ok().body(BaseResponse.success(response));
    }

    @ApiOperation(value = "添加角色", nickname = "roleAdd", notes = "", response =
            BaseResponse.class)
    @PostMapping(value = {"/role/add"}, produces = {"application/json"}, name = "添加角色")
    public ResponseEntity<BaseResponse<UserRoleListResponse>> roleAdd(@RequestBody @Validated UserRoleAddRequest request) {

        UserRoleListResponse userResponse = useRoleService.userRoleAdd(request);

        return ResponseEntity.ok(BaseResponse.success(userResponse));
    }

    @ApiOperation(value = "单人，批量授予用户角色", nickname = "roleAuthorization", notes = "", response =
            BaseResponse.class)
    @PostMapping(value = {"/role/authorization"}, produces = {"application/json"}, name = "角色授权")
    public ResponseEntity<BaseResponse<UserRoleListResponse>> roleAuthorization(@RequestBody @Validated UserRoleAuthorizationRequest request) {

        UserRoleListResponse userResponse = useRoleService.userRoleChange(request);

        return ResponseEntity.ok(BaseResponse.success(userResponse));
    }


    @ApiOperation(value = "获取角色功能权限列表", nickname = "queryPermissionsList", notes = "", response =
            BaseResponse.class)
    @PostMapping(value = {"/permissions/list/query"}, produces = {"application/json"}, name = "获取角色功能权限列表")
    public ResponseEntity<BaseResponse<List<RolePermissionsResponse>>> queryPermissionsList(@RequestBody @Validated RolePermissionsRequest request) {

        List<RolePermissionsResponse> responses = userPermissionsService.fetchUserPermissions(request);

        return ResponseEntity.ok(BaseResponse.success(responses));
    }

    @ApiOperation(value = "角色功能授权", nickname = "permissionsAuthorization", notes = "", response =
            BaseResponse.class)
    @PostMapping(value = {"/permissions/authorization"}, produces = {"application/json"}, name = "角色功能授权")
    public ResponseEntity<BaseResponse<List<RolePermissionsResponse>>> permissionsAuthorization(@RequestBody @Validated RolePermissionsAuthorizationRequest request) {

        List<RolePermissionsResponse> responses = userPermissionsService.permissionsAuthorization(request);

        return ResponseEntity.ok(BaseResponse.success(responses));
    }

    @ApiOperation(value = "组织架构", nickname = "organizationalStructure", notes = "", response =
            BaseResponse.class)
    @PostMapping(value = {"/organizational/structure"}, produces = {"application/json"}, name = "组织架构")
    public ResponseEntity<BaseResponse<OrganizationalStructureResponse>> organizationalStructure(@RequestBody @Validated OrganizationalStructureRequest request) {

        OrganizationalStructureResponse responses = organizationalStructureService.fetchStructures(request);

        return ResponseEntity.ok(BaseResponse.success(responses));
    }

    @ApiOperation(value = "根据部门名称查询人员", nickname = "organizationalStructureQuery", notes = "", response =
            BaseResponse.class)
    @PostMapping(value = {"/organizational/structure/query"}, produces = {"application/json"}, name = "根据部门名称查询人员")
    public ResponseEntity<BaseResponse<OrganizationalStructureQueryResponse>> organizationalStructureQuery(@RequestBody @Validated OrganizationalStructureQueryRequest request) {

        OrganizationalStructureQueryResponse responses = organizationalStructureService.fetchStructures(request);

        return ResponseEntity.ok(BaseResponse.success(responses));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComGroupChatsResponse.class)
    })
    @ApiOperation(value = "添加或者更新员工手机号", nickname = "updateOrInsertUserMobile", notes = "", response =
            BaseResponse.class)
    @RequestMapping(value = {"/save"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity updateOrInsertUserMobile(@NotNull @Valid @RequestParam(value = "project_id", required =
            true) String projectId, @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value =
            "corp_id", required = true) String corpId,
                                                   @ApiParam(value = "系统用户信息", required = true) @RequestBody @Valid SystemUserMessageRequest systemUserMessageRequest) {
        return ResponseEntity.ok(userLoginService.updateOrInsertUserMobile(projectId, corpId,
                systemUserMessageRequest));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = WeComGroupChatsResponse.class)
    })
    @ApiOperation(value = "修改授权状态", nickname = "updateSystemUserAuthStatus", notes = "", response =
            BaseResponse.class)
    @RequestMapping(value = {"/auth_status"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity updateSystemUserAuthStatus(@NotNull @Valid @RequestParam(value = "project_id", required =
            true) String projectId, @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value =
            "corp_id", required = true) String corpId,
                                                   @ApiParam(value = "系统用户信息", required = true) @RequestBody @Valid SystemUserMessageRequest systemUserMessageRequest) {
        return ResponseEntity.ok(userLoginService.updateSystemUserAuthStatus(projectId, corpId,
                systemUserMessageRequest));
    }

    @ApiOperation(value = "获取当前项目下当前企业登录用户角色信息", nickname = "userRoleInfo", notes = "", response =
            BaseResponse.class)
    @PostMapping(value = {"/role/info"}, produces = {"application/json"})
    public ResponseEntity<BaseResponse<List<RolePermissionsResponse>>> userRoleInfo(@RequestBody @Validated UserRoleInfoRequest request) {
        List<RolePermissionsResponse> response = useRoleService.userRolePermissionsInfo(request);
        return ResponseEntity.ok(BaseResponse.success(response));
    }
}
