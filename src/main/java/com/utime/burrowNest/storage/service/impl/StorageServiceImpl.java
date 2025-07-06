package com.utime.burrowNest.storage.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.storage.dao.StorageDao;
import com.utime.burrowNest.storage.service.StorageService;
import com.utime.burrowNest.storage.vo.AbsPath;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.BnFile;
import com.utime.burrowNest.storage.vo.DirectoryDto;
import com.utime.burrowNest.storage.vo.EBnFileType;
import com.utime.burrowNest.user.dao.UserDao;
import com.utime.burrowNest.user.vo.UserVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
	
	private final UserDao userDao;

	private final StorageDao storageDao;
	
	private Map<String, EBnFileType> mapFileType;
	
	private final DirecotryManager dirManager;
	
	/**
	 * ApplicationReadyEvent
	 */
	@EventListener(ApplicationReadyEvent.class)
	protected void handleApplicationReadyEvent() {
		this.mapFileType = storageDao.getBnFileType();
	}
	
	@Override
	public byte[] getThumbnail(String uid) {
		return storageDao.getThumbnail( uid );
	}

//	@Override
	public List<DirectoryDto> getRootDirectory(UserVo user) {
		
		return dirManager.getAccessibleDirectoriesForGroup(user.getGroup().getGroupNo());
		
//		final BnDirectory result = storageDao.getRootDirectory(user);
		
//		storageDao.get
		/*
	SELECT 
		DR.NO 
		, DR.REG_DATE 
		, DR.UPDATE_DATE 
		, DR.UID 
		, DR.PARENT_NO	
		, DR.OWNER_NO 
		, DR.HAS_CHILD	
		, DR.CREATION 
		, DR.LAST_MODIFIED 
		, DR.NAME CHARACTER 
		, DR.ABSOLUTE_PATH	
		, DA.ACCESS_FLAGS
	FROM BN_DIRECTORY DR
	INNER JOIN BN_DIRECTORY_ACCESS DA
	    ON DR.NO = DA.DIR_NO
	INNER JOIN BN_USER_GROUP GR
	    ON GR.NO = DA.GROUP_NO
	WHERE 1=1
	    AND GR.NO = 1
	    AND BITAND( DA.ACCESS_FLAGS, 1) = 1
		AND DR.ENABLED = TRUE
		AND GR.ENABLED = TRUE
	ORDER BY GR.NO

	-- SELECT * FROM BN_USER_GROUP;

	-- SELECT * FROM BN_DIRECTORY_ACCESS ;
	
	
	<select id="selectTopGroup1Nodes" resultType="int">
  <![CDATA[
    WITH RECURSIVE data_tree AS (
        SELECT no, parent_no
        FROM data
        WHERE `group` = 1

        UNION ALL

        SELECT d.no, d.parent_no
        FROM data d
        INNER JOIN data_tree dt ON dt.parent_no = d.no
    )
    SELECT DISTINCT no
    FROM data_tree
    WHERE no = parent_no
  ]]>
</select>



H2 DB이다.
BN_DIRECTORY, BN_DIRECTORY_ACCESS 를 이용해 아래 쿼리를 실행했더니 오류가 난다.

	CREATE TABLE BN_DIRECTORY (
		NO BIGINT PRIMARY KEY AUTO_INCREMENT
		, ENABLED BOOLEAN DEFAULT FALSE -- 사용 여부. T:사용, F:미사용
		, PARENT_NO	BIGINT NOT NULL	-- 부모 번호
		, NAME CHARACTER VARYING(255) NOT NULL -- 순수  이름
		, FOREIGN KEY(PARENT_NO) REFERENCES BN_DIRECTORY(NO) ON DELETE CASCADE
	)


	CREATE TABLE BN_DIRECTORY_ACCESS (
		DIR_NO BIGINT NOT NULL
		, GROUP_NO INT NOT NULL 
		, ACCESS_FLAGS TINYINT NOT NULL
		, PRIMARY KEY (DIR_NO, GROUP_NO)
		, FOREIGN KEY(DIR_NO) REFERENCES BN_DIRECTORY(NO) ON DELETE CASCADE
		, FOREIGN KEY(GROUP_NO) REFERENCES BN_USER_GROUP(NO) ON DELETE CASCADE
	)

BN_DIRECTORY는 PARENT_NO를 이용해 TREE NODE 구조를 갖고 있다.
그래서 이 쿼리의 목적은 GROUP_NO = 4의 최 상위 노드 번호 NO를 구하는 것이다.
	
	
WITH RECURSIVE DATA_TREE(NO, PARENT_NO, LEVEL) AS (
    -- 시작: GROUP_NO = 4인 노드들
    SELECT 
    	DR.NO
    	, DR.PARENT_NO
    	, 1 AS LEVEL
    FROM BN_DIRECTORY DR
    	INNER JOIN BN_DIRECTORY_ACCESS DA 
    		ON DR.NO = DA.DIR_NO
    WHERE 1=1
    	AND DR.ENABLED = TRUE 
    	AND DA.GROUP_NO = 4

    UNION ALL

    -- 부모 방향으로 계속 올라가기
    SELECT 
    	D.NO
    	, D.PARENT_NO
    	, LEVEL + 1
    FROM BN_DIRECTORY D
    	INNER JOIN DATA_TREE DT 
    		ON D.NO = DT.PARENT_NO
    WHERE LEVEL < 20
)
SELECT DISTINCT NO
FROM DATA_TREE
-- WHERE PARENT_NO IS NULL;



LIMIT 5 OFFSET 0 ;
OFFSET 0: 1번째 row부터 시작
LIMIT 5: 5개 row 조회


WITH RECURSIVE DATA_TREE(NO, PARENT_NO, NAME LEVEL) AS (
    -- 시작: GROUP_NO = 4인 노드들
    SELECT 
    	DR.NO
    	, DR.PARENT_NO
    	, DR.NAME
    	, 1 AS LEVEL
    FROM BN_DIRECTORY DR
    	INNER JOIN BN_DIRECTORY_ACCESS DA 
    		ON DR.NO = DA.DIR_NO
    WHERE 1=1
    	AND DR.ENABLED = TRUE 
    	AND DA.GROUP_NO = 4

    UNION ALL

    -- 부모 방향으로 계속 올라가기
    SELECT 
    	D.NO
    	, D.PARENT_NO
    	, D.NAME
    	, LEVEL + 1
    FROM BN_DIRECTORY D
    	INNER JOIN DATA_TREE DT 
    		ON D.NO = DT.PARENT_NO
    WHERE LEVEL < 20
)
SELECT DISTINCT NAME
FROM DATA_TREE
-- WHERE PARENT_NO IS NULL;


WITH RECURSIVE DATA_PATH(NO, PARENT_NO, NAME ) AS (
	SELECT 
		D.NO, D.PARENT_NO, D.NAME 
	FROM BN_DIRECTORY D
	
	UNION ALL
	
	SELECT 
		DR.NO, DR.PARENT_NO, DR.NAME 
	FROM BN_DIRECTORY DR 
		INNER JOIN DATA_PATH DP ON DR.PARENT_NO = DP.NO 
	
) SELECT * FROM DATA_PATH

		*/
	}
		
	@Override
	public List<DirectoryDto> getDirectory(UserVo user, String uid) {
		
		final List<DirectoryDto> result =  dirManager.getAccessibleDirectoriesForGroup(user.getGroup().getGroupNo());
		
		if( BurrowUtils.isEmpty(uid) ) {
			log.info("루트 호출");
		}else{
			final DirectoryDto dir = dirManager.getDirectoryForGroup(user.getGroup().getGroupNo(), uid);
			if( dir != null ) {
				dir.setSelected(true);
			}
		}
		
		return result;
	}

	@Override
	public List<String> getPaths(UserVo user, BnDirectory dir) {
		final List<String> result = this.storageDao.getPaths(user, dir);
		return result;
	}
	
	@Override
	public BnDirectory getParentDirectory(UserVo user, String uid) {
		final BnDirectory result = this.storageDao.getParentDirectory(user, uid);
		
		return result;
	}

	@Override
	public List<AbsPath> getFiles(UserVo user, String uid) {
		
		final int groupNo = user.getGroup().getGroupNo();
		final List<AbsPath> result = new ArrayList<>();
		
		final DirectoryDto dir = dirManager.getDirectoryForGroup(groupNo, uid); //this.storageDao.getDirectory(user, uid);
		if( dir == null ) {
			return result;
		}
		
		List<DirectoryDto> directories = dirManager.getAccessibleChildren(groupNo, uid);
		if( BurrowUtils.isNotEmpty(directories) ) {
			for( DirectoryDto dto : directories ) {
				result.add(dto.getOwner());
			}
		}

		final List<BnFile> files = this.storageDao.getFiles(user, dir.getOwner());
		if( BurrowUtils.isNotEmpty(files) )
			result.addAll( files );
		
		return result;
	}
	
	@Override
	public BnFile getFile(UserVo user, String uid) {
		
		final BnFile file = this.storageDao.getFile( user, uid );
		
		return file;
	}
	
	@Override
	public List<BnDirectory> getGroupStorageList(int groupNo) {
		
		return this.storageDao.getRootDirectory( groupNo );
	}
	
}
