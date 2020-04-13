package com.bishe.java.config;

import com.github.pagehelper.PageHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @ClassName： PageHelperConfig
 * @description:
 * @author: lisheng
 * @create: 2020-02-09 14:24
 **/
@Configuration
public class PageHelperConfig {

    @Bean
    public PageHelper pageHelper(){
        PageHelper pageHelper = new PageHelper();
        Properties p = new Properties();
        p.setProperty("offsetAsPageNum","true"); //设置为true时，会将RowBounds第一个参数offset当成pageNum页码使用.
        p.setProperty("rowBoundsWithCount","true"); //设置为true时，使用RowBounds分页会进行count查询.
        p.setProperty("reasonable","true"); //启用合理化时，如果pageNum<1会查询第一页，如果pageNum>pages会查询最后一页。
        pageHelper.setProperties(p);
        return pageHelper;
    }
}
