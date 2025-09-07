package org.example.tlonlineedupbackend.config;

import org.example.tlonlineedupbackend.auth.util.StringToCourseConverter;
import org.example.tlonlineedupbackend.auth.util.StringToDepartmentConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final StringToCourseConverter stringToCourseConverter;
    private final StringToDepartmentConverter stringToDepartmentConverter;

    public WebConfig(
            StringToCourseConverter stringToCourseConverter,
            StringToDepartmentConverter stringToDepartmentConverter
    ) {
        this.stringToCourseConverter = stringToCourseConverter;
        this.stringToDepartmentConverter = stringToDepartmentConverter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToCourseConverter);
        registry.addConverter(stringToDepartmentConverter);
    }
}
