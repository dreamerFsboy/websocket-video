package com.example.xuebshe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.util.WebAppRootListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@SpringBootApplication
public class XueBsheApplication {

    public static void main(String[] args) {
        SpringApplication.run(XueBsheApplication.class, args);
    }

}
