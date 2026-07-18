package net.berryhomes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final String uploadDir;

    public WebMvcConfig(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDirPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String uploadResourcePath = "file:" + uploadDirPath.toString() + "/";

        registry.addResourceHandler("/images/**")
                .addResourceLocations(uploadResourcePath + "images/");

        registry.addResourceHandler("/documents/**")
                .addResourceLocations(uploadResourcePath + "documents/");
    }
}