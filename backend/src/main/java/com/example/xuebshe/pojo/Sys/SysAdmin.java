package com.example.xuebshe.pojo.Sys;


import com.example.xuebshe.pojo.Base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("管理员类")
public class SysAdmin extends BaseModel {
    @ApiModelProperty("管理员邮箱")
    private String email;
    @ApiModelProperty("管理员密码")
    private String password;
    @ApiModelProperty("管理员盐")
    private String salt;
    @ApiModelProperty("管理员是否激活")
    private Byte status;
    @ApiModelProperty("管理员是否被删除(未实装)")
    private Byte delFlag;
}
