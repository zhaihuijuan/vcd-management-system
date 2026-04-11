package top.tatobamail.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源映射，前端打包文件放在 classpath:/dist/ 下
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/dist/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 根路径重定向到登录页
        registry.addRedirectViewController("/", "/login.html");
    }
}
