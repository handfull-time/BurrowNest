package com.utime.burrowNest.common.vo;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

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
			, "/DbConsoleH2/"
			, "/Test/"
			, "/BackEvent/"
		};
	
	public static final Set<String> whiteListPaths = Arrays.stream(AddressList)
//            .map(path -> path.endsWith("/") ? path : path + "/") 
            .collect(Collectors.toSet());
}
