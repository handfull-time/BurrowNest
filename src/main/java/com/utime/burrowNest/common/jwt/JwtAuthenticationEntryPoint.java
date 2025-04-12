package com.utime.burrowNest.common.jwt;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.utime.burrowNest.common.vo.BurrowDefine;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("jwtAuthenticationEntryPoint")
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private final String contextPath = BurrowDefine.ContextPath+"/";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
    	
    	final String requestUri = request.getRequestURI();
    	
    	if( this.isBot(request.getHeader("User-Agent"))) {
    		log.info("Bot 진입 : " + request.getHeader("User-Agent"));
    		
    		String originalUrl = requestUri;
    		final String query = request.getQueryString();
            if (query != null) {
                originalUrl += "?" + query;
            }

            // URL 인코딩
            final String redirectUrl = URLEncoder.encode(originalUrl, StandardCharsets.UTF_8.name());

            response.sendRedirect(request.getContextPath() + "/Auth/NoneAuthMeta.html?redirectUrl=" + redirectUrl);
    		return;
    	}

    	final int status = response.getStatus();
        
    	log.warn( "Url:{}\tStatus:{}\tMessage:{}", requestUri, status, authException.getMessage() );

    	// 이 프로잭트의 메인 페이지는 .html로 끝난다.
        if( (requestUri.equals(contextPath) || requestUri.lastIndexOf(".html") > -1) && status == HttpServletResponse.SC_UNAUTHORIZED) {
        	// 이전 페이지 정보 저장
        	request.getSession().setAttribute(BurrowDefine.KeyBeforeUri, requestUri);
        	
            // 인증되지 않은 사용자가 보호된 페이지에 접근하면 로그인 페이지로 리디렉트
        	response.sendRedirect(contextPath + "Auth/Login.html");
        }
    }
    
    private boolean isBot(String userAgent) {
        return userAgent != null && (
                userAgent.contains("Slackbot") ||
                userAgent.contains("Twitterbot") ||
                userAgent.contains("facebookexternalhit") ||
                userAgent.contains("Discordbot") ||
                userAgent.contains("WhatsApp") ||
                userAgent.contains("KAKAOTALK") // 카톡
        );
    }

}