package com.example.xuebshe.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.xuebshe.common.picture.SysPicture;
import com.example.xuebshe.mapper.ImgMapper;
import com.example.xuebshe.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author reine
 * 2022/6/30 9:24
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class FileServiceImpl implements FileService {

    @Value("${local.store}")
    private String localStore;

    @Resource
    private ImgMapper imgMapper;

    @Override
    public Map<String, String> storeImageGUI(String path, String project, File imgFile) throws Exception {
        String fileName = imgFile.getName();
        String storePath = path + "\\" + project;
        // 拼接文件路径
        File file = createFile(project, fileName, storePath);
        int i = imgFile.getPath().indexOf(":");
        String realpath = imgFile.getPath().substring(i + 2);
        return copyFileAndGetUrl(project, fileName, file, realpath);
    }

    @Override
    public Map<String, String> storeImageAPI(String project, File imgFile, String fileName) throws Exception {
        String storePath = localStore + project;
        try{
            File file = createFile(project, fileName, storePath);
            String realpath = imgFile.getAbsolutePath();
            return copyFileAndGetUrl(project, fileName, file, realpath);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, String> copyFileAndGetUrl(String project, String fileName, File file, String realpath) throws Exception {
        // 数据缓冲区
        byte[] bs = new byte[1024];
        // 读取到的数据长度
        int len;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = Files.newInputStream(Paths.get(realpath));
            outputStream = new FileOutputStream(file);
            while ((len = inputStream.read(bs)) != -1) {
                outputStream.write(bs, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("写入文件失败");
        } finally {
            closeStream(inputStream, outputStream);
        }
        Map<String, String> resultMap = new HashMap<>(2);
        resultMap.put("project", project);
        resultMap.put("filename", fileName);
        return resultMap;
    }

    private File createFile(String project, String fileName, String storePath) throws Exception {
        File dir = new File(storePath);
        String filePath = storePath + "\\" + fileName;
        File file = new File(filePath);
        if (uploadImage(file, project, fileName)) {
            throw new Exception("插入数据库失败");
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return file;
    }

    @Override
    public boolean showImage(String project, String imgName, HttpServletResponse response) {
        String filePath = getPath(project, imgName);
        FileInputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            int i = inputStream.available();
            //byte数组用于存放图片字节数据
            byte[] buffer = new byte[i];
            inputStream.read(buffer);
            outputStream = response.getOutputStream();
            outputStream.write(buffer);
        } catch (IOException | NullPointerException e) {
            log.error("文件不存在");
            return false;
        } finally {
            closeStream(inputStream, outputStream);
        }
        return true;
    }

    @Override
    public boolean deleteImage(String project, String imgName) {
        String filePath = getPath(project, imgName);
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            file.delete();
            return deleteImageInfo(project, imgName);
        }
    }


    /**
     * 关闭流
     *
     * @param inputStream  输入流
     * @param outputStream 输出流
     */
    private void closeStream(InputStream inputStream, OutputStream outputStream) {
        Optional.ofNullable(outputStream).ifPresent(fileOutputStream -> {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Optional.ofNullable(inputStream).ifPresent(fileInputStream -> {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 插入图片路径名称匹配数据到数据库
     *
     * @param file    生成的文件
     * @param project 项目名
     * @param name    文件名
     * @return 成功标志
     */
    private boolean uploadImage(File file, String project, String name) throws Exception {
        Integer integer;
        try {
            // 如果存在则先删除
            String path = imgMapper.getPath(project, name);
            if (path != null) {
                imgMapper.deleteImg(project, name);
            }
            SysPicture sysPicture = new SysPicture();
            //MYSQL需要换
            sysPicture.setPath(file.getAbsolutePath().replace("\\","/"));
            sysPicture.setName(name);
            sysPicture.setProject(project);
            integer = imgMapper.storeImg(sysPicture);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("插入数据失败");
        }
        return integer == null;
    }

    /**
     * 获取图片路径
     *
     * @param project  项目名
     * @param fileName 图片名
     * @return 图片路径
     */
    private String getPath(String project, String fileName) {
        return imgMapper.getPath(project, fileName).replace("/","\\");
    }

    /**
     * 删除数据库中的图片信息
     *
     * @param project  项目名
     * @param fileName 文件名
     * @return 成功标志
     */
    private boolean deleteImageInfo(String project, String fileName) {
        Integer integer = imgMapper.deleteImg(project, fileName);
        return integer != null;
    }
}
