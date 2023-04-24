package com.easy.marketgo.gateway.controller;

import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.react.service.client.WeComClientVerifyService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/21/22 3:01 PM
 * Describe:
 */
@Api(value = "客户端验证管理", tags = "客户端验证管理")
@RestController
@RequestMapping(value = "/")
@Slf4j
public class WeComClientFileVerifyController extends BaseController {

    @Autowired
    private WeComClientVerifyService weComClientVerifyService;

    @ApiResponses({
            @ApiResponse(code = 0, message = "ok", response = BaseResponse.class)
    })
    @ApiOperation(value = "企业微信可信域名校验", nickname = "checkCredFile", notes = "", response =
            BaseResponse.class)
    @RequestMapping(value = {"{file_name}"}, produces = {"application/json"}, method =
            RequestMethod.GET)
    public ResponseEntity checkCredFile(
            @ApiParam(value = "可信文件名", required = true) @PathVariable("file_name") String fileName,
            HttpServletResponse httpServletResponse) {
        log.info("weCom file verify. fileName={}", fileName);

        return ResponseEntity.ok(weComClientVerifyService.checkCredFile(fileName, httpServletResponse));
    }
}
