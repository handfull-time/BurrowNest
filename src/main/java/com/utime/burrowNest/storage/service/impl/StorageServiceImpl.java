package com.utime.burrowNest.storage.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.utime.burrowNest.storage.dao.StorageDao;
import com.utime.burrowNest.storage.service.StorageService;
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
	
	final static ObjectWriter objMapper = new ObjectMapper().writerWithDefaultPrettyPrinter();
	
	private final UserDao userDao;

	private final StorageDao storageDao;
	
	private Map<String, EBnFileType> mapFileType;
	
	/**
	 * ApplicationReadyEvent
	 */
	@EventListener(ApplicationReadyEvent.class)
	protected void handleApplicationReadyEvent() {
		this.mapFileType = storageDao.getBnFileType();
	}

	@Override
	public byte[] getThumbnail(String fid) {
		return storageDao.getThumbnail( fid );
	}

	@Override
	public DirectoryDto getRootDirectory(UserVo user) {
		final DirectoryDto result = storageDao.getRootDirectory(user);
		
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
    SELECT DR.NO, DR.PARENT_NO, 1 AS LEVEL
    FROM BN_DIRECTORY DR
    INNER JOIN BN_DIRECTORY_ACCESS DA ON DR.NO = DA.DIR_NO
    WHERE DR.ENABLED = TRUE AND DA.GROUP_NO = 4

    UNION ALL

    -- 부모 방향으로 계속 올라가기
    SELECT D.NO, D.PARENT_NO, LEVEL + 1
    FROM BN_DIRECTORY D
    INNER JOIN DATA_TREE DT ON D.NO = DT.PARENT_NO
    WHERE LEVEL < 20
)
SELECT DISTINCT NO
FROM DATA_TREE
-- WHERE PARENT_NO IS NULL;



LIMIT 5 OFFSET 0 ;
OFFSET 0: 1번째 row부터 시작
LIMIT 5: 5개 row 조회

		*/
		return result;
	}

	@Override
	public DirectoryDto getDirectory(UserVo user, String path) {
		final DirectoryDto result = null;
		
		return result;
	}

	@Override
	public List<BnFile> getFiles(UserVo user, BnDirectory dir) {
		// TODO Auto-generated method stub
		return null;
	}
}
