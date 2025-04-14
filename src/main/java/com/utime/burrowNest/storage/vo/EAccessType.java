package com.utime.burrowNest.storage.vo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum EAccessType {
	READ(0b001),
    WRITE(0b010),
    EXECUTE(0b100); // 디렉토리 진입 권한 등

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
