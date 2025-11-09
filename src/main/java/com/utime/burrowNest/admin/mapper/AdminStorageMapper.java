package com.utime.burrowNest.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.burrowNest.storage.vo.BnDirectory;

/**
 * 어드민 저장소 
 */
@Mapper
public interface AdminStorageMapper {
	
	/**
	 * 루트 Dir 조회
	 * @return
	 */
	BnDirectory selectRootDirectory();
	
	/**
	 * parentNo 기준 조회
	 * @param parentNo
	 * @return
	 */
	List<BnDirectory> selectBnDirectoryParentNo(@Param("parentNo") long parentNo);

	/**
	 *  루트 비포함 directory 조회
	 * @param groupNo
	 * @return
	 */
	List<BnDirectory> selectUnIncludeRootDirectories(@Param("groupNo") long groupNo);
	
	/**
	 * 루트 directory 조회
	 * @param user
	 * @return
	 */
	List<BnDirectory> selectRootDirectories(@Param("groupNo") long groupNo);
	
	/**
	 * 
	 * @param groupNo
	 * @param uid
	 * @return
	 */
	List<BnDirectory> getGroupStorageList(@Param("groupNo") long groupNo, @Param("dirNo") long dirNo);

	/**
	 * 
	 * @param groupNo
	 * @param parentUid
	 * @return
	 */
	List<BnDirectory> getGroupStorageListUid(@Param("groupNo") long groupNo, @Param("parentUid") String parentUid);
	
	/**
	 * 그룹 저장소 삭제
	 * @param groupNo
	 * @param dirNo
	 * @return
	 */
	int removeGroupStorage(@Param("groupNo") long groupNo, @Param("dirNo") long dirNo)throws Exception;

	/**
	 * 그룹 소유자 저장소 목록 전달
	 * @param groupNo
	 * @return
	 */
	List<BnDirectory> getOwnerGroupStorageList(@Param("groupNo") long groupNo);
}
