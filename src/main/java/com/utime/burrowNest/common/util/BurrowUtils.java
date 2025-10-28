package com.utime.burrowNest.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		
		if( obj instanceof String ) {
			final String str = ((String)obj).trim();
			if( str.length() == 0 ) {
				return true;
			}
			if( str.equalsIgnoreCase("null") ) {
				return true;
			}
			return false;
		}
		
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
    	
    	if( BurrowUtils.isEmpty(dmsString) ) {
    		return 0D;
    	}
    	
        final String[] parts = dmsString.trim().split("\\s+");
        if (parts.length < 3) {
        	log.warn("잘못된 DMS 형식: " + dmsString);
        	return 0D;
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
        	log.warn("숫자 형식 오류: " + dmsString);
        	return 0D;
        }

        double decimalDegrees = degrees + (minutes / 60.0) + (seconds / 3600.0);

        if (direction.equals("S") || direction.equals("W")) {
            decimalDegrees *= -1;
        } else if (!direction.isEmpty() && !direction.equals("N") && !direction.equals("E")) {
        	log.warn("잘못된 방향 표시: " + direction);
        	decimalDegrees = 0D;
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
    
    private static DateTimeFormatter formatterZoneOffset = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ssXXX");
    private static DateTimeFormatter formatterUtcTime = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ssX");
    private static DateTimeFormatter formatterBasic = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
    
    public static LocalDateTime convertToLocalDateTime(String input) {
    	
    	if( BurrowUtils.isEmpty(input) ) {
    		return null;
    	}
    	
    	LocalDateTime result = null;
    	
    	try {
        	if (input.matches(".*[+-]\\d{2}:\\d{2}")) {
                // Case 1: with zone offset (e.g., +09:00)
                result = OffsetDateTime.parse(input, formatterZoneOffset).toLocalDateTime();
            } else if (input.endsWith("Z")) {
                // Case 2: UTC time
                result = OffsetDateTime.parse(input, formatterUtcTime).toLocalDateTime();
            } else {
                // Case 3: basic local format
                result = LocalDateTime.parse(input, formatterBasic);
            }
		} catch (Exception e) {
			log.error("" + input, e );
		}
    	
        return result;
    }
    
    public static Date convertToDate(String input) {
    	
    	final LocalDateTime dt = convertToLocalDateTime( input );
    	if( dt == null ) {
    		return null;
    	}
    	
        return Date.from(dt.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    public static LocalDateTime convertToLocalDateTime(FileTime fileTime) {
    	// 변환 과정: FileTime → Instant → LocalDateTime
        final LocalDateTime result = fileTime.toInstant()
                                              .atZone(ZoneId.systemDefault()) // 시스템 시간대 적용
                                              .toLocalDateTime();
        
        return result;
    }
    	
    	


    public static byte [] encodeImageToByteArray(InputStream inputStream) throws IOException {
        if (inputStream == null || inputStream.available() < 1) {
            return null; 
        }

        final byte[] imageBytes = StreamUtils.copyToByteArray(inputStream);
        if( imageBytes == null || imageBytes.length < 10 ) {
        	return null;
        }
        
        return imageBytes;
    }
    
    public static final MediaType MEDIA_TYPE_SVG = new MediaType("image", "svg+xml");

    
    public static MediaType detectImageType(byte[] header) {
    	
        if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8 &&
            header[2] == (byte) 0xFF && header[3] == (byte) 0xE0) {
            return MediaType.IMAGE_JPEG;
        }

        if (header[0] == (byte) 0x89 && header[1] == (byte) 0x50 &&
            header[2] == (byte) 0x4E && header[3] == (byte) 0x47 &&
            header[4] == (byte) 0x0D && header[5] == (byte) 0x0A &&
            header[6] == (byte) 0x1A && header[7] == (byte) 0x0A) {
            return MediaType.IMAGE_PNG;
        }

        if (header[0] == (byte) 0x3C && header[1] == (byte) 0x3F &&
            header[2] == (byte) 0x78 && header[3] == (byte) 0x6D &&
            header[4] == (byte) 0x6C) {
            return MEDIA_TYPE_SVG;
        }

        return null;
    }
    
    
    private final static ObjectWriter objWirter;
    static {
    	final ObjectMapper objMapper = new ObjectMapper();
    	objMapper.registerModule(new JavaTimeModule()); // Java 8 날짜/시간 타입 지원 추가
    	objWirter = objMapper.writerWithDefaultPrettyPrinter();
    }

    /**
     * json 형태 출력
     * @param obj
     * @return
     */
    public static String toJson( Object obj ) {
    	
    	if( obj == null ) {
    		return "Is null.";
    	}
    	
		try {
			return obj.getClass().getSimpleName() + ": " + objWirter.writeValueAsString(obj) + "\n";
		} catch (Exception e) {
			log.error("", e);
			return "{}";
		}

    }
}
