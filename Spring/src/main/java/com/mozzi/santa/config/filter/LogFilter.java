package com.mozzi.santa.config.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.debug("log filter doFilter");

        // 다운캐스팅
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String requestURI = httpRequest.getRequestURI();
        String uuid = UUID.randomUUID().toString();
        try{
            log.debug("Logging Filter Req UUID, RequestURI = [{}][{}] ", uuid ,requestURI);
            chain.doFilter(request, response);
        }catch(Exception e){
            throw e;
        }finally {
            log.debug("Logging Filter Res UUID, RequestURI = [{}][{}] " , uuid,requestURI);
        }

    }

}
