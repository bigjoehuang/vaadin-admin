package com.admin.service.impl;

import com.admin.config.FileConfig;
import com.admin.service.FileService;
import com.admin.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 文件服务实现
 *
 * @author Admin
 * @date 2024-01-01
 */
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);
    private final FileConfig fileConfig;

    @Override
    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String filePath = FileUtil.generateFilePath(fileConfig.getPath(), originalFilename);

        try {
            FileUtil.createDir(new File(filePath).getParent());
            file.transferTo(new File(filePath));
            log.info("文件上传成功: {}", filePath);
            return filePath;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
            log.info("文件删除成功: {}", filePath);
        }
    }
}

