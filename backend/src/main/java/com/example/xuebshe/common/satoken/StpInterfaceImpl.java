package com.example.xuebshe.common.satoken;

import cn.dev33.satoken.stp.StpInterface;
import com.alibaba.fastjson.JSON;
import com.example.xuebshe.common.result.CommonResult;
import com.example.xuebshe.pojo.Login.LoginBean;
import com.example.xuebshe.pojo.Sys.SysUser;
import com.example.xuebshe.service.AdminService;
import com.example.xuebshe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限认证接口扩展，Sa-Token 将从此实现类获取每个账号拥有的权限码
 *
 * @author kong
 * @since 2022-10-13
 */
@Component	// 打开此注解，保证此类被springboot扫描，即可完成sa-token的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {
    @Autowired
    UserService userService;
    @Autowired
    AdminService adminService;
    //权限:user admin




    /**
     * 由于条件简单,无需使用鉴权,只需要身份鉴权识别
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return new ArrayList<>();
    }

    /**
     * 返回一个账号所拥有的角色标识,登录鉴权已经添加了,直接解析即可
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        CommonResult loginBean = JSON.toJavaObject((JSON)JSON.parse((String)loginId),CommonResult.class);
        List<String> list = new ArrayList<>();
        String role = loginBean.getMsg();
        list.add(role);
        return list;
    }
}
