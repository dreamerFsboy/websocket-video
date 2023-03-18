package com.example.xuebshe.common.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.WebAppRootListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@Configuration

@ComponentScan

@EnableAutoConfiguration
public class WebSocketConfig implements ServletContextInitializer {//配置websocket传输大小，50M

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        System.out.println("尝试解禁中出");
        servletContext.addListener(WebAppRootListener.class);
        servletContext.setInitParameter("org.apache.tomcat.websocket.textBufferSize","92428800");
        servletContext.setInitParameter("org.apache.tomcat.websocket.binaryBufferSize","92428800");

    }

}
