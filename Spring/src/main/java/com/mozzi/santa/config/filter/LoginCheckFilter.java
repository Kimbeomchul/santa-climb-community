package com.mozzi.santa.config.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.mozzi.santa.config.Constants.SESSION_NAME;

@Slf4j
public class LoginCheckFilter implements Filter {

    private static final String[] whitelist = {"/" , "/login/*" , "/auth/*" ,"/terms/*" , "/sign/*", "/css/*", "/js/*" , "/fonts/*", "/img/*", "/flutter/*" ,"/download" , "/apk/*" , "/sign/validate/*"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        // 다운캐스팅
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();

        try{

            if(checkPath(requestURI)){
                HttpSession session = httpRequest.getSession(false);

                if(session == null || session.getAttribute(SESSION_NAME) == null){
                    // 로그인 사용자정보가 없을시 메인으로 이동시키며 이전 URL 가지고 이동
                    httpResponse.sendRedirect("/?continue=" + requestURI);
                    log.debug("LoginCheck filter RequestedURI = {}" , requestURI);
                    return;
                }
            }
            chain.doFilter(request, response);

        }catch(Exception e){
            throw e;
        }

    }

    /**
     * 화이트리스트에 존재시 필터스킵
     * @param requestURI
     * @return
     */
    private boolean checkPath(String requestURI){
        return !PatternMatchUtils.simpleMatch(whitelist,requestURI);
    }

}
