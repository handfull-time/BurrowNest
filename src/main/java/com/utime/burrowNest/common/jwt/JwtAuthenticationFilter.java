package com.utime.burrowNest.common.jwt;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.utime.burrowNest.common.vo.WhiteAddressList;
import com.utime.burrowNest.user.vo.ResUserVo;
import com.utime.burrowNest.user.vo.UserVo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(1)
@Component("JwtAuthentication")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
    private JwtProvider jwtUtil;
    
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
    	 
    	final String path = request.getRequestURI().substring(request.getContextPath().length());
//    	final String path = request.getRequestURI();
    	if( path.length() < 1) {
    		return false;
    	}

    	return WhiteAddressList.whiteListPaths.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
    	
    	ResUserVo tokenRes = jwtUtil.procPagingToken(request, response);
    	if( tokenRes.isError() ) {
    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    	}else {
    		log.info(request.getRequestURI());
    	    this.authenticateUser( tokenRes.getUser() );
    	}
    	
    	filterChain.doFilter(request, response);
    }
    
    /**
     * SecurityContext에 사용자 정보 저장
     * @param token
     */
    private void authenticateUser(UserVo user) {
    	
        if (user != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("Setting Authentication for user: {}", user.getId());
            
            final Authentication authToken = new UsernamePasswordAuthenticationToken(user, null,
                    Collections.singleton( new SimpleGrantedAuthority(user.getGroup().getRole().name()) ));
            
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }
}
