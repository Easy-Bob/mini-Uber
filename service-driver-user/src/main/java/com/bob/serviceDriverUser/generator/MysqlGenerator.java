package com.bob.serviceDriverUser.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * Created by Sun on 2025/8/12.
 * Description:自动生成代码工具类
 */
public class MysqlGenerator {
    public static void main(String[] args){
        String url = "jdbc:mysql:///service-driver-user?characterEncoding=utf-8&serverTimeZone=GMT%2B8";
        String username = "root";
        String password = "123456";

        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    builder.author("Bob Sun").fileOverride().outputDir("service-driver-user\\src\\main\\java");
                })
                .packageConfig(builder -> {
                     builder.parent("com.bob.serviceDriverUser")
                             .pathInfo(Collections.singletonMap(OutputFile.mapperXml,"service-driver-user\\src\\main\\java\\com\\bob\\serviceDriverUser\\mapper"));
                })
                .strategyConfig(builder -> {
                    builder.addInclude("car");
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
                ;
    }
}
