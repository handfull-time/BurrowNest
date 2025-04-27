package com.utime.burrowNest.common.interceptor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.utime.burrowNest.common.vo.BurrowDefine;
import com.utime.burrowNest.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("ViewInterceptor")
class ViewHandlerInterceptor implements AsyncHandlerInterceptor {

	@Override
	public void postHandle(HttpServletRequest req, HttpServletResponse res, Object handler, ModelAndView view)
			throws Exception {
		
		if( ! (handler instanceof HandlerMethod) )
			return;
		
		final String path = req.getServletPath();
		if( path.lastIndexOf(".html") < 1 && path.lastIndexOf(".json") < 1 && ! path.contains("/File"))
			return;

		final ModelMap model = view.getModelMap();
		
		final String uri = req.getRequestURI();
		final String contextPath = req.getContextPath();
		model.addAttribute("currentURI", uri.substring(contextPath.length()) );
		
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null) {
			final Object obj = authentication.getPrincipal();
			if( obj != null && obj instanceof UserVo ) {
				model.addAttribute(BurrowDefine.KeyParamUser, obj );
			}
        }
	}
}
