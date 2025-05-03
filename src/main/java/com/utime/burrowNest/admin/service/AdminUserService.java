package com.utime.burrowNest.admin.service;

import java.util.List;

import com.utime.burrowNest.admin.vo.ManageUserVo;
import com.utime.burrowNest.common.vo.ReturnBasic;
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

}
