package io.github.sobotapl.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Set;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

    private Set<HandlerInterceptor> interceptros;

    public MvcConfiguration(Set<HandlerInterceptor> interceptros) {
        this.interceptros = interceptros;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        interceptros.forEach(registry::addInterceptor);

    }
}
