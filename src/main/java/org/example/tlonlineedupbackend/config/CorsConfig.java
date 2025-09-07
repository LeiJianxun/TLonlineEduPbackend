package org.example.tlonlineedupbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 适用于所有路径
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 包括 OPTIONS
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(true) // 允许发送 cookies 或认证信息
                .maxAge(3600); // 缓存 CORS 响应，避免重复的预检请求
    }
}
