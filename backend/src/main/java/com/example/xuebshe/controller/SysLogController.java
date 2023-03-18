package com.example.xuebshe.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.example.xuebshe.common.page.PageRequest;
import com.example.xuebshe.common.result.CommonResult;
import com.example.xuebshe.service.SysLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("log")
@SaCheckLogin
@Api(tags = "日志查看部分")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;
    @ApiOperation(value = "查看日志信息",notes = "[管理员]分页查询日志信息")
    @SaCheckRole("admin")
    @PostMapping(value="/findPage")
    public CommonResult findPage(@RequestBody PageRequest pageRequest) {
        return CommonResult.success(sysLogService.findPage(pageRequest));
    }
}
