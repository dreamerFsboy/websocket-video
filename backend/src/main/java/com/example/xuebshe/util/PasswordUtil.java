package com.example.xuebshe.util;

import cn.dev33.satoken.secure.SaSecureUtil;
import com.example.xuebshe.pojo.Sys.SysUser;

/**
 * @Author 10319
 * @Date 2019/6/13 22:10
 * @Version 1.0
 */
public class PasswordUtil {

    public static String twicePassword(String password, String salt) {
        return SaSecureUtil.md5(SaSecureUtil.md5(password + salt));
    }

    /**
     * 使用盐加密密码
     * @param user
     */
    public static void encryptPassword(SysUser user){
        // 设置盐
        user.setSalt("1a2b3c4d");
        //将用户的注册密码经过散列算法替换成一个不可逆的新密码保存进数据，使用过程使用了盐
        String newPassword = twicePassword(user.getPassword(),user.getSalt());
        //String newPassword = new SimpleHash(algorithmName, user.getPassword(), salt, hashIterations).toString();
        user.setPassword(newPassword);
    }

    public static String encryptPassword(String password, String salt){
        //将用户的注册密码经过散列算法替换成一个不可逆的新密码保存进数据，使用过程使用了盐
        String newPassword =twicePassword(password,salt);
        return newPassword;
    }

}
