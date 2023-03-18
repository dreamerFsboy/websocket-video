package com.example.xuebshe.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.example.xuebshe.aspect.Log;
import com.example.xuebshe.common.page.PageRequest;
import com.example.xuebshe.common.result.CommonResult;
import com.example.xuebshe.pojo.Login.LoginBean;
import com.example.xuebshe.pojo.Sys.SysAdmin;
import com.example.xuebshe.pojo.Sys.SysUser;
import com.example.xuebshe.service.AdminService;
import com.example.xuebshe.service.UserService;
import com.example.xuebshe.util.PasswordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Api(tags = "管理员部分")
@SaCheckLogin
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Resource
    public AdminService adminService;
    @Resource
    public UserService userService;

    @ApiOperation(value="管理员登录接口",notes="该接口可自动设置Cookie,设置Cookie名为satoken,它也会返回token值")
    @SaIgnore
    @Log("管理员登录")
    @PostMapping("login")
    public CommonResult login(@RequestBody LoginBean loginBean, HttpServletRequest request) {
        String username = loginBean.getEmail();
        String password = loginBean.getPassword();
        String captcha = loginBean.getCaptcha();
        Object kaptcha = request.getSession().getAttribute("KAPTCHA_ADMIN_KEY");
        if(kaptcha == null){
            return CommonResult.error("验证码已失效");
        }
        if(!captcha.equals(kaptcha)){
            return CommonResult.error("验证码不正确");
        }
        //搜索用户
        SysAdmin user = adminService.findByEmail(loginBean.getEmail());
        //用户为空不存在
        if (user == null) {
            return CommonResult.error("登录管理员失败");
        }
        //加盐
        String passwdWithSalt = PasswordUtil.encryptPassword(password, user.getSalt());
        System.out.println(passwdWithSalt);
        System.out.println(user.getPassword());
        // 这将会用于分析日志等取值
        CommonResult token = CommonResult.success(user);
        token.setMsg("admin");
        //使用框架提供的手段登录
        StpUtil.login(token);
        //输出密码
        System.out.println("当前密码是" + user.getPassword() +"分割"+ passwdWithSalt);
        if (!StrUtil.equals(user.getPassword(), passwdWithSalt)) {
            return CommonResult.error("密码错误");
        }
        //更新登录时间
        adminService.updateLoginTime(user);
        //设置登录token
        Map<String, Object> map = new HashMap<>();
        //推送token验证，以兼容前端的获取方案
        map.put("token", StpUtil.getTokenValue());
        //成功登录
        return CommonResult.success("登录成功", map);
    }
    @ApiOperation(value="新增或修改用户密码接口",notes="[需要管理员权限]传入用户User的JSON,其根据主键ID查询,若传入的用户密码不为空且不存在时将会自动新增,否则修改密码")
    @SaCheckRole("admin")
    @Log("新增/修改用户")
    @PostMapping(value = "/save")
    public CommonResult save(@RequestBody SysUser record) {
        SysUser user = userService.findById(record.getId());
        //如果密码是空的
        if (record.getPassword() != null) {
            if (user == null) {
                // 新增用户
                if (userService.findByEmail(record.getEmail()) != null) {
                    return CommonResult.error("用户名已存在!");
                }
                PasswordUtil.encryptPassword(record);
            } else {
                // 修改用户, 且修改了密码
                if (!record.getPassword().equals(user.getPassword())) {
                    PasswordUtil.encryptPassword(record);
                }
            }
        }
        //保存
        return CommonResult.success(userService.save(record));
    }
    @ApiOperation(value = "查看用户列表接口",notes = "[管理员]分页查询用户信息")
    @SaCheckRole("admin")
    @Log("查看用户列表")
    @PostMapping(value = "/findPage")
    public CommonResult findPage(@RequestBody PageRequest pageRequest) {
        return CommonResult.success(userService.findPage(pageRequest));
    }
    @ApiOperation(value = "删除用户接口",notes = "[管理员]传入多个用户JSON完成删除功能")
    @SaCheckRole("admin")
    @Log("删除用户")
    @PostMapping(value = "/delete")
    public CommonResult delete(@RequestBody List<SysUser> records) {
        for (SysUser record : records) {
            SysUser sysUser = userService.findById(record.getId());
        }
        return CommonResult.success(userService.delete(records));
    }
    @ApiOperation(value = "查询用户详细信息",notes = "通过邮箱查询用户的详细信息")
    @Log("按名称查询用户详细信息")
    @GetMapping(value = "/findByName")
    public CommonResult findByUserName(@RequestParam String email) {
        return CommonResult.success(userService.findByEmail(email));
    }

    @ApiOperation(value = "用户登出",notes = "登出后登录态失效.")
    @Log("登出")
    @GetMapping("/logout")
    public CommonResult logout() {
        StpUtil.logout();
        return CommonResult.success("登出成功");
    }


}
