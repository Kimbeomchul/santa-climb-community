package com.mozzi.santa.config;

import com.mozzi.santa.config.filter.LogFilter;
import com.mozzi.santa.config.filter.LoginCheckFilter;
import com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;


/**
 *  작성자 : beomchul.kim@lotte.net
 *  설정파일
 */



@Configuration
public class SantaConfig implements WebMvcConfigurer {



    // 로그필터설정
    @Bean
    public FilterRegistrationBean logFilter(){

        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new LogFilter()); // 필터설정
        filterFilterRegistrationBean.setOrder(1); // 필터순서
        filterFilterRegistrationBean.addUrlPatterns("/board/*" , "/sign/*", "/mt/*"); // 필터적용할 URL

        return filterFilterRegistrationBean;
    }


    // 로그인 체크필터
    @Bean
    public FilterRegistrationBean loginCheckFilter(){

        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new LoginCheckFilter()); // 필터설정
        filterFilterRegistrationBean.setOrder(2); // 필터순서
        filterFilterRegistrationBean.addUrlPatterns("/*"); // 필터적용할 URL

        return filterFilterRegistrationBean;
    }

    // xss
    @Bean
    public FilterRegistrationBean<XssEscapeServletFilter> filterRegistrationBean() {
        FilterRegistrationBean<XssEscapeServletFilter> filterRegistration = new FilterRegistrationBean<>();
        filterRegistration.setFilter(new XssEscapeServletFilter());
        filterRegistration.setOrder(3);
        filterRegistration.addUrlPatterns("/*");
        return filterRegistration;
    }

}
