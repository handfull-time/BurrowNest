package com.utime.burrowNest.common.config;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.utime.burrowNest.common.vo.WhiteAddressList;

import jakarta.annotation.Resource;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;

@Configuration
public class ViewResolverConfig implements WebMvcConfigurer { 
    
	/**
	 * Static resources handler<P>
	 * 실제 위치와 서버 호출 주소를 매핑 시켜 준다.
	 */
	@Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
        	.addResourceLocations("classpath:/static/images/")
        	.setCacheControl(CacheControl.maxAge(2, TimeUnit.DAYS).cachePublic().immutable());
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
    }
	
	@Resource(name="UserArgument")
	private HandlerMethodArgumentResolver userArgument;

    /**
     * Controller에서 별도 Object 형태의 파라미터를 전달 받을 때 사용함.
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(this.userArgument);
    }
    
    @Resource(name="ViewInterceptor")
	private AsyncHandlerInterceptor viewInterceptor;
    
    @Override
	public void addInterceptors(InterceptorRegistry registry) {
		
    	final List<String> patterns = Arrays.stream(WhiteAddressList.AddressList).map(path -> path.endsWith("/") ? path + "**" : path ).toList();
		
		registry.addInterceptor( this.viewInterceptor ).excludePathPatterns(patterns).addPathPatterns("/**/*.html");
    }
    
	@Bean
    public SpringResourceTemplateResolver templateResolver() {
        final SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver ();
        
        templateResolver.setPrefix("classpath:templates/");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
//        templateResolver.setCacheable(true);
        templateResolver.setCacheable(false);
        templateResolver.setOrder(0);
        
        return templateResolver;
    }
    
    @Bean
    public SpringTemplateEngine templateEngine(MessageSource messageSource) {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        
        templateEngine.setEnableSpringELCompiler( true );
        templateEngine.setTemplateResolver( this.templateResolver());
        templateEngine.setTemplateEngineMessageSource(messageSource);
        templateEngine.addDialect(new LayoutDialect());
        
        return templateEngine;
    }
    
    @Bean
    public ObjectMapper objectMapper() {
    	final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Java 8 날짜/시간 타입 지원 추가
        return objectMapper;
    }

}
