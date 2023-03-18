package com.example.xuebshe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.xuebshe.pojo.Sys.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserMapper extends BaseMapper<SysUser> {
}
