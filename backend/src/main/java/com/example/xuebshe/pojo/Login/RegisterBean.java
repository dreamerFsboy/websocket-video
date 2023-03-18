package com.example.xuebshe.pojo.Login;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Data
@ApiModel("通用注册POJO")
public class RegisterBean {
        /** 密码 */
        @ApiModelProperty("密码")
        private String password;
        /** 邮箱  */
        @ApiModelProperty("邮箱")
        private String email;
}
