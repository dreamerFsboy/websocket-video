package com.example.xuebshe.pojo.Sys;


import com.example.xuebshe.pojo.Base.BaseModel;
import lombok.Data;

@Data
public class SysLog extends BaseModel {

    private String userEmail;

    private String operation;

    private String method;

    private String params;

    private Long time;

    private String ip;
}
