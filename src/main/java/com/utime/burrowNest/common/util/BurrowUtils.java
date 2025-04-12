package com.utime.burrowNest.common.util;

import jakarta.servlet.http.HttpServletRequest;

public class BurrowUtils {
	
	private static final String UnknownIp = "unknown";
	
	/**
	 * 접근한 IP Address를 반환한다.
	 * @param request
	 * @return Real Remote Address
	 */
	public static String getRemoteAddress( final HttpServletRequest request ) {
		
		String result = request.getHeader("X-Forwarded-For");
        if (result == null || result.length() == 0 || UnknownIp.equalsIgnoreCase(result)) {  
            result = request.getHeader("Proxy-Client-IP");  
        }  
        if (result == null || result.length() == 0 || UnknownIp.equalsIgnoreCase(result)) {  
            result = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if (result == null || result.length() == 0 || UnknownIp.equalsIgnoreCase(result)) {  
            result = request.getHeader("HTTP_CLIENT_IP");  
        }  
        if (result == null || result.length() == 0 || UnknownIp.equalsIgnoreCase(result)) {  
            result = request.getHeader("HTTP_X_FORWARDED_FOR");  
        }  
        if (result == null || result.length() == 0 || UnknownIp.equalsIgnoreCase(result)) {  
            result = request.getRemoteAddr();  
        }
        
        if( result != null && result.indexOf(",") > 0 ){
        	// ELB 접근 했을 때와 EC2 접근 IP가 [,]를 구분으로 넘어 온다.
        	result = result.split(",")[0];
        }
        
        return result;
	}
	
	/**
	 * obj 값이 비었는가? 
	 * @param obj
	 * @return true : null 또는 암것도 없다.
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty( Object obj ) {
		
		if( obj == null )
			return true;
		
		if( obj instanceof String )
			return ((String)obj).trim().length() == 0;
		
		if( obj instanceof java.lang.Iterable ) 
			return !((java.lang.Iterable)obj).iterator().hasNext();
		
		if( obj instanceof java.util.Map ) 
			return ((java.util.Map)obj).isEmpty();
		
		if( obj.getClass().isArray() ) 
			return ((Object[])obj).length == 0;
		
		if( obj instanceof Number )
			return ((Number)obj).longValue() != 0L;
		
		return false;
	}
	
	public static boolean isNotEmpty( Object obj ) {
		return ! BurrowUtils.isEmpty(obj);
	}

}
