package com.utime.burrowNest.storage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.burrowNest.storage.mapper.row.DirNodeRow;

@Mapper
public interface DirectoryMapper {

    // uid → 단일 노드
    DirNodeRow selectByUid(@Param("uid") String uid);

    // 사용자 접근 가능한 기본 루트(없으면 시스템 루트 반환)
    DirNodeRow selectDefaultRootForUser(@Param("userNo") long userNo);

    // 조상(루트→부모), 자신은 제외
    List<DirNodeRow> selectAncestorsByNo(@Param("no") long no);

    // 현재 노드의 유효 권한 비트 (Allow OR-집계)
    Integer selectEffectiveFlags(@Param("userNo") long userNo, @Param("dirNo") long dirNo);

    // 직계 자식 중에서 사용자에게 '읽기(1)' 가능한 노드만 반환 + hasChild 계산
    List<DirNodeRow> selectChildrenReadable(@Param("userNo") long userNo, @Param("parentNo") long parentNo);
    
    //“최상위 허용 디렉터리만” 반환
    List<DirNodeRow> selectAllowedRoots(@Param("userNo") long userNo);
}
