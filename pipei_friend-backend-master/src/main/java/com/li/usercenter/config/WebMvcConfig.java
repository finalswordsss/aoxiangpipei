package com.li.usercenter.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootConfiguration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //设置允许跨域的路径
        registry.addMapping("/**")
                //设置允许跨域请求的域名
                .allowedOrigins("http://127.0.0.1:8080", "http://friend.llong7.cn:8081", "http://friend-backend.llong7.cn:8081", "http://friend.llong7.cn", "http://friend-backend.llong7.cn")
                //是否允许证书，不再默认开启
                .allowCredentials(true)
                //设置允许的方法
                .allowedMethods("POST","GET","PUT","OPTIONS","DELETE")
                .allowedHeaders("*")
                .exposedHeaders("*")
                //跨域允许时间
                .maxAge(3600);
    }

}
