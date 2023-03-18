package com.example.xuebshe.pojo.Login;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("登录通用Bean")
public class LoginBean {
    /** 用户名是Email */
    @ApiModelProperty("邮箱")
    private String email;
    /** 密码 */
    @ApiModelProperty("密码")
    private String password;
    /** 验证码 */
    @ApiModelProperty("验证码")
    private String captcha;

    @Override
    public String toString()
    {
        //返回JSON的属性
        return JSON.toJSONString(this);
    }

}
