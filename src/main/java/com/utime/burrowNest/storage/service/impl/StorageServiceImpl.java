package com.utime.burrowNest.storage.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.common.util.FileUtils;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.root.service.LoadStorageService;
import com.utime.burrowNest.storage.dao.StorageDao;
import com.utime.burrowNest.storage.service.StorageService;
import com.utime.burrowNest.storage.util.StorageUtils;
import com.utime.burrowNest.storage.vo.AbsPath;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.BnFile;
import com.utime.burrowNest.storage.vo.EStorageIOMode;
import com.utime.burrowNest.storage.vo.PasteItem;
import com.utime.burrowNest.storage.vo.RenameItem;
import com.utime.burrowNest.storage.vo.StorageIOItem;
import com.utime.burrowNest.user.vo.UserVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class StorageServiceImpl implements StorageService {
	
	private final StorageDao storageDao;
	
	private final LoadStorageService loadStorageService;
	
//	private Map<String, EBnFileType> mapFileType;
//	
//	/**
//	 * ApplicationReadyEvent
//	 */
//	@EventListener(ApplicationReadyEvent.class)
//	protected void handleApplicationReadyEvent() {
//		this.mapFileType = storageDao.getBnFileType();
//	}
	
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
				
		return result;
	}

//	@Override
//	public List<DirectoryDto> getRootDirectory(UserVo user) {
//		List<DirectoryDto> result = new ArrayList<>();
//		List<BnDirectory> list = storageDao.getAdminRootStorage();
//		for( BnDirectory item : list ) {
//			final DirectoryDto add = new DirectoryDto(item);
//			result.add(add);
//		}
//		
//		return result; 
//	}
	
	@Override
	public BnDirectory getParentDirectory(UserVo user, String uid) {
		final BnDirectory result = this.storageDao.getParentDirectory(user, uid);
		
		return result;
	}

	@Override
	public List<AbsPath> getFiles(UserVo user, String uid) {
		
		final long groupNo = user.getGroup().getGroupNo();
		final List<AbsPath> result = new ArrayList<>();
		
		// groupNo, directoryUid 로 Directory 목록 조회
		final List<BnDirectory> dirList = this.storageDao.getDirectories( groupNo, uid );
		this.existDirecotryList(user, uid, dirList);
		result.addAll( dirList );
		
		// groupNo, directoryUid 로 파일 목록 조회
		final List<BnFile> fileList = this.storageDao.getFiles( groupNo, uid );
		this.existFileList( user, fileList );
		result.addAll( fileList );
		
		return result;
	}
	
	@Override
	public BnFile getFile(UserVo user, String uid) {
		
		final BnFile file = this.storageDao.getFile( user, uid );
		
		return file;
	}
	
	private void existDirecotryList(UserVo user, String parentUid, List<BnDirectory> list) {
		// If list is null, nothing to compare against; attempt to resolve parent directory
		BnDirectory currentDir = null;
		if (list != null && !list.isEmpty()) {
			currentDir = list.get(0);
		} else if (!BurrowUtils.isEmpty(parentUid)) {
			currentDir = storageDao.getDirectory(user, parentUid);
		}
		
		if (currentDir == null) {
			// No parent information available; nothing we can do safely.
			return;
		}
		
		final String currentPath = currentDir.getAbsolutePath();
		if (BurrowUtils.isEmpty(currentPath)) {
			return;
		}
		
		final File parentFile = new File(currentPath).getParentFile();
		if (!parentFile.exists() || !parentFile.isDirectory()) {
			// Parent no longer exists on disk; remove all DB entries in the list
			if (list == null) return;
			for (int i = list.size() - 1; i >= 0; i--) {
				final BnDirectory item = list.get(i);
				try {
					storageDao.deleteDirectory(item);
					list.remove(i);
				} catch (Exception e) {
					log.error("Dir 삭제 실패:" + item.getNo(), e);
				}
			}
			return;
		}
		
		// Build a set of names present in DB
		final Set<String> dbNames = new HashSet<>();
		if (list != null) {
			for (BnDirectory d : list) {
				if (!BurrowUtils.isEmpty(d.getName())) dbNames.add(d.getName());
			}
		}
		
		// Scan filesystem for directories
		final File[] children = parentFile.listFiles(file -> file.isDirectory());
		if (children == null) {
			return;
		}
		
//		// For directories missing in DB, persist them and add to list
//		for (File f : children) {
//			final String name = f.getName();
//			if (!dbNames.contains(name)) {
//				try {
//					final BnDirectory childDir = StorageUtils.getDirectoryInfo(f);
//					childDir.setEnabled(true);
//					childDir.setPublicAccessible(true);
//					childDir.setParentNo(currentDir.getNo());
//					childDir.setOwnerNo(user.getUserNo());
//					try {
//						if (storageDao.saveDirectory(childDir, user) < 1) {
//							log.warn("Dir 저장 실패: " + childDir);
//						} else {
//							if (list != null) list.add(childDir);
//						}
//					} catch (Exception e) {
//						log.error("디렉토리 저장 실패: " + f.getAbsolutePath(), e);
//					}
//				} catch (Exception e) {
//					log.error("디렉토리 정보 생성 실패: " + f.getAbsolutePath(), e);
//				}
//			}
//		}
		
		// Now remove DB entries that no longer have physical directories (original behavior)
		if (list == null) return;
		for (int index = list.size() - 1; index >= 0; index--) {
			final BnDirectory item = list.get(index);
			if (BurrowUtils.isEmpty(parentFile.getAbsolutePath())) continue;
			final File d = new File(parentFile.getAbsolutePath(), item.getName());
			if (!d.exists()) {
				try {
					storageDao.deleteDirectory(item);
					list.remove(index);
				} catch (Exception e) {
					log.error("Dir 삭제 실패:" + item.getNo(), e);
				}
			}
		}
	}
	
	/**
	 * 물리적 객체 존재 여부 확인
	 */
	private void existFileList( UserVo user, List<BnFile> list ) {
		
		if( BurrowUtils.isEmpty(list) ) {
			return;
		}
		
		BnFile item = list.get(0);
		final long parentDirNo = item.getParentNo();
		
		final File dir = new File( item.getDirectoryPath() );
		final File [] files = dir.listFiles( new java.io.FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile();
			}
		});
		
		final Map<String, File> fileMap = new HashMap<>();
		for( File f : files ) {
			fileMap.put(f.getName(), f);
		}
		
		for( int index=list.size()-1 ; index>=0 ; index-- ) {
			
			item = list.get(index);
			
			final File f = new File( item.getDirectoryPath(), item.getFullName() );
			if( ! f.exists() ) {
				try {
					storageDao.deleteFile(item);
					list.remove(index);
				} catch (Exception e) {
					log.error("File 삭제 실패:" + item.getNo(), e);
				}
			}else {
				fileMap.remove( item.getFullName() );
			}
		}
		
		for( String fileName : fileMap.keySet() ) {
			this.loadStorageService.saveFileStorage(parentDirNo, user, fileMap.get(fileName) );
		}
	}

	@Override
	public List<BnDirectory> getGroupStorageList(UserVo user, String uid) {
		
		List<BnDirectory> result;
		
		if( BurrowUtils.isEmpty(uid) ) {
			log.info("루트 호출");
			result = this.storageDao.getRootDirectory( user.getGroup().getGroupNo() );
		}else {
			result = storageDao.getGroupStorageList( user.getGroup().getGroupNo(), uid );
		}
		
		this.existDirecotryList(user, uid, result);
		
		return result;
	}
	
	@Override
	public List<BnDirectory> getParentDirectoryList(String uid) {
		
		List<BnDirectory> result;
		if( BurrowUtils.isEmpty(uid) ) {
			result = new ArrayList<>();
		}else {
			result = storageDao.getParentDirectoryList(uid);
		}
		
		return result;
	}

	@Override
	public ReturnBasic pasteStorage(UserVo user, PasteItem pasteItem) {
		
		final ReturnBasic result = new ReturnBasic();
		
		final BnDirectory dirTarget = storageDao.getDirectory( user, pasteItem.getTarget() );
		if( dirTarget == null ) {
			result.setCodeMessage("E", "존재하지 않는 디렉토리입니다.");
			return result;
		}
		
		if( pasteItem.getMode() == EStorageIOMode.Copy ) {
			// 복사
			for( StorageIOItem item : pasteItem.getList() ) {
				this.copyStorage( user, dirTarget, item);
			}
		}else {
			// 이동
			for( StorageIOItem item : pasteItem.getList() ) {
				this.moveStorage( user, dirTarget, item);
			}
		}
		
		return result;
	}

	private void moveStorage(UserVo user, BnDirectory dirTarget, StorageIOItem item) {
		
		AbsPath pathItem = storageDao.selectStorageItem(user, item);
		if( pathItem == null ) {
			log.warn("pathItem is null. {}", item.toString());
			return;
		}
		
		final Path target = Paths.get(dirTarget.getAbsolutePath()).resolve(dirTarget.getName()).normalize();
		
		final Path source;
		if( pathItem.isFile() ) {
			final BnFile f = (BnFile)pathItem;
			source = Paths.get(f.getDirectoryPath()).resolve(f.getName()).normalize();
		}else {
			final BnDirectory d = (BnDirectory)pathItem; 
			source = Paths.get(d.getAbsolutePath()).resolve(d.getName()).normalize();
		}

		try {
			if( item.isFile() ) {
				Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
			}else {
				FileUtils.moveDirectory(source, target);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Storage 복사
	 */
	private void copyStorage(UserVo user, BnDirectory dirTarget, StorageIOItem item) {
		AbsPath pathItem = storageDao.selectStorageItem(user, item);
		if( pathItem == null ) {
			log.warn("pathItem is null. {}", item.toString());
			return;
		}
		
		final Path target = Paths.get(dirTarget.getAbsolutePath()).resolve(dirTarget.getName()).normalize();
		
		final Path source;
		if( pathItem.isFile() ) {
			final BnFile f = (BnFile)pathItem;
			source = Paths.get(f.getDirectoryPath()).resolve(f.getName()).normalize();
		}else {
			final BnDirectory d = (BnDirectory)pathItem; 
			source = Paths.get(d.getAbsolutePath()).resolve(d.getName()).normalize();
		}

		try {
			if( item.isFile() ) {
				Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
				try {
					final BnFile f = (BnFile)pathItem;
					f.setParentNo(dirTarget.getNo());

					this.storageDao.copyFile(f, user);
				} catch (Exception e) {
					log.error("파일 복사 오류", e);
				}
			} else {
			    	
		        if (!Files.exists(source) || !Files.isDirectory(source)) {
		            throw new IllegalArgumentException("Source must be an existing directory: " + source);
		        }
		        
		        if (target.startsWith(source)) {
		            throw new IllegalArgumentException("targetDir must not be inside sourceDir");
		        }

		        // 디렉토리 복사 수행
		        Files.walkFileTree(source, new SimpleFileVisitor<>() {
			        
		            @Override
		            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		                Path relative = source.relativize(dir);
		                Path targetPath = target.resolve(relative);
		                if (!Files.exists(targetPath)) {
		                    Files.createDirectories(targetPath);
		                }
		                return FileVisitResult.CONTINUE;
		            }

		            @Override
		            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		                Path relative = source.relativize(file);
		                Path targetPath = target.resolve(relative);
		                
		                Files.copy(file, targetPath, StandardCopyOption.REPLACE_EXISTING);
		                return FileVisitResult.CONTINUE;
		            }
		        });
			}
		} catch (IOException e) {
			log.error("파일 복사 Exception", e);
		}
	}

	@Override
	public ReturnBasic deleteStorage(UserVo user, List<StorageIOItem> delItems) {
		
		final ReturnBasic result = new ReturnBasic();
		
		final List<AbsPath> list = storageDao.selectStorageItems(user, delItems);
		for( AbsPath item : list ) {
			if( item instanceof BnDirectory ) {
				BnDirectory dir = (BnDirectory)item;
				if( FileUtils.deleteDirectory( new File( dir.getAbsolutePath() ).toPath() ) ) {
					log.info("디렉토리 삭제 성공: " + dir.getAbsolutePath());
					try {
						storageDao.deleteDirectory(dir);
					} catch (Exception e) {
						log.error("디렉토리 삭제 DB 반영 실패: " + dir.getAbsolutePath(), e);
						result.setCodeMessage("E", "디렉토리 삭제 DB 반영 실패: " + dir.getAbsolutePath());
					}
				}else {
					log.error("디렉토리 삭제 실패: " + dir.getAbsolutePath());
					result.setCodeMessage("E", "디렉토리 삭제 실패: " + dir.getAbsolutePath());
				}
			}else if( item instanceof BnFile ) {
				BnFile file = (BnFile)item;
				if( FileUtils.deleteFile( new File( file.getDirectoryPath(), file.getFullName() ) ) ) {
					log.info("파일 삭제 성공: " + file.getDirectoryPath() + File.separator + file.getFullName());
					try {
						storageDao.deleteFile(file);
					} catch (Exception e) {
						log.error("파일 삭제 DB 반영 실패: " + file.getDirectoryPath() + File.separator + file.getFullName(), e);
						result.setCodeMessage("E", "파일 삭제 DB 반영 실패: " + file.getDirectoryPath() + File.separator + file.getFullName());
					}
				}else {
					log.error("파일 삭제 실패: " + file.getDirectoryPath() + File.separator + file.getFullName());
					result.setCodeMessage("E", "파일 삭제 실패: " + file.getDirectoryPath() + File.separator + file.getFullName());
				}
			}
		}
		
		return result;
	}

	@Override
	public ReturnBasic renameStorage(UserVo user, RenameItem renameItem) {
		
		final ReturnBasic result = new ReturnBasic();

		if( renameItem.isFile() ) {
			final BnFile file = storageDao.getFile( user, renameItem.getUid() );
			if( file == null ) {
				result.setCodeMessage("E", "존재하지 않는 파일입니다.");
				return result;
			}
			
			final File oldFile = new File( file.getDirectoryPath(), file.getFullName() );
			final File newFile = new File( file.getDirectoryPath(), renameItem.getName() );
			
			if( oldFile.renameTo( newFile ) ) {
				log.info("파일명 변경 성공: {} -> {}", oldFile.getName(), newFile.getName());
				
				file.setName(newFile.getName());
				
				try {
					storageDao.updateRename(file);
				} catch (Exception e) {
					log.error("파일명 변경 DB 반영 실패: {} -> {}", oldFile.getName(), newFile.getName(), e);
					// 파일명 원복
					if( newFile.renameTo( oldFile ) ) {
						log.info("파일명 원복 성공: {} -> {}", newFile.getName(), oldFile.getName());
					}else {
						log.error("파일명 원복 실패: {} -> {}", newFile.getName(), oldFile.getName());
					}
				}
				
			}else {
				log.error("파일명 변경 실패: {} -> {}", oldFile.getName(), newFile.getName());
			}
			
		} else {
			final BnDirectory dir = storageDao.getDirectory( user, renameItem.getUid() );
			if( dir == null ) {
				result.setCodeMessage("E", "존재하지 않는 디렉토리입니다.");
				return result;
			}

			final File oldDir = new File( dir.getAbsolutePath() );
			final File newDir = new File( oldDir.getParent(), renameItem.getName() );
			
			if( oldDir.renameTo( newDir ) ) {
				log.info("디렉토리명 변경 성공: {} -> {}", oldDir.getName(), newDir.getName());
				dir.setName( renameItem.getName() );
				try {
					storageDao.updateRename(dir);
				} catch (Exception e) {
					log.error("디렉토리명 변경 DB 반영 실패: {} -> {}", oldDir.getName(), newDir.getName(), e);
					// 파일명 원복
					if( newDir.renameTo( oldDir ) ) {
						log.info("디렉토리명 원복 성공: {} -> {}", newDir.getName(), oldDir.getName());
					}else {
						log.error("디렉토리명 원복 실패: {} -> {}", newDir.getName(), oldDir.getName());
					}
				}
			}else {
				log.error("디렉토리명 변경 실패: {} -> {}", oldDir.getName(), newDir.getName());
			}
		}
		
		return result;
	}

	@Override
	public ReturnBasic newFolderStorage(UserVo user, RenameItem newFolderItem) {
		
		final ReturnBasic result = new ReturnBasic();

		final BnDirectory parentDir = storageDao.getParentDirectory(user, newFolderItem.getUid());
		if( parentDir == null ) {
			result.setCodeMessage("E", "존재하지 않는 폴더입니다.");
			return result;
		}
		
		final File newDir = new File( parentDir.getAbsolutePath(), newFolderItem.getName() );
		if( newDir.exists() ) {
			result.setCodeMessage("E", "이미 존재하는 폴더명입니다.");
			return result;
		}
		
		if( newDir.mkdir() ) {
			final BnDirectory childDir;
			
			try {
				childDir = StorageUtils.getDirectoryInfo(newDir);
				childDir.setEnabled(true);
				childDir.setPublicAccessible(true);
				childDir.setParentNo( parentDir.getNo() );
				childDir.setOwnerNo( user.getUserNo() );
				if( storageDao.saveDirectory( childDir, user ) < 1 ) {
					log.warn("Dir 저장 실패: " + childDir);
				};
				
			} catch (Exception e) {
				log.error("", e);
				result.setCodeMessage("E", "폴더 정보 저장에 실패했습니다.");
			}
		}else {
			result.setCodeMessage("E", "폴더 생성에 실패했습니다.");
		}
		
		return result;
	}
	
	
}
