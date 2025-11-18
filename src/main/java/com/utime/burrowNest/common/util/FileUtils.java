package com.utime.burrowNest.common.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils {

    /**
     * 디렉토리 복사
     *
     * @param sourceDir 복사할 디렉토리
     * @param targetDir 대상 디렉토리
     * @throws IOException 예외 발생 시
     */
    public static void copyDirectory(Path sourceDir, Path targetDir) throws IOException {
    	
        if (!Files.exists(sourceDir) || !Files.isDirectory(sourceDir)) {
            throw new IllegalArgumentException("Source must be an existing directory: " + sourceDir);
        }
        
        if (targetDir.startsWith(sourceDir)) {
            throw new IllegalArgumentException("targetDir must not be inside sourceDir");
        }

        // 디렉토리 복사 수행
        Files.walkFileTree(sourceDir, new SimpleFileVisitor<>() {
        	
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path relative = sourceDir.relativize(dir);
                Path targetPath = targetDir.resolve(relative);
                if (!Files.exists(targetPath)) {
                    Files.createDirectories(targetPath);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relative = sourceDir.relativize(file);
                Path targetPath = targetDir.resolve(relative);
                
                Files.copy(file, targetPath, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    // Path 지원 버전
    public static void copyDirectory(String source, String target) throws IOException {
    	FileUtils.copyDirectory(Paths.get(source), Paths.get(target));
    }
    
    /**
     * 디렉토리 이동
     *
     * @param sourceDir 복사할 디렉토리
     * @param targetDir 대상 디렉토리
     * @throws IOException 예외 발생 시
     */
    public static void moveDirectory(Path sourceDir, Path targetDir) throws IOException {
    	
        if (!Files.exists(sourceDir) || !Files.isDirectory(sourceDir)) {
            throw new IllegalArgumentException("Source must be an existing directory: " + sourceDir);
        }
        
        if (targetDir.startsWith(sourceDir)) {
            throw new IllegalArgumentException("targetDir must not be inside sourceDir");
        }

        // 디렉토리 복사 수행
        Files.walkFileTree(sourceDir, new SimpleFileVisitor<>() {
        	
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path relative = sourceDir.relativize(dir);
                Path targetPath = targetDir.resolve(relative);
                
                if (!Files.exists(targetPath)) {
                    Files.createDirectories(targetPath);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relative = sourceDir.relativize(file);
                Path targetPath = targetDir.resolve(relative);
                
                Files.move(file, targetPath, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                // 하위 파일/폴더 이동이 끝난 후 원본 디렉토리 삭제
                if (exc != null) {
                    throw exc;
                }
                Files.delete(dir); // 최종적으로 sourceDir 자체도 여기서 삭제됨
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
    /**
	 * 디렉토리 삭제
	 * @param dir 삭제할 디렉토리
	 * @throws IOException 예외 발생 시
	 */
    public static boolean deleteDirectory(Path dir) {
        
    	if (!Files.exists(dir) || !Files.isDirectory(dir)) {
			return false;
		}
        
        try {
        	Files.walkFileTree(dir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (FileSystemException e) {
        	log.error("파일 점유 중: " + e.getFile(), e);
        } catch (IOException e) {
        	log.error("기타 삭제 오류", e);
        }
        return  ! Files.exists(dir);
   }

    /**
	 * 파일 삭제
	 */
	public static boolean deleteFile(File file) {
		if( file == null || ! file.exists() ) {
			return false;
		}
		
		try {
		    Files.delete(file.toPath());
		    return true;

		} catch (FileSystemException e) {
		    log.error("파일이 다른 프로세스에서 사용 중: " + e.getMessage());
		    return false;

		} catch (IOException e) {
			log.error("삭제 실패: " + e.getMessage());
		    return false;
		}

	}

}
