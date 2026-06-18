package com.rubber.shop.controller;

import com.rubber.shop.common.BusinessException;
import com.rubber.shop.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UploadController {

    @Value("${upload.path}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID().toString() + extension;
        File destDir = new File(System.getProperty("user.dir"), uploadPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        File dest = new File(destDir, fileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new BusinessException("文件上传失败");
        }

        return Result.success("上传成功", "/uploads/" + fileName);
    }
}
