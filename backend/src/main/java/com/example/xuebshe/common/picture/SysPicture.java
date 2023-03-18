package com.example.xuebshe.common.picture;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author reine
 * 2022/7/6 21:40
 */
@Data
public class SysPicture {
    /**
     * 主键id
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    /**
     * 文件路径
     */
    private String path;
    /**
     * 项目名
     */
    private String project;
    /**
     * 文件名
     */
    private String name;
}
