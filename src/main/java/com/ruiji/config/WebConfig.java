package com.ruiji.config;

//import com.alibaba.druid.support.http.ResourceServlet;
import com.ruiji.common.JacksonObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/backend/**").addResourceLocations("/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("/front/");
        registry.addResourceHandler("/test/**").addResourceLocations("/test/");
        super.addResourceHandlers(registry);
    }

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter mjhmc = new MappingJackson2HttpMessageConverter();
        mjhmc.setObjectMapper(new JacksonObjectMapper());
        converters.add(0, mjhmc);
//        super.extendMessageConverters(converters);
    }
}
