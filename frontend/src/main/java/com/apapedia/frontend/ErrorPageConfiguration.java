package com.apapedia.frontend;

import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("dev")
public class ErrorPageConfiguration {

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/error/400").setViewName("400");
                registry.addViewController("/error/404").setViewName("404");
                registry.addViewController("/error/500").setViewName("500");
            }
        };
    }

    @Bean
    public ErrorViewResolver customErrorViewResolver() {
        return (request, status, model) -> {
            if (status == HttpStatus.BAD_REQUEST) {
                return new ModelAndView("400");
            } else if (status == HttpStatus.NOT_FOUND) {
                return new ModelAndView("404");
            } else if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
                return new ModelAndView("500");
            }
            return null;
        };
    }
}



