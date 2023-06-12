package com.easy.marketgo.gateway.controller;

import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.gateway.wecom.request.client.WeComUserVerifyRequest;
import com.easy.marketgo.gateway.wecom.request.client.WeComVerifySdkConfigRequest;
import com.easy.marketgo.react.model.QuerySignatureResponse;
import com.easy.marketgo.react.service.client.WeComClientVerifyService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/21/22 3:01 PM
 * Describe:
 */
@Api(value = "客户端验证管理", tags = "客户端验证管理")
@RestController
@RequestMapping(value = "/mktgo/client/wecom")
@Slf4j
public class WeComClientVerifyController extends BaseController {

    @Autowired
    private WeComClientVerifyService weComClientVerifyService;

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "用户登录验证code", nickname = "verifyUserCode", notes = "", response =
            BaseResponse.class)
    @RequestMapping(value = {"/verify"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity verifyUserCode(
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value = "corp_id", required =
                    true) String corpId,
            @ApiParam(value = "企微应用ID", required = true) @RequestParam("agent_id") @NotBlank @Valid String agentId,
            @ApiParam(value = "创建员工任务", required = true) @RequestBody @Valid WeComUserVerifyRequest request) {

        String code = (request != null) ? request.getCode() : "";
        String memberId = (request != null) ? request.getMemberId() : "";
        return ResponseEntity.ok(weComClientVerifyService.userVerify(corpId, agentId, code, memberId));
    }

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = QuerySignatureResponse.class)
    })
    @ApiOperation(value = "用户登录验证code", nickname = "verifySdkConfig", notes = "", response =
            BaseResponse.class)
    @RequestMapping(value = {"/sdk_config"}, produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity verifySdkConfig(
            @ApiParam(value = "企业的企微ID", required = true) @NotNull @Valid @RequestParam(value = "corp_id", required =
                    true) String corpId,
            @ApiParam(value = "企微应用ID", required = true) @RequestParam("agent_id") @NotBlank @Valid String agentId,
            @ApiParam(value = "SDK配置信息", required = true) @RequestBody @Valid WeComVerifySdkConfigRequest request) {

        return ResponseEntity.ok(weComClientVerifyService.sdkConfigVerify(corpId, agentId, request.getType().getValue(),
                request.getUrl()));
    }
}
