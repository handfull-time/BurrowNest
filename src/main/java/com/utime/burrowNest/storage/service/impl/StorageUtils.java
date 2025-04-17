package com.utime.burrowNest.storage.service.impl;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Timestamp;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ooxml.POIXMLProperties.CoreProperties;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.utime.burrowNest.storage.vo.AbsBnFileInfo;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.BnFile;
import com.utime.burrowNest.storage.vo.BnFileDocument;
import com.utime.burrowNest.storage.vo.BnFileImage;

import lombok.extern.slf4j.Slf4j;

/**
 * 저장소 유틸
 * @author utime
 *
 */
@Slf4j
public class StorageUtils {
	
	public static BnDirectory getDirectoryInfo(File dir) throws Exception{
		
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
        	log.warn("dir 이상함 " + dir );
            throw new IllegalArgumentException("유효하지 않은 디렉토리입니다.");
        }
        
        final BnDirectory result = new BnDirectory();

        result.setHasChild( dir.listFiles(File::isDirectory) != null && dir.listFiles(File::isDirectory).length > 0 );
        result.setName( dir.getName() );
        result.setAbsolutePath( dir.getAbsolutePath() );

        final Path path = dir.toPath();
        try {
        	final BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);

            result.setCreation( new Timestamp(attrs.creationTime().toMillis()) );
            result.setLastModified( new Timestamp(attrs.lastModifiedTime().toMillis()) );

        } catch (Exception e) {
        	log.error("dir 오류", e);
            throw new RuntimeException("디렉토리 속성 읽기 실패: " + dir.getAbsolutePath(), e);
        }
        
        return result;
    }
	
	public static BnFile getFileInfo(File file) throws Exception{
		
	    if (file == null || !file.exists() || file.isDirectory()) {
	    	log.warn("file 이상함 " + file );
            throw new IllegalArgumentException("유효한 파일이 아닙니다.");
	    }
	    
	    final BnFile result = new BnFile();
        result.setFileLength( file.length() );

	    try {
	        // 기본 정보
	        final Path path = file.toPath();
	        final BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            result.setCreation( new Timestamp(attrs.creationTime().toMillis()) );
            result.setLastModified( new Timestamp(attrs.lastModifiedTime().toMillis()) );

	        // 이름 및 확장자
            final String fullName = file.getName();
	        String extension = "";
	        String nameOnly = fullName;

	        final int lastDot = fullName.lastIndexOf('.');
	        if (lastDot != -1 && lastDot != 0 && lastDot < fullName.length() - 1) {
	            extension = fullName.substring(lastDot + 1);
	            nameOnly = fullName.substring(0, lastDot);
	        }
	        
	        result.setName(nameOnly);
	        result.setFullName(fullName);
	        result.setExtension(extension.toLowerCase());

	    } catch (Exception e) {
	    	log.error("file 오류", e);
            throw new RuntimeException("디렉토리 속성 읽기 실패: " + file.getName(), e);
	    }
	    
	    return result;
	}

	private static void procDoc(BnFileDocument doc, CoreProperties props) {
		doc.setTitle( props.getTitle() );
		doc.setSubject( props.getSubject());
		doc.setAuthor(props.getCreator());
		doc.setManager(props.getLastModifiedByUser());
		doc.setCompany(props.getContentType());
		doc.setCategory(props.getCategory());
		doc.setKeywords(props.getKeywords());
		doc.setNotes(props.getDescription());
	}
	
	public static BnFileDocument getFileInfoDocument(File file, BnFile bnFile ) throws Exception {
		
		final BnFileDocument result = new BnFileDocument();
		result.setFileNo(bnFile.getNo());

        // ---------------- OOXML (.docx/.xlsx/.pptx) ----------------
		switch( bnFile.getExtension() ) {
		case "doc":
		case "docx":{
			try (XWPFDocument doc = new XWPFDocument(OPCPackage.open(file))) {
				procDoc(result, doc.getProperties().getCoreProperties() );
            }
			break;
		}
		case "xlsx":{
			try (XSSFWorkbook wb = new XSSFWorkbook(OPCPackage.open(file))) {
				procDoc(result, wb.getProperties().getCoreProperties() );
            }
			break;
		}
		case "ppt":
		case "pptx":{
			try (XMLSlideShow ppt = new XMLSlideShow(OPCPackage.open(file))) {
				procDoc(result, ppt.getProperties().getCoreProperties() );
            }
			break;
		}
		case "xls":{
			try (HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file))) {
                final SummaryInformation si = wb.getSummaryInformation();
                if( si != null ) {
            		result.setTitle( si.getTitle() );
            		result.setSubject( si.getSubject());
            		result.setKeywords(si.getKeywords());
                }
            }
			break;
		}
		case "hwp":{
			break;
		}
		case "pdf":{
			try (PDDocument document = Loader.loadPDF(file)) {
	            final PDDocumentInformation info = document.getDocumentInformation();
	            
	            result.setTitle( info.getTitle() );
	            result.setSubject( info.getSubject());
	            result.setAuthor(info.getCreator());
	            result.setKeywords(info.getKeywords());

	        } catch (IOException e) {
	            log.warn( "PDF 메타데이터 추출 실패: " + e.getMessage());
	        }
			break;
		}
		}// switch end
		
		return result;
	}

	public static AbsBnFileInfo getFileInfoImage(File file, BnFile bnFile) throws Exception{
		final BnFileImage result = new BnFileImage();
		result.setFileNo(bnFile.getNo());

        // ---------------- OOXML (.docx/.xlsx/.pptx) ----------------
		switch( bnFile.getExtension() ) {
		case "jpg":
		case "jpeg":{break;}
		
		}// switch end
		
		return result;
	}

	public static AbsBnFileInfo getFileInfoVideo(File file, BnFile bnFile) throws Exception{
		final BnFileImage result = new BnFileImage();
		result.setFileNo(bnFile.getNo());

        // ---------------- OOXML (.docx/.xlsx/.pptx) ----------------
		switch( bnFile.getExtension() ) {
		case "docx":{break;}
		}// switch end
		
		return result;
	}

	public static AbsBnFileInfo getFileInfoAudio(File file, BnFile bnFile) throws Exception{
		final BnFileImage result = new BnFileImage();
		result.setFileNo(bnFile.getNo());

        // ---------------- OOXML (.docx/.xlsx/.pptx) ----------------
		switch( bnFile.getExtension() ) {
		case "docx":{break;}
		}// switch end
		
		return result;
	}

	public static AbsBnFileInfo getFileInfoArchive(File file, BnFile bnFile) throws Exception{
		final BnFileImage result = new BnFileImage();
		result.setFileNo(bnFile.getNo());

        // ---------------- OOXML (.docx/.xlsx/.pptx) ----------------
		switch( bnFile.getExtension() ) {
		case "docx":{break;}
		}// switch end
		
		return result;
	}

	public static String getFileThumbnail(File file, BnFile bnFile) throws Exception{
		String result = null;
		
		final int width = 320, height = 320;

        // ---------------- OOXML (.docx/.xlsx/.pptx) ----------------
		switch( bnFile.getExtension() ) {
		case "jpg":
        case "jpeg":
        case "png":
        case "bmp":
        case "gif":{
        	BufferedImage sourceImage = ImageIO.read(file);
            BufferedImage thumb = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = thumb.createGraphics();

            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(sourceImage, 0, 0, width, height, null);
            g2d.dispose();
            
            // 이미지 -> Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumb, "jpeg", baos); // "jpeg" 또는 "png" 등
            byte[] imageBytes = baos.toByteArray();

            result = Base64.getEncoder().encodeToString(imageBytes);
            break;
        }
		case "pptx":{
			try (FileInputStream fis = new FileInputStream(file);
		             XMLSlideShow ppt = new XMLSlideShow(fis)) {

		            Dimension pageSize = ppt.getPageSize();
		            XSLFSlide slide = ppt.getSlides().get(0); // 첫 슬라이드

		            BufferedImage img = new BufferedImage(pageSize.width, pageSize.height, BufferedImage.TYPE_INT_ARGB);
		            Graphics2D graphics = img.createGraphics();
		            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		            slide.draw(graphics);
		            graphics.dispose();

		            // 이미지 -> Base64
		            ByteArrayOutputStream baos = new ByteArrayOutputStream();
		            ImageIO.write(img, "jpeg", baos); // "jpeg" 또는 "png" 등
		            byte[] imageBytes = baos.toByteArray();

		            result = Base64.getEncoder().encodeToString(imageBytes);
		        }
			
			break;
		}
		}// switch end
		
		return result;
	}

}
