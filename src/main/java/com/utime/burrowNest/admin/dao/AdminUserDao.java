package com.utime.burrowNest.admin.dao;

import java.util.List;

import com.utime.burrowNest.admin.vo.ManageUserVo;
import com.utime.burrowNest.user.vo.GroupVo;
import com.utime.burrowNest.user.vo.UserVo;

public interface AdminUserDao {

	/**
	 * 회원 전체 조회
	 * @param id
	 * @return
	 */
	List<ManageUserVo> userList(String id);

	/**
	 * 회원 정보 조회
	 * @param userNo
	 * @return
	 */
	UserVo getUserFromNo(int userNo);

	/**
	 * 사용자 정보 수정
	 * @param user
	 * @return
	 */
	int updateUser(UserVo user)throws Exception;

	/**
	 * 회원 삭제
	 * @param user
	 * @return
	 * @throws Exception
	 */
	int deleteUser(UserVo user)throws Exception;
	
	/**
	 * 사용자 그룹 정보 조회
	 * @return
	 */
	List<GroupVo> getUserGroupList(Boolean enabled, String grName);
	
	/**
	 * 그룹 정보 조회
	 * @param groupNo
	 * @return
	 */
	GroupVo getGroupByNo(int groupNo);
	
	/**
	 * 그룹 정보 저장
	 * @param vo
	 * @return
	 */
	int saveGroup(GroupVo vo)throws Exception;
	
	
	

}
