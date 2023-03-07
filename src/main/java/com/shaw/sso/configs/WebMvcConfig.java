package com.shaw.sso.configs;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author shaw
 * @date 2022/12/12
 */
@Configuration
public class WebMvcConfig extends WebMvcAutoConfiguration implements WebMvcConfigurer {

    //加载静态页面的css和js文件
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String[] STATIC_RESOURCE = {"/", "classpath:/", "classpath:/META-INF/resources/", "classpath:/META-INF/resources/webjars/",
                "classpath:/resources/", "classpath:/static/"};
        if (!registry.hasMappingForPattern("/**")) {
            registry.addResourceHandler("/**").addResourceLocations(STATIC_RESOURCE);
        }
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        spring.mvc.view.prefix + /index + spring.mvc.view.suffix 构成完整的请求地址
//        访问项目根目录即是访问 /views/index.html页面
//        registry.addViewController("/").setViewName("/index");
    }


    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    }
}
