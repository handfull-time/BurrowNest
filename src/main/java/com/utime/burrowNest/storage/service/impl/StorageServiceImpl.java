package com.utime.burrowNest.storage.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.storage.dao.StorageDao;
import com.utime.burrowNest.storage.dto.ChildrenResponse;
import com.utime.burrowNest.storage.dto.DirContextResponse;
import com.utime.burrowNest.storage.dto.DirNodeDto;
import com.utime.burrowNest.storage.mapper.DirectoryMapper;
import com.utime.burrowNest.storage.mapper.row.DirNodeRow;
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
	
	private final ExecutorService executorThumbnail = Executors.newSingleThreadExecutor();
	
	/**
	 * ApplicationReadyEvent
	 */
	@EventListener(ApplicationReadyEvent.class)
	protected void handleApplicationReadyEvent() {
		this.mapFileType = storageDao.getBnFileType();
	}
	
	@EventListener(ContextClosedEvent.class)
	protected void onShutdown() {
		executorThumbnail.shutdown();
		
		try {
			executorThumbnail.awaitTermination(10, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			log.error("", e);
		}
		
		if( ! executorThumbnail.isShutdown() ) {
			executorThumbnail.shutdownNow();
		}
    }
	
	/**
	 * 기본 관리자 계정의 최상위 Root를 생성한다.
	 */
	@Override
	public ReturnBasic saveRootStorage(UserVo user) {
		final ReturnBasic result = new ReturnBasic();
		
		try {
			// 관련 테이블 생성
			this.storageDao.initStorageTable();
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", "기본 Storage Table 생성 실패");
			return result;
		}

		try {
			storageDao.addRootDirectory(user);
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", "루트 생성 실패");
		}
		return result;
	}
	
	@Override
	public byte[] getThumbnail(UserVo user, String uid) {
		
		final byte[] result = storageDao.getThumbnail( uid );
		
		if( result == null ) {
			executorThumbnail.execute( () -> {
				final BnFile file = storageDao.getFile(user, uid);
				if( file != null ) {
					
				}
			});
		}
				
		return result;
	}

//	@Override
	public List<DirectoryDto> getRootDirectory(UserVo user) {
		List<DirectoryDto> result = new ArrayList<>();
		List<BnDirectory> list = storageDao.getAdminRootStorage();
		for( BnDirectory item : list ) {
			final DirectoryDto add = new DirectoryDto(item);
			result.add(add);
		}
		
		return result; //dirManager.getAccessibleDirectoriesForGroup(user.getGroup().getGroupNo());
		
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
		, ENABLED BOOLEAN DEFAULT FALSE NOT NULL -- 사용 여부. T:사용, F:미사용
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
		
		if( BurrowUtils.isEmpty(uid) ) {
			log.info("루트 호출");
			this.storageDao.getRootDirectory( user.getGroup().getGroupNo() );	
		}
		
		
		List<DirectoryDto> result = this.getRootDirectory(null);//  dirManager.getAccessibleDirectoriesForGroup(user.getGroup().getGroupNo());
//		
//		if( BurrowUtils.isEmpty(uid) ) {
//			log.info("루트 호출");
//		}else{
//			final DirectoryDto dir = dirManager.getDirectoryForGroup(user.getGroup().getGroupNo(), uid);
//			if( dir != null ) {
//				dir.setSelected(true);
//			}
//		}
		
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
		
		final long groupNo = user.getGroup().getGroupNo();
		final List<AbsPath> result = new ArrayList<>();
		
//		final DirectoryDto dir = dirManager.getDirectoryForGroup(groupNo, uid); //this.storageDao.getDirectory(user, uid);
//		if( dir == null ) {
//			return result;
//		}
//		
//		List<DirectoryDto> directories = dirManager.getAccessibleChildren(groupNo, uid);
//		if( BurrowUtils.isNotEmpty(directories) ) {
//			for( DirectoryDto dto : directories ) {
//				result.add(dto.getOwner());
//			}
//		}
//
//		final List<BnFile> files = this.storageDao.getFiles(user, dir.getOwner());
//		if( BurrowUtils.isNotEmpty(files) )
//			result.addAll( files );
		
		return result;
	}
	
	@Override
	public BnFile getFile(UserVo user, String uid) {
		
		final BnFile file = this.storageDao.getFile( user, uid );
		
		return file;
	}
	
	@Override
	public List<BnDirectory> getGroupStorageList(long groupNo) {
		
		return this.storageDao.getRootDirectory( groupNo );
	}
	
	@Override
	public List<BnDirectory> getAdminRootStorage() {
		return this.storageDao.getAdminRootStorage();
	}
	
	@Autowired
	DirectoryMapper directoryMapper;
	
	@Override
    public DirContextResponse buildContext(UserVo user, String uid) {
        // 1) 타깃 노드 결정: uid 없으면 사용자 접근 가능한 루트, 있으면 해당 uid
        DirNodeRow node = (uid == null || uid.isBlank())
                ? directoryMapper.selectDefaultRootForUser(user.getUserNo()) // 없으면 시스템 루트 반환하도록 쿼리 설계
                : directoryMapper.selectByUid(uid);

        if (node == null) return null;

        
        final long nodeNo = node.getNo();
        
        // 2) 조상(루트→부모) 목록
        List<DirNodeRow> ancestors = directoryMapper.selectAncestorsByNo(nodeNo);

        // 3) 현재 노드 유효 권한 플래그 (Allow 상속 OR-집계)
        Integer effectiveFlags = directoryMapper.selectEffectiveFlags(user.getUserNo(), nodeNo);
        if (effectiveFlags == null) effectiveFlags = 0;

        // 4) 자식 목록(읽기 권한 있는 것만) + hasChild 계산 포함
        List<DirNodeRow> children = directoryMapper.selectChildrenReadable(user.getUserNo(), nodeNo);

        // 5) DTO 변환
        DirContextResponse resp = new DirContextResponse();
        resp.setNode(toDto(node, true));
        resp.setAncestors(ancestors.stream().map(r -> toDto(r, false)).toList());
        resp.setChildren(children.stream().map(r -> toDto(r, false)).toList());
        resp.setBreadcrumbs(buildBreadcrumbs(ancestors, node));
        resp.setEffectiveFlags(effectiveFlags);
        return resp;
    }

    private DirNodeDto toDto(DirNodeRow r, boolean selected) {
        DirNodeDto dto = new DirNodeDto();
        dto.setOwner(r.getUid(), r.getName());
        dto.setHasChild(r.isHasChild());
//        dto.setEffectiveFlags(null); // 필요시 개별 노드에 표시
        dto.setSelected(selected);
        dto.setChild(List.of());     // 지연로딩 기본 비움
        return dto;
    }

    private List<String> buildBreadcrumbs(List<DirNodeRow> ancestors, DirNodeRow node) {
        List<String> names = new ArrayList<>();
        for (DirNodeRow a : ancestors) names.add(a.getName());
        names.add(node.getName());
        return names;
    }
	
    @Override
    @Transactional(readOnly = true)
    public List<DirNodeDto> findAllowedRoots(UserVo user) {
        List<DirNodeRow> rows = directoryMapper.selectAllowedRoots(user.getUserNo());
        return rows.stream()
                .map(r -> {
                    DirNodeDto dto = new DirNodeDto();
                    dto.setOwner(r.getUid(), r.getName());
                    dto.setHasChild(r.isHasChild());
//                    dto.setEffectiveFlags(null);     // 원하면 집계 플래그 넣어도 됨
                    return dto;
                })
                .toList();
    }

	
	@Override
	public ChildrenResponse findChildren(UserVo user, String uid, int page, int size, Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BnDirectory getAdminTopStorage() {
		return storageDao.getRootDirectory();
	}
	@Override
	public List<BnDirectory> getGroupStorageList(long groupNo, long dirNo) {
		List<BnDirectory> result = storageDao.getGroupStorageList( groupNo, dirNo );
		return result;
	}
	
	@Override
	public ReturnBasic removeGroupStorage(long groupNo, long dirNo) {
		final ReturnBasic result = new ReturnBasic();
		
		try {
			storageDao.removeGroupStorage( groupNo, dirNo );
		} catch (Exception e) {
			result.setCodeMessage("E", e.getMessage());
		}
		
		return result;
	}
}
