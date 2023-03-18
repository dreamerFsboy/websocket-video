package com.example.xuebshe.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@Api(tags = "验证码部分")
public class VerifyController {
    @ApiOperation(value = "验证码接口",notes = "[无需登录]该接口为管理员和用户共用")
    @SaIgnore
    @GetMapping("captcha.jpg")
    public void captcha(HttpServletResponse response, HttpServletRequest request, @ApiParam("当传入check为admin时,为管理员登录,否则为用户登录.")@RequestParam String check) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(200, 100, 4, 20);
        // 保存到验证码到 session
        if("admin".equals(check))
        {
            request.getSession().setAttribute("KAPTCHA_ADMIN_KEY", captcha.getCode());
        }
        request.getSession().setAttribute("KAPTCHA_SESSION_KEY", captcha.getCode());
        ServletOutputStream out = response.getOutputStream();
        captcha.write(out);
        out.close();
    }
}
