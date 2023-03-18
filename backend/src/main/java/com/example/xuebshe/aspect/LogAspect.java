package com.example.xuebshe.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.xuebshe.common.result.CommonResult;
import com.example.xuebshe.pojo.Login.LoginBean;
import com.example.xuebshe.pojo.Sys.SysAdmin;
import com.example.xuebshe.pojo.Sys.SysLog;
import com.example.xuebshe.pojo.Sys.SysUser;
import com.example.xuebshe.service.SysLogService;
import com.example.xuebshe.util.IPUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

@Slf4j
@Aspect
@Component
public class LogAspect {
    @Autowired
    private SysLogService logService;

    @Pointcut("@annotation(com.example.xuebshe.aspect.Log)")
    public void pointcut() {
        // do nothing
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result = null;
        long beginTime = System.currentTimeMillis();
        // 获取request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = IPUtils.getIpAddr(request);
        // 执行方法
        result = point.proceed();
        // 执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        LoginBean loginBean = new LoginBean();
        loginBean.setEmail("未登录用户");
        // 使用这个获取LoginBean是可行的
        String loginjson = (String)StpUtil.getLoginIdDefaultNull();
        if(loginjson!=null)
        {
            CommonResult token = JSON.toJavaObject((JSON)JSON.parse((String)loginjson),CommonResult.class);
            loginBean.setEmail(((JSONObject)token.getData()).get("email").toString());
        }
        String username = loginBean.getEmail();
        SysLog sysLog = new SysLog();
        sysLog.setUserEmail(username);
        sysLog.setCreateBy(username);
        sysLog.setCreateTime(new Date());
        sysLog.setLastUpdateBy(username);
        sysLog.setLastUpdateTime(new Date());
        sysLog.setIp(ip);
        sysLog.setTime(time);
        String className = point.getTarget().getClass().getName();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);
        if (logAnnotation != null) {
            // 注解上的描述
            sysLog.setOperation(logAnnotation.value());
        }
        String methodName = signature.getName();
        sysLog.setMethod(className + "." + methodName + "()");
        // 请求的方法参数值
        Object[] args = point.getArgs();
        try{
            String params = JSONObject.toJSONString(args[0]);
            if(params.length() > 200) {
                params = params.substring(0, 200) + "...";
            }
            sysLog.setParams(params);
        } catch (Exception e){
        }
        logService.save(sysLog);
        return result;
    }
}
