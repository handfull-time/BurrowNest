package com.utime.burrowNest.admin.service;

import java.util.List;

import com.utime.burrowNest.admin.vo.ManageUserVo;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.user.vo.GroupVo;
import com.utime.burrowNest.user.vo.UserVo;

public interface AdminUserService {

	/**
	 * 사용자 전체 목록 조회
	 * @param id
	 * @return
	 */
	List<ManageUserVo> userList(String id);

	/**
	 * 사용자 상세 보기
	 * @param userNo
	 * @return
	 */
	UserVo getUserFromNo(int userNo);

	/**
	 * 사용자 정보 저장
	 * @param user
	 * @return
	 */
	ReturnBasic saveUser(UserVo user);

	/**
	 * 회원 삭제
	 * @param user
	 * @return
	 */
	ReturnBasic deleteUser(UserVo user);
	
	/**
	 * 사용자 그룹 전체 조회
	 * @param grName 
	 * @return
	 */
	List<GroupVo> getUserGroupListAll(String grName);

	/**
	 * 사용자 그룹 정보 조회
	 * @return
	 */
	List<GroupVo> getUserGroupList();
	
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
	ReturnBasic saveGroup(GroupVo vo);

	/**
	 * 그룹 삭제
	 * @param group
	 * @return
	 */
	ReturnBasic deleteGroup(GroupVo group);

}
