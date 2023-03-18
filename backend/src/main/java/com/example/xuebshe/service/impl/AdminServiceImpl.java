package com.example.xuebshe.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.xuebshe.mapper.SysAdminMapper;
import com.example.xuebshe.pojo.Sys.SysAdmin;
import com.example.xuebshe.service.AdminService;
import org.springframework.stereotype.Service;

import java.util.Date;
@Service
public class AdminServiceImpl extends ServiceImpl<SysAdminMapper, SysAdmin> implements AdminService {

    @Override
    public SysAdmin findByEmail(String userEmail) {
        LambdaQueryWrapper<SysAdmin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysAdmin::getEmail, userEmail);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public void updateLoginTime(SysAdmin user) {
        user.setLastUpdateTime(new Date());
        updateById(user);
    }
}
