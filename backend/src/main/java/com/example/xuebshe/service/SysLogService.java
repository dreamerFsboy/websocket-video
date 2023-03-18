package com.example.xuebshe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.xuebshe.common.page.PageRequest;
import com.example.xuebshe.common.page.PageResult;
import com.example.xuebshe.pojo.Sys.SysLog;

public interface SysLogService extends IService<SysLog> {

    PageResult findPage(PageRequest pageRequest);
}
