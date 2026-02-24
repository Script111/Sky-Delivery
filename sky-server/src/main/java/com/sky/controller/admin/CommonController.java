package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.management.ObjectName;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api("文件上传")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;
    //文件上传
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("upload");
        String originalFilename = file.getOriginalFilename();
        String extension= originalFilename.substring(originalFilename.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString() + extension;
        try {
            String FilePath = aliOssUtil.upload(file.getBytes(), uuid);
            return Result.success(FilePath);
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
