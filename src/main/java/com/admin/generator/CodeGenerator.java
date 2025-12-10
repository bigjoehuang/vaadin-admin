package com.admin.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 代码生成器
 *
 * @author Admin
 * @date 2024-01-01
 */
@Slf4j
@Component
public class CodeGenerator {

    @Value("${generator.author:Admin}")
    private String defaultAuthor;

    @Value("${generator.package-name:com.admin}")
    private String defaultPackageName;

    @Value("${generator.module-name:system}")
    private String defaultModuleName;

    /**
     * 生成代码
     */
    public void generate(GeneratorConfig config) {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
            cfg.setClassForTemplateLoading(CodeGenerator.class, "/generator/template/");
            cfg.setDefaultEncoding("UTF-8");

            Map<String, Object> data = new HashMap<>();
            data.put("config", config);
            data.put("author", config.getAuthor() != null ? config.getAuthor() : defaultAuthor);
            data.put("packageName", config.getPackageName() != null ? config.getPackageName() : defaultPackageName);
            data.put("moduleName", config.getModuleName() != null ? config.getModuleName() : defaultModuleName);
            data.put("date", LocalDate.now().toString());
            data.put("entityName", config.getEntityName());
            data.put("tableName", config.getTableName());
            data.put("tableComment", config.getTableComment());

            // 生成 Entity
            generateFile(cfg, "entity.ftl", data, getEntityPath(config), config.getEntityName() + ".java");

            // 生成 Mapper
            generateFile(cfg, "mapper.ftl", data, getMapperPath(config), config.getEntityName() + "Mapper.java");

            // 生成 Mapper XML
            generateFile(cfg, "mapper-xml.ftl", data, getMapperXmlPath(config), config.getEntityName() + "Mapper.xml");

            // 生成 Service
            generateFile(cfg, "service.ftl", data, getServicePath(config), config.getEntityName() + "Service.java");

            // 生成 ServiceImpl
            generateFile(cfg, "service-impl.ftl", data, getServiceImplPath(config), config.getEntityName() + "ServiceImpl.java");

            // 生成 Controller
            generateFile(cfg, "controller.ftl", data, getControllerPath(config), config.getEntityName() + "Controller.java");

            // 生成 View
            generateFile(cfg, "view.ftl", data, getViewPath(config), config.getEntityName() + "ListView.java");

            log.info("代码生成完成！");
        } catch (Exception e) {
            log.error("代码生成失败", e);
            throw new RuntimeException("代码生成失败", e);
        }
    }

    private void generateFile(Configuration cfg, String templateName, Map<String, Object> data,
                              String outputPath, String fileName) throws Exception {
        Template template = cfg.getTemplate(templateName);
        File outputDir = new File(outputPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        File file = new File(outputDir, fileName);
        try (Writer out = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            template.process(data, out);
            log.info("生成文件: {}", file.getAbsolutePath());
        }
    }

    private String getEntityPath(GeneratorConfig config) {
        return "src/main/java/" + config.getPackageName().replace(".", "/") + "/entity";
    }

    private String getMapperPath(GeneratorConfig config) {
        return "src/main/java/" + config.getPackageName().replace(".", "/") + "/mapper";
    }

    private String getMapperXmlPath(GeneratorConfig config) {
        return "src/main/resources/mapper";
    }

    private String getServicePath(GeneratorConfig config) {
        return "src/main/java/" + config.getPackageName().replace(".", "/") + "/service";
    }

    private String getServiceImplPath(GeneratorConfig config) {
        return "src/main/java/" + config.getPackageName().replace(".", "/") + "/service/impl";
    }

    private String getControllerPath(GeneratorConfig config) {
        return "src/main/java/" + config.getPackageName().replace(".", "/") + "/controller";
    }

    private String getViewPath(GeneratorConfig config) {
        return "src/main/java/" + config.getPackageName().replace(".", "/") + "/views";
    }
}

