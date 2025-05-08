package com.utime.burrowNest.storage.vo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum EAccessType {
	NONE(         0b0000), // 권한 없음
    READ(         0b0001), // 목록 조회 권한
    WRITE_UPLOAD( 0b0010), // 업로드 권한
    WRITE_MODIFY( 0b0100), // 수정 권한
    WRITE_DELETE( 0b1000), // 삭제 권한
	ALL(          0b1111); // 모든 권한

    private final int bit;

    EAccessType(int bit) {
        this.bit = bit;
    }

    public int getBit() {
        return bit;
    }

    /**
     * 비트 조합으로 포함 여부 확인
     * @param flags
     * @param type
     * @return
     */
    public static boolean hasPermission(int flags, EAccessType type) {
        return (flags & type.bit) != 0;
    }

    /**
     * 비트 플래그 → Enum 리스트 역변환
     * @param flags
     * @return
     */
    public static List<EAccessType> fromFlags(int flags) {
        return Arrays.stream(values())
                     .filter(p -> (flags & p.bit) != 0)
                     .collect(Collectors.toList());
    }

    /**
     *  Enum 리스트 → 비트값 변환
     * @param types
     * @return
     */
    public static int toFlags(List<EAccessType> types) {
        return types.stream()
        		.mapToInt(EAccessType::getBit)
        		.reduce(0, (a, b) -> a | b);
    }
    
    /**
     *  Enum 배열 → 비트값 변환
     * @param permissions
     * @return
     */
    public static int toFlags(EAccessType ... types) {
        return Arrays.stream(types)
                     .mapToInt(EAccessType::getBit)
                     .reduce(0, (a, b) -> a | b);
    }

}
