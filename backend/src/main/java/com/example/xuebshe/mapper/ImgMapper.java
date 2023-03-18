package com.example.xuebshe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.xuebshe.common.picture.SysPicture;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.awt.*;

/**
 * @author reine
 * 2022/7/6 21:34
 */
@Mapper
public interface ImgMapper extends BaseMapper<SysPicture> {
    /**
     * 存储图片信息到数据库
     * @param image 图片信息
     * @return 受影响的行数
     */
    @Insert("INSERT INTO `sys_picture` (path, project, name) VALUES ('${path}', '${project}', '${name}')")
    Integer storeImg(SysPicture image);

    /**
     * 通过文件name获取文件path信息
     * @param project 项目名
     * @param name 文件名
     * @return 文件存储路径
     */
    @Select("SELECT path FROM sys_picture WHERE project = '${project}' and name = '${name}' ")
    String getPath(String project,String name);

    /**
     * 通过文件name删除文件
     * @param project 项目名
     * @param name 文件名
     * @return 受影响的行数
     */
    @Delete("        DELETE" +
            "        FROM 'imgUrl'" +
            "        WHERE project = '${project}'" +
            "          and name = '${name}'")
    Integer deleteImg(String project,String name);





}
