package com.example.xuebshe.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.example.xuebshe.aspect.Log;
import com.example.xuebshe.common.result.CommonResult;
import com.example.xuebshe.pojo.Login.LoginBean;
import com.example.xuebshe.pojo.Login.RegisterBean;
import com.example.xuebshe.pojo.Sys.SysUser;
import com.example.xuebshe.service.UserService;
import com.example.xuebshe.util.PasswordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@Slf4j
@SaCheckLogin
@Api(tags = "用户登录部分")
public class LoginController {
    @Resource
    private UserService userService;

    @ApiOperation(value = "用户注册",notes = "用户注册接口,默认用户注册后未激活,请手动引导用户访问登录或激活页面")
    @SaIgnore
    @Log("用户注册")
    @RequestMapping(value = "/regist", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult regist (@RequestBody RegisterBean registerBean
    ) {
        String psw = registerBean.getPassword();
        String email = registerBean.getEmail();
        //密码若非空
        if(email==null)
        {
            return CommonResult.error("没有邮箱无法注册！");
        }
        if(!Validator.isEmail(email))
        {
            return CommonResult.error("邮箱格式不正确！");
        }
        if(psw!= null)
        {
            //根据名称确定是否可注册
                SysUser suser =userService.findByEmail(email);
                if(suser != null) {
                    if (suser.getStatus() != (byte) 0) {
                        return CommonResult.error("用户已激活,请不要重复激活!");
                    }
                    try {
                        userService.sendActivate(email);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return CommonResult.success("重发激活邮件成功");
                }
                //否则加密密码
                SysUser user = new SysUser();
                user.setEmail(email);
                user.setPassword(psw);
                //默认设置没有激活
                user.setStatus((byte) 0);
                PasswordUtil.encryptPassword(user);
                user.setCreateTime(new Date());
                try {
                    userService.sendActivate(email);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //返回成功
                return CommonResult.success(userService.save(user));
        }
        else
        {
            return CommonResult.error("密码为空");
        }
    }
    @ApiOperation(value = "用户登录",notes = "用户登录,未激活的用户会提示尚未激活,可以用于判定.会自动设置token,但也会返回token.")
    @SaIgnore
    @Log("用户登录")
    @PostMapping(value = "/user/login")
    @ResponseBody
    public CommonResult userlogin(@RequestBody LoginBean loginBean, HttpServletRequest request) {
        String username = loginBean.getEmail();
        String password = loginBean.getPassword();
        String captcha = loginBean.getCaptcha();
        Object kaptcha = request.getSession().getAttribute("KAPTCHA_SESSION_KEY");

        if(kaptcha == null){
            return CommonResult.error("验证码已失效");
        }
        if(!captcha.equals(kaptcha)){
            return CommonResult.error("验证码不正确");
        }
        //搜索用户
        SysUser user = userService.findByEmail(username);
        //用户为空不存在
        if (user == null) {
            return CommonResult.error("用户名不存在");
        }
        if(user.getStatus().equals((byte)0))
        {
            return CommonResult.error("尚未激活!");
        }
        //加盐
        String passwdWithSalt = PasswordUtil.encryptPassword(password, user.getSalt());
        // 这将会用于分析日志等取值
        CommonResult token = CommonResult.success(user);
        token.setMsg("user");
        //使用框架提供的手段登录
        StpUtil.login(token);
        //输出密码
        System.out.println("当前密码是" + user.getPassword() +"分割"+ passwdWithSalt);
        if (!StrUtil.equals(user.getPassword(), passwdWithSalt)) {
            return CommonResult.error("密码错误");
        }
        //更新登录时间
        userService.updateLoginTime(user);
        //设置登录token
        Map<String, Object> map = new HashMap<>();
        //推送token验证，以兼容前端的获取方案
        map.put("token", StpUtil.getTokenValue());
        //成功登录
        return CommonResult.success("登录成功", map);
    }

    @ApiOperation(value = "用户登出",notes = "登出后登录态失效.")
    @Log("登出")
    @GetMapping("/logout")
    public CommonResult logout() {
        StpUtil.logout();
        return CommonResult.success("登出成功");
    }
}
