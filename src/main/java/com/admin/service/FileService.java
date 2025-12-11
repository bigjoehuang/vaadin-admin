package com.admin.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 *
 * @author Admin
 * @date 2024-01-01
 */
public interface FileService {
    /**
     * 上传文件
     */
    String uploadFile(MultipartFile file);

    /**
     * 删除文件
     */
    void deleteFile(String filePath);
}






