package com.example.xuebshe.common.satoken;

import com.example.xuebshe.common.result.CommonResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 全局异常拦截
    @ExceptionHandler
    public CommonResult handlerException(Exception e) {
        e.printStackTrace();
        return CommonResult.error(e.getMessage());
    }
}
