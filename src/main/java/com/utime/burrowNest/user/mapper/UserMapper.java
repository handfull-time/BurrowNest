package com.utime.burrowNest.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.burrowNest.common.vo.BinResultVo;
import com.utime.burrowNest.user.vo.ELoginResult;
import com.utime.burrowNest.user.vo.GroupVo;
import com.utime.burrowNest.user.vo.LoginReqVo;
import com.utime.burrowNest.user.vo.UserVo;

/**
 * 사용자 처리
 */
@Mapper
public interface UserMapper {
	
	/**
	 * 사용자 그룹
	 * @return
	 */
	int createUserGroup();
	
	/**
	 * 그룹 추가
	 * @return
	 */
	int insertGroup( GroupVo group );
	
	/**
	 * 그룹 조회
	 * @return
	 */
	GroupVo selectGroupByName( @Param("name") String name );
	
	/**
	 * 그룹 조회
	 * @param groupNo
	 * @return
	 */
	GroupVo selectGroupByNo( @Param("groupNo") int groupNo );
	
	/**
	 * 회원 테이블 생성
	 * @return
	 */
	int createUser();
	
	/**
	 * 비번 
	 * @return
	 */
	int createUserPw();
	
	/**
	 * 로그인 기록
	 * @return
	 */
	int createLoginRecord();
	
	
	/**
	 * 사용자 추가
	 * @param user
	 * @return
	 */
	int insertUser(@Param("user") UserVo user, @Param("profileImg") byte [] profileImg);
	
	/**
	 * 로그인 기록 추가.
	 * @param reqVo
	 * @param user
	 * @param idnotfound
	 * @return
	 */
	int insertLoginRecord(@Param("req") LoginReqVo reqVo, @Param("user") UserVo user, @Param("status") ELoginResult res);

	/**
	 * id 조회
	 * @param id
	 * @return
	 */
	UserVo selectUserId(@Param("id") String id);
	
	/**
	 * 기본 회원 정보 조회
	 * @param id
	 * @return
	 */
	UserVo selectUserIdBasic(@Param("id") String id);
	
	/**
	 * 암호 조회
	 * @param id
	 * @return
	 */
	String selectUserPw(@Param("id") String id);

	/**
	 * pw 추가
	 * @param user
	 * @param pw
	 * @return
	 */
	int insertUserPw(@Param("user") UserVo user, @Param("pw") String pw);
	
	/**
	 * pw 수정.
	 * @param user
	 * @param pw
	 * @return
	 */
	int updateUserPw(@Param("user") UserVo user, @Param("pw") String pw);
	
	/**
	 * 해당 id 찾기
	 * @param id
	 * @param genUserUniqueHashing
	 * @return
	 */
	UserVo findUser(@Param("id") String id, @Param("authHint") String genUserUniqueHashing);

	/**
	 * 사용자 정보 갱신 
	 * @param user
	 * @return
	 */
	int updateUser( @Param("user") UserVo user, @Param("profileImg") byte [] profileImg )throws Exception ;

	/**
	 * PW 성공 여부 횟수 업데이트
	 * @param user
	 * @param isSuccess
	 * @return
	 */
	int updatePwCount(@Param("user") UserVo user, @Param("isSuccess") boolean isSuccess);

	/**
	 * id 사용 여부
	 * @param id
	 * @return true: 있다. false:없다.
	 */
	boolean checkId(@Param("id") String id);

	/**
	 * 사용자 프로필 이미지
	 * @param userNo
	 * @return
	 */
	BinResultVo selectProfileImg(@Param("userNo") int userNo);
}
