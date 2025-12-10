package com.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 文件上传配置
 *
 * @author Admin
 * @date 2024-01-01
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "file.upload")
public class FileConfig {
    /**
     * 文件上传路径
     */
    private String path;

    /**
     * 最大文件大小
     */
    private String maxSize;

    /**
     * 允许的文件类型
     */
    private String allowedTypes;

    /**
     * OSS 配置
     */
    private OssConfig oss = new OssConfig();

    @Data
    public static class OssConfig {
        /**
         * 是否启用 OSS
         */
        private Boolean enabled = false;

        /**
         * OSS 端点
         */
        private String endpoint;

        /**
         * 访问密钥 ID
         */
        private String accessKeyId;

        /**
         * 访问密钥 Secret
         */
        private String accessKeySecret;

        /**
         * 存储桶名称
         */
        private String bucketName;
    }
}

