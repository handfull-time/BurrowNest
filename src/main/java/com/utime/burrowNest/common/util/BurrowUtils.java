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
	
	/**
     * DMS (Degree Minute Second) 형식의 위도 또는 경도 문자열을 Decimal Degree 형식으로 변환합니다.
     *
     * @param dmsString DMS 형식의 문자열 (예: "37 33' 59.78\" N", "126 58' 41.23\" E")
     * @return 변환된 Decimal Degree 값 (Double)
     * @throws IllegalArgumentException DMS 문자열 형식이 올바르지 않은 경우 발생
     */
    public static double dmsToDecimal(String dmsString) {
        String[] parts = dmsString.trim().split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("잘못된 DMS 형식: " + dmsString);
        }

        double degrees;
        double minutes;
        double seconds;
        String direction = "";

        try {
            degrees = Double.parseDouble(parts[0]);
            minutes = Double.parseDouble(parts[1].replace("'", ""));
            seconds = Double.parseDouble(parts[2].replace("\"", ""));

            if (parts.length > 3) {
                direction = parts[3].toUpperCase();
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("숫자 형식 오류: " + dmsString);
        }

        double decimalDegrees = degrees + (minutes / 60.0) + (seconds / 3600.0);

        if (direction.equals("S") || direction.equals("W")) {
            decimalDegrees *= -1;
        } else if (!direction.isEmpty() && !direction.equals("N") && !direction.equals("E")) {
            throw new IllegalArgumentException("잘못된 방향 표시: " + direction);
        }

        return decimalDegrees;
    }
    
    /**
     * Decimal Degree 형식의 위도 또는 경도를 DMS (Degree Minute Second) 형식의 문자열로 변환합니다.
     *
     * @param decimalDegree Decimal Degree 값
     * @param isLatitude    위도인 경우 true, 경도인 경우 false
     * @return DMS 형식의 문자열 (예: "37° 33' 59.78\" N", "126° 58' 41.23\" E")
     */
    public static String decimalToDMS(double decimalDegree, boolean isLatitude) {
        int degrees = (int) decimalDegree;
        double remainingMinutes = (decimalDegree - degrees) * 60;
        int minutes = (int) remainingMinutes;
        double seconds = (remainingMinutes - minutes) * 60;

        String direction = "";
        if (isLatitude) {
            direction = (decimalDegree >= 0) ? "N" : "S";
        } else {
            direction = (decimalDegree >= 0) ? "E" : "W";
        }

        degrees = Math.abs(degrees);
        minutes = Math.abs(minutes);
        seconds = Math.abs(seconds);

        return String.format("%d° %d' %.2f\" %s", degrees, minutes, seconds, direction);
    }

}
