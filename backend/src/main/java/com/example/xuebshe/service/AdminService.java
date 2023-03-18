package com.example.xuebshe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.xuebshe.pojo.Sys.SysAdmin;
import com.example.xuebshe.pojo.Sys.SysUser;

public interface AdminService extends IService<SysAdmin> {
    SysAdmin findByEmail(String userEmail);

    void updateLoginTime(SysAdmin user);
}
