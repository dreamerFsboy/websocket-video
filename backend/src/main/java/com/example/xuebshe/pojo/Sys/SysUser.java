package com.example.xuebshe.pojo.Sys;


import com.example.xuebshe.pojo.Base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ApiModel("用户类")
@ToString
public class SysUser extends BaseModel {

@ApiModelProperty("用户邮箱")
    private String email;
    @ApiModelProperty("用户密码")
    private String password;
    @ApiModelProperty("用户盐")
    private String salt;
    @ApiModelProperty("用户是否激活")
    private Byte status;
    @ApiModelProperty("用户是否被删除(未实装)")
    private Byte delFlag;
    @ApiModelProperty("用户图片URL")
    private String picture;
    @ApiModelProperty("用户昵称")
    private String nick;
}
