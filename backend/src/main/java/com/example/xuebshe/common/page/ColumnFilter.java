package com.example.xuebshe.common.page;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("查询参数类")
public class ColumnFilter {
    /**
     * 过滤列名
     */
    @ApiModelProperty("过滤列名")
    private String name;
    /**
     * 查询的值
     */
    @ApiModelProperty("将要查询的值")
    private String value;
}
