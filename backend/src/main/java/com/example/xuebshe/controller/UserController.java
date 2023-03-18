package com.example.xuebshe.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.Validator;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.xuebshe.aspect.Log;
import com.example.xuebshe.common.page.PageRequest;
import com.example.xuebshe.common.result.CommonResult;
import com.example.xuebshe.pojo.Sys.SysCheckPasswd;
import com.example.xuebshe.pojo.Sys.SysUser;
import com.example.xuebshe.service.UserService;
import com.example.xuebshe.util.PasswordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@Api(tags = "用户控制部分")
@RestController
@RequestMapping("user")
@SaCheckLogin
public class UserController {

    @Resource
    private UserService userService;

    public SysUser getSysUser() {
        String loginjson = (String) StpUtil.getLoginIdDefaultNull();
        CommonResult token = JSON.toJavaObject((JSON) JSON.parse((String) loginjson), CommonResult.class);
        JSONObject object = (JSONObject) token.getData();
        return userService.findByEmail((String) object.get("email"));
    }


    @ApiOperation(value = "用户获取个人信息", notes = "[用户端]自动使用token验证,无需传参,若报错,则代表token过期,需要重新登录")
    @Log("用户获取个人信息")
    @PostMapping("getMyInformation")
    public CommonResult getUserInformation() {
        String loginjson = (String) StpUtil.getLoginIdDefaultNull();
        CommonResult token = JSON.toJavaObject((JSON) JSON.parse((String) loginjson), CommonResult.class);
        JSONObject object = (JSONObject) token.getData();
        SysUser user = userService.findByEmail((String) object.get("email"));
        //隐藏无用信息
        user.setPassword(null);
        user.setSalt(null);
        return CommonResult.success(user);
    }

    @ApiOperation(value = "用户设置昵称", notes = "传入昵称设置")
    @PostMapping("SetNick")
    public CommonResult setNick(String nickname) {
        SysUser user = getSysUser();
        user.setNick(nickname);
        userService.save(user);
        return CommonResult.success("修改昵称成功");
    }


//    @SaIgnore
//    @ApiOperation(value = "用户重发激活邮件",notes = "[无需登录]当用户错误预估时间导致注册失败,提供用户重发接口.当检测到用户激活时,不会发送信息.")
//    @Log("重发激活邮件")
//    @PostMapping("/reactivate")
//    public CommonResult reactivate(String email) throws Exception {
//        if (userService.findByEmail(email).getStatus() != (byte) 0) {
//            return CommonResult.error("用户已激活,请不要重复激活!");
//        }
//        userService.sendActivate(email);
//        return CommonResult.success("重发激活邮件成功");
//    }

    @SaIgnore
    @ApiOperation(value = "用户激活", notes = "[无需登录]用户使用Email内的验证码进行激活.")
    @Log("激活用户功能")
    @GetMapping(value = "/activate")
    public CommonResult activate(@ApiParam("用户邮箱接受的验证码") @RequestParam String token) {
        String email = userService.checkCodeValid(token);
        if (email != null) {
            //激活成功
            SysUser user = userService.findByEmail(email);
            user.setStatus((byte) 1);
            userService.save(user);
            return CommonResult.success("用户已激活");
        }
        return CommonResult.error("用户激活失败,token已过期,请重新完成激活");
    }

    @SaIgnore
    @ApiOperation(value = "忘记密码验证码请求", notes = "忘记密码时请求验证码修改密码.")
    @Log("请求忘记密码的验证码")
    @RequestMapping(value = "/resetPasswd", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult resetPasswd(@ApiParam("用户邮箱") @RequestBody String email) throws Exception {
        if (Validator.isEmail(email)) {
            if (userService.sendPasswdReset(email)) {
                //发送邮件成功，给前端200
                return CommonResult.success("成功");
            }
            return CommonResult.error("发送失败请联系管理员！");
        }
        return CommonResult.error("邮箱不正确！");
    }

    @SaIgnore
    @ApiOperation(value = "用户修改密码接口", notes = "用户修改密码为指定的密码.传入新密码以及验证码进行修改,不需要传入邮箱")
    @Log("修改密码")
    @RequestMapping(value = "/modifyPasswd", method = RequestMethod.POST)
    public CommonResult modifyPasswd(@RequestBody SysCheckPasswd checkPasswd) {
        String verifycode = checkPasswd.getVerify();
        String email = userService.checkCodeValid(verifycode);
        if (email != null) {
            try {
                //加密工作已经在后台做了
                userService.beginPasswdReset(email, checkPasswd.getNewpass());
                return CommonResult.success("修改密码成功，请使用新密码登录");
            } catch (Exception e) {
                CommonResult.error("错误，请检查后台");
                e.printStackTrace();
            }
        }
        return CommonResult.error("验证码错误或过期！");
    }
}
