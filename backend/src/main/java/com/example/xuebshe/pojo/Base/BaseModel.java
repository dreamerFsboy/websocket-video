package com.example.xuebshe.pojo.Base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
@Data
@ApiModel("常用数据库处理")
public class BaseModel {
    // 数据库自增id
    @ApiModelProperty("主键ID")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    @ApiModelProperty("创建者")
    private String createBy;
    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("最后一次更新者")
    private String lastUpdateBy;
    @ApiModelProperty("最后一次更新时间")
    private Date lastUpdateTime;
}