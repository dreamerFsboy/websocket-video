package com.example.xuebshe.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.xuebshe.common.result.CommonResult;
import com.example.xuebshe.pojo.Sys.SysUser;
import com.example.xuebshe.service.FileService;
import com.example.xuebshe.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 图片存储控制器
 *
 * @author reine
 * @since 2022/4/30 8:41
 */
@RestController
@RequestMapping("/picture")
@SaCheckLogin
public class FileController {

    @Resource
    private FileService fileService;
    @Resource
    private UserService userService;

    /**
     * 图片上传
     *
     * @param project 项目名称
     * @param imgFile 图片文件
     * @param filename 文件名
     * @return 成功或失败信息
     */
    @ApiOperation(value = "上传文件",notes = "传入项目名称(随便写),图片文件和文件名,上传文件")
    @PostMapping("/{project}")
    public CommonResult storeImage(@PathVariable String project,
                                   @RequestPart("imgFile")MultipartFile imgFile,
                                   @RequestParam(value = "filename", required = false) String filename) {
        Map<String, String> resultMap;
        try {
            File file = transferToFile(imgFile);
            if (filename == null || "".equals(filename)) {
                filename = imgFile.getOriginalFilename();
            }
            resultMap = fileService.storeImageAPI(project, file, filename);
        } catch (Exception e) {
            return CommonResult.error("上传失败");
        }
        return CommonResult.success("上传成功", resultMap);
    }


    @ApiOperation(value = "上传头像",notes = "传入项目名称(随便写),图片文件和文件名,上传文件")
    @PostMapping("/avator/upload")
    public CommonResult uploadAvatar(@RequestPart("imgFile")MultipartFile imgFile,
                                   @RequestParam(value = "filename", required = false) String filename) {
        String project = "avatar";
        Map<String, String> resultMap;
        //
        String loginjson = (String) StpUtil.getLoginIdDefaultNull();
        SysUser user = new SysUser();
        if(loginjson!=null)
        {
            CommonResult token = JSON.toJavaObject((JSON)JSON.parse((String)loginjson),CommonResult.class);
            user= JSONObject.toJavaObject((JSONObject)token.getData(), SysUser.class);
        }

        try {
            File file = transferToFile(imgFile);
            if (filename == null || "".equals(filename)) {
                filename = imgFile.getOriginalFilename();
            }
            resultMap = fileService.storeImageAPI(project, file, filename);
        } catch (Exception e) {
            return CommonResult.error("上传失败");
        }
        //上传成功
        String avatorurl = "picture/" + project + "/" + filename;
        user.setPicture(avatorurl);
        SysUser myuser = userService.findByEmail(user.getEmail());
        myuser.setPicture(avatorurl);
        userService.save(myuser);
        return CommonResult.success("上传成功", resultMap);
    }





    /**
     * 图片读取
     *
     * @param project 项目名称
     * @param imgName 图片名称
     * @return 成功或失败信息
     */
    @SaIgnore
    @ApiOperation(value = "读取图片",notes = "传入项目名称(随便写)和图片名称,读取图片")
    @GetMapping("/{project}/{imgName}")
    public CommonResult showImage(@PathVariable String project, @PathVariable String imgName, HttpServletResponse response) {
        boolean flag = fileService.showImage(project, imgName, response);
        if (flag) {
            response.setHeader("Cache-Control", "no-store, no-cache");
            response.setContentType("image/jpeg");
            return CommonResult.success("图片展示成功");
        } else {
            return CommonResult.error("图片展示失败");
        }
    }

    /**
     * 删除图片
     *
     * @param project 项目名称
     * @param imgName 图片名称
     * @return 成功或失败信息
     */
    @SaCheckRole("admin")
    @ApiOperation(value = "删除图片",notes = "传入已有的项目名称和图片名称,删除图片")
    @DeleteMapping("/{project}/{imgName}")
    public CommonResult deleteImage(@PathVariable String project, @PathVariable String imgName) {
        boolean flag = fileService.deleteImage(project, imgName);
        if (flag) {
            return CommonResult.success("图片删除成功");
        } else {
            return CommonResult.error("图片删除失败");
        }
    }

    /**
     * multipartFile转file
     *
     * @param multipartFile 文件
     * @return 文件
     */
    private File transferToFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        File file = null;
        if (originalFilename != null) {
            String[] filename = originalFilename.split("\\.");
            file = File.createTempFile(filename[0] + "caonimadeshabi", "." + filename[1]);
            multipartFile.transferTo(file);
            file.deleteOnExit();
        }
        return file;
    }

}
