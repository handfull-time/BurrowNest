package com.utime.burrowNest.storage.vo;

/**
 * File 또는 Directory 접근 권한
 * @author utime
 *
 */
public enum EAccessType {
//	NONE(         0b0000), //  0 권한 없음
//  READ(         0b0001), //  1 목록 조회 권한
//  WRITE_UPLOAD( 0b0011), //  3 업로드 권한
//  WRITE_MODIFY( 0b0111), //  7 수정 권한
//	ALL(          0b1111); // 15 모든 권한
	None(0, "없음")
	, Read(1, "읽기 전용")
	, Download(3, "다운로드")
	, Upload(7, "업로드")
	, All(15, "모든 권한");

    private final int bit;
    private final String dscr;

    EAccessType(int bit, String dscr) {
        this.bit = bit;
        this.dscr = dscr;
    }

    public int getBit() {
        return bit;
    }
    
    public String getDscr() {
		return dscr;
	}
    
    public static EAccessType getEAccessType( int accType ) {
    	final EAccessType result;
    	switch( accType ) {
    	case  1:result = Read; break;
    	case  3:result = Download; break;
    	case  7:result = Upload; break;
    	case 15:result = All; break;
    	default: result = None; 
    	}
    	
    	return result;
    }

//    /**
//     * 비트 조합으로 포함 여부 확인
//     * @param flags
//     * @param type
//     * @return
//     */
//    public static boolean hasPermission(int flags, EAccessType type) {
//        return (flags & type.bit) != 0;
//    }
//
//    /**
//     * 비트 플래그 → Enum 리스트 역변환
//     * @param flags
//     * @return
//     */
//    public static List<EAccessType> fromFlags(int flags) {
//        return Arrays.stream(values())
//                     .filter(p -> (flags & p.bit) != 0)
//                     .collect(Collectors.toList());
//    }
//
//    /**
//     *  Enum 리스트 → 비트값 변환
//     * @param types
//     * @return
//     */
//    public static int toFlags(List<EAccessType> types) {
//        return types.stream()
//        		.mapToInt(EAccessType::getBit)
//        		.reduce(0, (a, b) -> a | b);
//    }
//    
//    /**
//     *  Enum 배열 → 비트값 변환
//     * @param permissions
//     * @return
//     */
//    public static int toFlags(EAccessType ... types) {
//        return Arrays.stream(types)
//                     .mapToInt(EAccessType::getBit)
//                     .reduce(0, (a, b) -> a | b);
//    }

}
