package com.example.xuebshe.service.impl;

import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.mail.MailUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.xuebshe.common.page.ColumnFilter;
import com.example.xuebshe.common.page.PageRequest;
import com.example.xuebshe.common.page.PageResult;
import com.example.xuebshe.mapper.UserMapper;
import com.example.xuebshe.pojo.Sys.SysUser;
import com.example.xuebshe.service.UserService;
import com.example.xuebshe.util.DESUtil;
import com.example.xuebshe.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, SysUser> implements UserService {


    @Override
    public boolean save(SysUser record) {
        Long id = null;
        if(record.getId() == null || record.getId() == 0) {
            // 新增用户
            super.save(record);
            id = record.getId();
        } else {
            // 更新用户信息
            updateById(record);
        }
        return true;
    }

    @Override
    public PageResult findPage(PageRequest pageRequest) {
        PageResult pageResult = null;
        String name = getColumnFilterValue(pageRequest, "name");
        String email = getColumnFilterValue(pageRequest, "email");
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        if(!StrUtil.isEmpty(name)) {
            queryWrapper.eq(SysUser::getEmail, name);
            if(!StrUtil.isEmpty(email)) {
                queryWrapper.eq(SysUser::getEmail, email);
            }
        }
        IPage<SysUser> result = baseMapper.selectPage(page, queryWrapper);
        pageResult = new PageResult(result);
        return pageResult;
    }

    @Override
    public SysUser findById(Long userId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getId, userId);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public SysUser findByEmail(String userEmail) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getEmail, userEmail);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public void updateLoginTime(SysUser user) {
        user.setLastUpdateTime(new Date());
        updateById(user);
    }

    /**
     * 获取过滤字段的值
     * @param filterName
     * @return
     */
    public String getColumnFilterValue(PageRequest pageRequest, String filterName) {
        String value = null;
        ColumnFilter columnFilter = pageRequest.getColumnFilters().get(filterName);
        if(columnFilter != null) {
            value = columnFilter.getValue();
        }
        return value;
    }

    @Override
    public void createUser(SysUser user) {
        user.setCreateTime(new Date());
        PasswordUtil.encryptPassword(user);
        save(user);
    }




    @Override
    public int delete(List<SysUser> users) {
        for (SysUser user : users) {
            removeById(user.getId());
        }
        return 1;
    }

    @Override
    public boolean beginPasswdReset(String useremail, String newpass) throws Exception {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getEmail, useremail);
        SysUser userBean = baseMapper.selectOne(wrapper);
        if(userBean == null)
        {
            return false;
        }
        //二次加密密码
        userBean.setPassword(newpass);
        PasswordUtil.encryptPassword(userBean);
        this.save(userBean);
        return true;
    }


    public boolean sendEmail(String userEmail,String content,String why) throws Exception {
        //获取当前时间并转换
        Date now = new Date();
        String currentTime = "" + now.getTime();
        //拼凑成找回密码的链接
        String plainText = currentTime + "$" + userEmail; // 当前时间加上用户邮箱 使用$进行连接二者，在大多数情况下不会产生歧义
        String desKey = "CONSTRUCTIONCOMPLETE"; // des算法中的密钥
        String link = DESUtil.encrypt(desKey, plainText);//加密后的链接
        //调用发送请求进行发送
        try
        {
            MailUtil.send(userEmail,why,content + "验证码为:" + link,true);
        }
        catch(Exception e)
        {
            return false;
        }
        return true;
    }



    @Override
    public boolean sendPasswdReset(String userEmail) throws Exception {
        return sendEmail(userEmail,"您好,请尽快填写本次验证的验证码.","找回密码");

    }

    @Override
    public boolean sendActivate(String userEmail) throws Exception {
        return sendEmail(userEmail,"您好,请将激活码复制到激活码栏目以激活.","激活");

    }

    @Override
    public String checkCodeValid(String code)
    {
        //获取传入参数
        long checktime = 7200000;
        //验证验证码是否过期（设置一个开关让它可以100%的失败）
        try
        {
            Date now = new Date();
            String p = DESUtil.decrypt("CONSTRUCTIONCOMPLETE", code);
            System.out.println("-----------解密后的key参数---------------------");
            System.out.println(p);
            String[] checklist = p.split("\\$");
            long time = Long.parseLong(checklist[0]);
            if(now.getTime() - time > checktime)
            {
                //时间超出推算
                System.out.println("已经过期");
                return null;
            }
            return checklist[1];
        }
        catch (Exception e){
            return null;
        }
    }




}
