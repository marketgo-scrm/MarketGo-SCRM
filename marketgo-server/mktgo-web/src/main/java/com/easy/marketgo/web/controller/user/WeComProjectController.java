package com.easy.marketgo.web.controller.user;

import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.web.model.request.ProjectCreateRequest;
import com.easy.marketgo.web.model.response.ProjectFetchResponse;
import com.easy.marketgo.web.service.project.IProjectService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-18 20:18:50
 * @description : WeComProjectController.java
 */
@Api(value = "项目管理", tags = "项目管理")
@RestController
@RequestMapping(value = "/mktgo/wecom/project/")
public class WeComProjectController {

    @Autowired
    private IProjectService projectService;

    @ApiOperation(value = "项目列表", nickname = "projectList", notes = "", response =
            BaseResponse.class)
    @PostMapping(value = {"list"}, produces = {"application/json"})
    public ResponseEntity<BaseResponse<ProjectFetchResponse>> projectList() {

        ProjectFetchResponse response = projectService.fetchProjects();

        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "创建项目", nickname = "createProject", notes = "", response =
            BaseResponse.class)
    @RequestMapping(value = {"/create"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity createProject(
            @ApiParam(value = "项目创建请求", required = true) @RequestBody @Valid ProjectCreateRequest projectCreateRequest) {
        return ResponseEntity.ok(projectService.createProject(projectCreateRequest));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "检查项目名称", nickname = "createProject", notes = "", response =
            BaseResponse.class)
    @RequestMapping(value = {"/check_name"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity checkProjectName(
            @ApiParam(value = "项目创建请求", required = true) @RequestBody @Valid ProjectCreateRequest projectCreateRequest) {
        return ResponseEntity.ok(projectService.createProject(projectCreateRequest));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "检查项目名称", nickname = "checkProjectName", notes = "", response =
            BaseResponse.class)
    @RequestMapping(value = {"/check_name"}, produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity checkProjectName(
            @ApiParam(value = "企微项目uuid", required = false) @NotNull @Valid @RequestParam(value = "project_id",
                    required = false) Integer projectId,
            @ApiParam(value = "任务中文名", required = true) @NotNull @Valid @RequestParam(value = "name", required
                    = true) String name) {
        return ResponseEntity.ok(projectService.checkName(projectId, name));
    }
}
