package com.utime.burrowNest.common.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("JwtAccessDenied")
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
	
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
    	log.warn("접근 권한이 없는 사용자입니다( AccessDeniedHandler )");
    	log.warn( "요청 URL : " + request.getRequestURI() );
    	
    	response.setStatus( HttpServletResponse.SC_FORBIDDEN );
    	
    	// 403 발생 시 특정 페이지로 이동
    	response.sendRedirect( request.getContextPath() + "/Error/AccessDenied.html?url=" + request.getRequestURI());
    }
}
