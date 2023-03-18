package com.example.xuebshe.pojo.Sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("修改密码通用Pojo")
public class SysCheckPasswd {
    @ApiModelProperty("邮箱内验证码")
    String verify;
    @ApiModelProperty("新密码")
    String newpass;
}
