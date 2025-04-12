package com.utime.burrowNest.common.vo;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;

/**
 * white list (Spring Security 체크 제외 목록)
 * @author utime
 *
 */
public class WhiteAddressList {

	/**
	 * white list
	 */
	public static String [] AddressList = new String[] {
			"/js/"
			, "/images/"
			, "/css/"
			, "/Auth/"
			, "/Error/"
			, "/error/"
			, "/Intro/"
			, "/Burrow-h2"
		};
	
	public static final Set<String> whiteListPaths = Arrays.stream(AddressList)
//            .map(path -> path.endsWith("/") ? path : path + "/") 
            .collect(Collectors.toSet());
}
