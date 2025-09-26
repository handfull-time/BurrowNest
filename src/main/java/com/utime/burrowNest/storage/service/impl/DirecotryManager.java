package com.utime.burrowNest.storage.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.storage.dao.StorageDao;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.BnPathAccess;
import com.utime.burrowNest.storage.vo.DirectoryDto;

import lombok.RequiredArgsConstructor;

// 사용자 별로 select 정보를 갖고 있어야 할 듯.
@Component
@RequiredArgsConstructor
public class DirecotryManager {
	
	private final StorageDao storageDao;

	final Map<Long, DirectoryDto> mapDirNoBnDirectory = new HashMap<>();
	final Map<String, DirectoryDto> mapUidBnDirectory = new HashMap<>();
	final Map<Long, BnPathAccess> mapDirNoPathAccess = new HashMap<>();
	final Map<Long, Set<Long>> groupDirectoryAccess = new HashMap<>();
	DirectoryDto root;
	
	/**
	 * ApplicationReadyEvent
	 */
	@EventListener(ApplicationReadyEvent.class)
	protected void handleApplicationReadyEvent() {
		
		this.initDirManager();
	}
	
	public void initDirManager() {
		
		if( ! this.storageDao.IsInit() ) {
			return;
		}
		
		this.clearAllData();
		
		final List<BnDirectory> directorylist = storageDao.getAllDirectory();
		final List<BnPathAccess> accessList = storageDao.getAllDirectoryAccess();
		
		this.loadDirecotryManager(directorylist, accessList);
	}
	
	private void loadDirecotryManager(List<BnDirectory> directorylist, List<BnPathAccess> accessList) {
		
		if( BurrowUtils.isEmpty(directorylist) ) {
			return;
		}
		
		final Map< Long, List<DirectoryDto> > mapListDirectory = new HashMap<>();
		final BnDirectory rootDir = directorylist.remove(0);
//		final BnDirectory rootDir = directorylist.get(0);
		
		this.root = new DirectoryDto( rootDir );
		this.mapDirNoBnDirectory.put(rootDir.getNo(), this.root);
		this.mapUidBnDirectory.put(rootDir.getUid(), this.root);
		
		this.init(directorylist, accessList, mapListDirectory);
		
		this.procChild( this.root, mapListDirectory);
	}
	
	private void procChild( DirectoryDto dir, final Map< Long, List<DirectoryDto> > mapListDirectory ) {
		
		final List<DirectoryDto> child = mapListDirectory.remove( dir.getOwner().getNo() );
		if( child == null || child.isEmpty() )
			return;
		
		dir.getChild().addAll(child);
		
		for( DirectoryDto sub : dir.getChild() ) {
			this.procChild( sub, mapListDirectory);
		}
	}
	
	private void init(List<BnDirectory> directorylist, List<BnPathAccess> accessList, Map< Long, List<DirectoryDto> > mapListDirectory) {
		
		if( directorylist.size() < 1 )
			return;
		
		for( BnPathAccess item : accessList) {
			mapDirNoPathAccess.put(item.getNo(), item);
			
			groupDirectoryAccess.computeIfAbsent(item.getGroupNo(), k -> new HashSet<>()).add(item.getNo());
		}
		
		for( BnDirectory item : directorylist ) {
			final Long dirNo = item.getNo();
			final DirectoryDto dto = new DirectoryDto(item);
			
			this.mapDirNoBnDirectory.put(dirNo, dto);
			this.mapUidBnDirectory.put(item.getUid(), dto);
			
			final Long parentNo = item.getParentNo();
            mapListDirectory.computeIfAbsent(parentNo, k -> new ArrayList<>()).add(dto);
		}
	}
	
	public void clearAllData() {
	    mapDirNoBnDirectory.clear();
	    mapUidBnDirectory.clear();
	    mapDirNoPathAccess.clear();

	    for (Set<Long> dirSet : groupDirectoryAccess.values()) {
	        dirSet.clear();
	    }
	    
	    groupDirectoryAccess.clear();
	}
	
	public List<DirectoryDto> getAccessibleDirectoriesForGroup(long groupNo) {
	    final Set<Long> accessibleDirs = groupDirectoryAccess.getOrDefault(groupNo, Collections.emptySet());
	    final List<DirectoryDto> result = new ArrayList<>();
	    
	    for (Long dirNo : accessibleDirs) {
	        DirectoryDto dir = mapDirNoBnDirectory.get(dirNo);
	        if (dir != null) {
	            result.add(dir);
	        }
	    }
	    
	    return result;
	}
	
	public DirectoryDto getDirectoryForGroup(long groupNo, long dirNo) {
		final Set<Long> accessibleDirs = groupDirectoryAccess.getOrDefault(groupNo, Collections.emptySet());

	    // 현재 디렉터리를 검사
	    DirectoryDto dir = mapDirNoBnDirectory.get(dirNo);
	    if (dir != null && accessibleDirs.contains(dirNo)) {
	        return dir;
	    }

	    // 부모 디렉터리를 재귀적으로 검사
	    return this.findAccessibleParent(dir, accessibleDirs);
	}
	
	public DirectoryDto getDirectoryForGroup(long groupNo, String uid) {
	    final Set<Long> accessibleDirs = groupDirectoryAccess.getOrDefault(groupNo, Collections.emptySet());

	    DirectoryDto dir = mapUidBnDirectory.get(uid);
	    if (dir == null) return null;

	    if (isAccessibleByParentTraversal(dir, accessibleDirs)) {
	        return dir; // ✅ 접근 가능하면 UID에 해당하는 원래 객체를 리턴
	    }

	    return null; // 접근 불가능한 경우 null
	}

	/**
	 * 해당 디렉터리 또는 상위 디렉터리 중 접근 가능한 것이 있는지 확인
	 */
	private boolean isAccessibleByParentTraversal(DirectoryDto dir, Set<Long> accessibleDirs) {
	    while (dir != null) {
	        if (accessibleDirs.contains(dir.getOwner().getNo())) {
	            return true;
	        }
	        dir = mapDirNoBnDirectory.get(dir.getOwner().getParentNo());
	    }
	    return false;
	}
	
	
//	public DirectoryDto getDirectoryForGroup(long groupNo, String uid) {
//		final Set<Long> accessibleDirs = groupDirectoryAccess.getOrDefault(groupNo, Collections.emptySet());
//
//	    // 현재 디렉터리를 검사
//	    DirectoryDto dir = mapUidBnDirectory.get(uid);
//	    if (dir != null && accessibleDirs.contains(dir.getOwner().getNo())) {
//	        return dir;
//	    }
//
//	    // 부모 디렉터리를 재귀적으로 검사
//	    return this.findAccessibleParent(dir, accessibleDirs);
//	}
//
	private DirectoryDto findAccessibleParent(DirectoryDto dir, Set<Long> accessibleDirs) {
	    if (dir == null) {
	        return null;
	    }

	    Long parentNo = dir.getOwner().getParentNo();
	    if (accessibleDirs.contains(parentNo)) {
	        return dir; // 부모 디렉터리가 접근 가능한 경우 현재 디렉터리를 반환
	    }

	    // 부모 디렉터리를 재귀적으로 확인
	    DirectoryDto parentDir = mapDirNoBnDirectory.get(parentNo);
	    return this.findAccessibleParent(parentDir, accessibleDirs);
	}

	/** 새로운 디렉터리를 추가 */
    public void addDirectory(BnDirectory newDir) {
        DirectoryDto newDto = new DirectoryDto(newDir);
        mapDirNoBnDirectory.put(newDir.getNo(), newDto);
        mapUidBnDirectory.put(newDir.getUid(), newDto);

        DirectoryDto parentDto = mapDirNoBnDirectory.get(newDir.getParentNo());
        if (parentDto != null) {
            parentDto.getChild().add(newDto);
            parentDto.getOwner().setHasChild(true); // 부모 디렉터리에 자식이 있다고 표시
        }
    }

    /** 기존 디렉터리 정보 수정 */
    public boolean updateDirectory(BnDirectory updatedDir) {
        DirectoryDto existingDto = mapDirNoBnDirectory.get(updatedDir.getNo());
        if (existingDto == null) return false; // 존재하지 않는 디렉터리는 수정 불가

        existingDto.getOwner().setName(updatedDir.getName());
        existingDto.getOwner().setPublicAccessible(updatedDir.isPublicAccessible());
        existingDto.getOwner().setLastModified(updatedDir.getLastModified());
        existingDto.getOwner().setAbsolutePath(updatedDir.getAbsolutePath());
        return true;
    }

    /** 디렉터리 삭제 */
//    public boolean removeDirectory(Long dirNo) {
//        DirectoryDto targetDto = mapDirNoBnDirectory.get(dirNo);
//        if (targetDto == null) return false; // 존재하지 않는 디렉터리는 삭제 불가
//
//        // 부모에서 자식 목록에서 제거
//        DirectoryDto parentDto = mapDirNoBnDirectory.get(targetDto.getOwner().getParentNo());
//        if (parentDto != null) {
//            parentDto.getChild().remove(targetDto);
//            if (parentDto.getChild().isEmpty()) parentDto.getOwner().setHasChild(false); // 부모가 자식이 없으면 표시 변경
//        }
//
//        // 내부 맵에서 삭제
//        mapDirNoBnDirectory.remove(dirNo);
//        mapUidBnDirectory.remove(targetDto.getOwner().getUid());
//
//        return true;
//    }
//    
    public boolean removeDirectory(Long dirNo) {
        DirectoryDto targetDto = mapDirNoBnDirectory.get(dirNo);
        if (targetDto == null) return false; // 존재하지 않는 디렉터리는 삭제 불가

        // 먼저 모든 자식 디렉터리를 삭제 (재귀 호출)
        for (DirectoryDto child : new ArrayList<>(targetDto.getChild())) { // 복사하여 안전하게 반복
            removeDirectory(child.getOwner().getNo());
        }

        // 부모에서 자식 목록에서 제거
        DirectoryDto parentDto = mapDirNoBnDirectory.get(targetDto.getOwner().getParentNo());
        if (parentDto != null) {
            parentDto.getChild().remove(targetDto);
            if (parentDto.getChild().isEmpty()) parentDto.getOwner().setHasChild(false); // 부모가 자식이 없으면 표시 변경
        }

        // 내부 맵에서 삭제
        mapDirNoBnDirectory.remove(dirNo);
        mapUidBnDirectory.remove(targetDto.getOwner().getUid());

        return true;
    }

    public List<String> getPaths(long dirNo) {
        List<String> paths = new ArrayList<>();
        DirectoryDto current = mapDirNoBnDirectory.get(dirNo);

        while (current != null) {
            paths.add(current.getOwner().getName());
            current = mapDirNoBnDirectory.get(current.getOwner().getParentNo());
        }

        Collections.reverse(paths); // 부모부터 정렬
        return paths;
    }
    
    /**
     * 자식 목록 조회
     * @param groupNo
     * @param parentUid
     * @return
     */
    public List<DirectoryDto> getAccessibleChildren(long groupNo, String parentUid) {
        final Set<Long> accessibleDirs = groupDirectoryAccess.getOrDefault(groupNo, Collections.emptySet());

        final DirectoryDto parent = mapUidBnDirectory.get(parentUid);
        if (parent == null) {
            return Collections.emptyList();
        }

        // ✅ 상위 디렉터리를 따라가며 접근 가능한 디렉터리가 있는지 확인
        if (!isAccessibleByParentTraversal(parent, accessibleDirs)) {
            return Collections.emptyList();
        }

        final List<DirectoryDto> result = new ArrayList<>();
        result.addAll(parent.getChild());
        
        return result;
    }

}
