package com.easy.marketgo.web.controller.user;

import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.web.model.response.ProjectFetchResponse;
import com.easy.marketgo.web.service.project.IProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-18 20:18:50
 * @description : WeComProjectController.java
 */
@Api(value = "项目管理", tags = "项目管理")
@RestController
@RequestMapping(value = "/mktgo/wecom/")
public class WeComProjectController {

    @Autowired
    private IProjectService projectService;



    @ApiOperation(value = "项目列表", nickname = "projectList", notes = "", response =
            BaseResponse.class)
    @PostMapping(value = {"/project/list"}, produces = {"application/json"})
    public ResponseEntity<BaseResponse<ProjectFetchResponse>> projectList() {

        ProjectFetchResponse response = projectService.fetchProjects();

        return ResponseEntity.ok(BaseResponse.success(response));
    }




}
