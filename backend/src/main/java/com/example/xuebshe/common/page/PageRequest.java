package com.example.xuebshe.common.page;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
@Data
@ApiModel("分页参数")
public class PageRequest {
    /**
     * 当前页码
     */
    @ApiModelProperty("当前页码")
    private int pageNum = 1;
    /**
     * 每页数量
     */
    @ApiModelProperty("每页数量")
    private int pageSize = 10;
    @ApiModelProperty("查询筛选参数")
    private Map<String, ColumnFilter> columnFilters = new HashMap<String, ColumnFilter>();
}
