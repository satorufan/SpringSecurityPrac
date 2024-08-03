package com.cheeus.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
//        	.exposedHeaders("Set-Cookie")
            .allowedOrigins("http://localhost:3000")
            .allowedHeaders("*") // 허용할 HTTP 헤더
            .allowCredentials(true) // 인증 정보를 포함할지 여부
            .allowedMethods(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.DELETE.name());
    }

}
