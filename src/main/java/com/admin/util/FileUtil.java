package com.admin.util;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 文件工具类
 *
 * @author Admin
 * @date 2024-01-01
 */
@Slf4j
public class FileUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /**
     * 生成文件路径
     */
    public static String generateFilePath(String basePath, String originalFilename) {
        String datePath = LocalDateTime.now().format(DATE_FORMATTER);
        String filename = System.currentTimeMillis() + "_" + originalFilename;
        return basePath + "/" + datePath + "/" + filename;
    }

    /**
     * 获取文件扩展名
     */
    public static String getExtension(String filename) {
        if (StrUtil.isBlank(filename)) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 创建目录
     */
    public static void createDir(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            // 使用 Hutool 的 FileUtil，使用完全限定名避免命名冲突
            cn.hutool.core.io.FileUtil.mkdir(dir);
        }
    }
}

