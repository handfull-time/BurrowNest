package com.utime.burrowNest.common.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.utime.burrowNest.common.vo.EJwtRole;
import com.utime.burrowNest.common.vo.WhiteAddressList;

import jakarta.annotation.Resource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Resource(name="JwtAuthentication")
	private jakarta.servlet.Filter jwtAuthFilter;
	
	@Resource(name="jwtAuthenticationEntryPoint")
	private AuthenticationEntryPoint authenticationEntryPoint;
	
	@Resource(name="JwtAccessDenied")
	private AccessDeniedHandler accessDeniedHandler;
	
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {

    	final List<MvcRequestMatcher> permitAllMatchers = Arrays.stream(WhiteAddressList.AddressList)
    	        .map(path -> new MvcRequestMatcher(introspector, path.endsWith("/") ? path + "**" : path))
    	        .collect(Collectors.toList());

    	// ➕ 수동으로 /File/Stream 경로를 추가
    	permitAllMatchers.add(new MvcRequestMatcher(introspector, "/File/Stream"));
    	permitAllMatchers.add(new MvcRequestMatcher(introspector, "/"));
    
    	// 배열로 변환
    	final MvcRequestMatcher[] permitAllWhiteList = permitAllMatchers.toArray(new MvcRequestMatcher[0]);
	
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers( permitAllWhiteList ).permitAll() // 누구나 접근 가능.
        	    .requestMatchers("/Admin/**").hasRole(EJwtRole.Admin.name()) 
        	    .requestMatchers("/File/**", "/Dir/**", "/User/**").hasAnyRole(EJwtRole.User.name()) 
                .anyRequest().authenticated()
            );
        
        
        http.formLogin(AbstractHttpConfigurer::disable);
        
        http.logout(AbstractHttpConfigurer::disable);
        
//        http.csrf(AbstractHttpConfigurer::disable);
        
        http
        .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
        .headers(headers -> headers.frameOptions(FrameOptionsConfig::disable)); // FrameOptions 비활성화

        http.sessionManagement(session -> session
        		.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        	);

        http.addFilterBefore(this.jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(conf -> conf
                .authenticationEntryPoint(this.authenticationEntryPoint)
                .accessDeniedHandler(this.accessDeniedHandler)
            );
        
        return http.build();
    }
    
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .authorizeHttpRequests(auth -> auth
//                .anyRequest().permitAll()
//            )
//            .csrf(AbstractHttpConfigurer::disable)
//            .formLogin(AbstractHttpConfigurer::disable)
//            .httpBasic(AbstractHttpConfigurer::disable);
//        
//        return http.build();
//    }

}
