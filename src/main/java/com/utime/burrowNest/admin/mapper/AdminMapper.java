package com.utime.burrowNest.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.burrowNest.admin.vo.ManageUserVo;
import com.utime.burrowNest.user.vo.GroupVo;
import com.utime.burrowNest.user.vo.UserVo;

/**
 * 관리자 처리
 */
@Mapper
public interface AdminMapper {
	
	/**
	 * 사용자 정보 상세 갱신 
	 * @param user
	 * @return
	 */
	int updateUser( UserVo user );
	
	/**
	 * 사용자 목록 조회
	 * @return
	 */
	List<ManageUserVo> getUserList( @Param("id") String id);
	
	/**
	 * 사용자 상세 정보 조회
	 * @param userNo
	 * @return
	 */
	UserVo getUserDetail( @Param("userNo") int userNo);

	/**
	 * 회원 삭제
	 * @param userNo
	 * @return
	 */
	int deleteUser(int userNo);

	/**
	 * 사용자 그룹 정보 조회
	 * @return
	 */
	List<GroupVo> selectUserGroupList(@Param("enabled") Boolean enabled, @Param("grName") String grName);

	/**
	 * 사용자 그룹 정보 수정
	 * @param vo
	 * @return
	 */
	int updateGroup(GroupVo vo);

	/**
	 * 그룹 삭제
	 * @param groupNo
	 * @return
	 */
	int deleteGroup(int groupNo);

	/**
	 * Directory Access 그룹 추가
	 * @param groupNo
	 * @param accType
	 * @return
	 */
	int insertDirectoryAccessGroup(@Param("groupNo") int groupNo, @Param("accType") int accType);

	/**
	 * File Access 그룹 추가
	 * @param groupNo
	 * @param accType
	 * @return
	 */
	int insertFileAccessGroup(@Param("groupNo") int groupNo, @Param("accType") int accType);

	/**
	 * Directory Access 그룹 수정
	 * @param groupNo
	 * @param accType
	 * @return
	 */
	int updateDirectoryAccessGroup(@Param("groupNo") int groupNo, @Param("accType") int accType);

	/**
	 * File Access 그룹 수정
	 * @param groupNo
	 * @param accType
	 * @return
	 */
	int updateFileAccessGroup(@Param("groupNo") int groupNo, @Param("accType") int accType);

	

}
