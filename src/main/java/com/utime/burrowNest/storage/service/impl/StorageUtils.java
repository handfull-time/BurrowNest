package com.utime.burrowNest.storage.service.impl;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.common.util.CommandUtil;
import com.utime.burrowNest.storage.vo.AbsBnFileInfo;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.BnFile;
import com.utime.burrowNest.storage.vo.BnFileArchive;
import com.utime.burrowNest.storage.vo.BnFileAudio;
import com.utime.burrowNest.storage.vo.BnFileDocument;
import com.utime.burrowNest.storage.vo.BnFileImage;
import com.utime.burrowNest.storage.vo.BnFileVideo;

import lombok.extern.slf4j.Slf4j;

/**
 * 저장소 유틸
 * @author utime
 *
 */
@Slf4j
public class StorageUtils {
	
	private final static int width = 320, height = 320;
	private final static String ThumFormat = "jpeg";
	
	//exiftool -Model -Make -FNumber -ShutterSpeedValue -ISO -SubjectDistance -DateTimeOriginal -ImageWidth -ImageHeight -GPSLatitude -GPSLongitude -Flash -WhiteBalance -SceneCaptureType -Software 파일명
	//exiftool -b -ThumbnailImage 20250128_010059.cr2 > ./thumb/20250128.jpg
	
	//  exiftool -ThumbnailImage -b 20190417_123809.jpg | base64 
	// /9j/2wCEAAUDBAQEAwUEBAQFBQUGB .... 여러 라인
	
	// exiftool -ThumbnailImage -b 20190417_123809.jpg | xxd -p | tr -d '\n'


/* 
exiftool -list 20190417_123809.jpg

ExifTool Version Number         : 12.16
File Name                       : 20190417_123809.jpg
Directory                       : .
File Size                       : 5.0 MiB
File Modification Date/Time     : 2025:04:19 21:12:50+09:00
File Access Date/Time           : 2025:04:19 21:12:48+09:00
File Inode Change Date/Time     : 2025:04:19 21:12:50+09:00
File Permissions                : rw-r--r--
File Type                       : JPEG
File Type Extension             : jpg
MIME Type                       : image/jpeg
Exif Byte Order                 : Big-endian (Motorola, MM)
Camera Model Name               : LM-G710N
Orientation                     : Horizontal (normal)
Modify Date                     : 2019:04:17 12:38:09
Y Cb Cr Positioning             : Centered
ISO                             : 50
Exposure Program                : Program AE
F Number                        : 1.6
Exposure Time                   : 1/1389
Sensing Method                  : Not defined
Sub Sec Time Digitized          : 567906
Sub Sec Time Original           : 567906
Sub Sec Time                    : 567906
Focal Length                    : 4.0 mm
Flash                           : Off, Did not fire
Light Source                    : Unknown
Metering Mode                   : Center-weighted average
Scene Capture Type              : Standard
Interoperability Index          : R98 - DCF basic file (sRGB)
Interoperability Version        : 0100
Max Aperture Value              : 1.6
Create Date                     : 2019:04:17 12:38:09
Exposure Compensation           : 0
Exif Image Height               : 2620
White Balance                   : Auto
Date/Time Original              : 2019:04:17 12:38:09
Brightness Value                : 6.54
Exif Image Width                : 4656
Exposure Mode                   : Auto
Aperture Value                  : 1.6
Components Configuration        : Y, Cb, Cr, -
Color Space                     : sRGB
Scene Type                      : Directly photographed
Shutter Speed Value             : 1/1388
Exif Version                    : 0220
Flashpix Version                : 0100
Resolution Unit                 : inches
GPS Latitude Ref                : North
GPS Longitude Ref               : East
GPS Altitude Ref                : Above Sea Level
GPS Time Stamp                  : 03:38:09
GPS Processing Method           : GPS
GPS Date Stamp                  : 2019:04:17
X Resolution                    : 72
Y Resolution                    : 72
Make                            : LGE
Thumbnail Offset                : 1083
Thumbnail Length                : 21548
Compression                     : JPEG (old-style)
Image Width                     : 4656
Image Height                    : 2620
Encoding Process                : Baseline DCT, Huffman coding
Bits Per Sample                 : 8
Color Components                : 3
Y Cb Cr Sub Sampling            : YCbCr4:2:0 (2 2)
Aperture                        : 1.6
Shutter Speed                   : 1/1389
Create Date                     : 2019:04:17 12:38:09.567906
Date/Time Original              : 2019:04:17 12:38:09.567906
Modify Date                     : 2019:04:17 12:38:09.567906
Thumbnail Image                 : (Binary data 21548 bytes, use -b option to extract)
GPS Altitude                    : 57.5 m Above Sea Level
GPS Date/Time                   : 2019:04:17 03:38:09Z
GPS Latitude                    : 37 deg 33' 59.78" N
GPS Longitude                   : 126 deg 58' 32.81" E
GPS Position                    : 37 deg 33' 59.78" N, 126 deg 58' 32.81" E
Image Size                      : 4656x2620
Light Value                     : 12.8
Megapixels                      : 12.2
Focal Length                    : 4.0 mm
	
	
	
	
exiftool -list  '06 - SPACESHIP.flac'
	
ExifTool Version Number         : 12.16
File Name                       : 06 - SPACESHIP.flac
Directory                       : .
File Size                       : 26 MiB
File Modification Date/Time     : 2025:04:23 05:07:46+09:00
File Access Date/Time           : 2025:04:23 05:07:42+09:00
File Inode Change Date/Time     : 2025:04:23 05:07:46+09:00
File Permissions                : rw-r--r--
File Type                       : FLAC
File Type Extension             : flac
MIME Type                       : audio/flac
Block Size Min                  : 4096
Block Size Max                  : 4096
Frame Size Min                  : 539
Frame Size Max                  : 15158
Sample Rate                     : 44100
Channels                        : 2
Bits Per Sample                 : 16
Total Samples                   : 7558152
MD5 Signature                   : 4b186cdc2bfda9b052d86f753a7e311f
Vendor                          : reference libFLAC 1.3.1 20141125
ISRC Number                     : KRA871900041
Title                           : SPACESHIP
Artist                          : IZ*ONE(아이즈원)
Album                           : BLOOM*IZ
Genre                           : Dance, Ballad
Albumartist                     : IZ*ONE
Discnumber                      : 1
Date                            : 2020-02-17
Organization                    : Genie Music Corporation, Stone Music Entertainment
Track Number                    : 06
Picture Type                    : Front Cover
Picture MIME Type               : image/jpeg
Picture Description             :
Picture Width                   : 500
Picture Height                  : 500
Picture Bits Per Pixel          : 24
Picture Indexed Colors          : 0
Picture Length                  : 390918
Picture                         : (Binary data 390918 bytes, use -b option to extract)
Duration                        : 0:02:51



exiftool -list 20191229_224307.mov
ExifTool Version Number         : 12.16
File Name                       : 20191229_224307.mov
Directory                       : .
File Size                       : 109 MiB
File Modification Date/Time     : 2025:04:19 21:15:18+09:00
File Access Date/Time           : 2025:04:19 21:14:57+09:00
File Inode Change Date/Time     : 2025:04:19 21:15:18+09:00
File Permissions                : rw-r--r--
File Type                       : MOV
File Type Extension             : mov
MIME Type                       : video/quicktime
Major Brand                     : Apple QuickTime (.MOV/QT)
Minor Version                   : 2007.9.0
Compatible Brands               : qt  , CAEP
Media Data Size                 : 114450436
Media Data Offset               : 32
Movie Header Version            : 0
Create Date                     : 2019:12:29 13:43:07
Modify Date                     : 2019:12:29 13:43:07
Time Scale                      : 30000
Duration                        : 18.99 s
Preferred Rate                  : 1
Preferred Volume                : 100.00%
Preview Time                    : 0 s
Preview Duration                : 0 s
Poster Time                     : 0 s
Selection Time                  : 0 s
Selection Duration              : 0 s
Current Time                    : 0 s
Next Track ID                   : 3
Track Header Version            : 0
Track Create Date               : 2019:12:29 13:43:07
Track Modify Date               : 2019:12:29 13:43:07
Track ID                        : 1
Track Duration                  : 18.99 s
Track Layer                     : 0
Track Volume                    : 0.00%
Image Width                     : 1920
Image Height                    : 1080
Graphics Mode                   : srcCopy
Op Color                        : 0 0 0
Compressor ID                   : avc1
Source Image Width              : 1920
Source Image Height             : 1080
X Resolution                    : 72
Y Resolution                    : 72
Bit Depth                       : 24
Video Frame Rate                : 29.97
Matrix Structure                : 1 0 0 0 1 0 0 0 1
Media Header Version            : 0
Media Create Date               : 2019:12:29 13:43:07
Media Modify Date               : 2019:12:29 13:43:07
Media Time Scale                : 48000
Media Duration                  : 18.99 s
Balance                         : 0
Handler Class                   : Data Handler
Handler Type                    : Alias Data
Audio Format                    : sowt
Audio Bits Per Sample           : 16
Audio Sample Rate               : 48000
Layout Flags                    : Stereo
Audio Channels                  : 2
Compressor Version              : CanonAVC0002
Camera Model Name               : Canon EOS 5D Mark II
Firmware Version                : Firmware Version 2.1.2
Avg Bitrate                     : 48.2 Mbps
Rotation                        : 0
Image Size                      : 1920x1080
Megapixels                      : 2.1

	


 exiftool -list 20250126_173638.mp4
 
ExifTool Version Number         : 12.16
File Name                       : 20250126_173638.mp4
Directory                       : .
File Size                       : 10 MiB
File Modification Date/Time     : 2025:04:19 21:17:01+09:00
File Access Date/Time           : 2025:04:19 21:16:59+09:00
File Inode Change Date/Time     : 2025:04:19 21:17:01+09:00
File Permissions                : rw-r--r--
File Type                       : MP4
File Type Extension             : mp4
MIME Type                       : video/mp4
Major Brand                     : MP4 v2 [ISO 14496-14]
Minor Version                   : 0.0.0
Compatible Brands               : isom, mp42
Media Data Size                 : 10499911
Media Data Offset               : 40
Movie Header Version            : 0
Create Date                     : 2025:01:26 08:36:47
Modify Date                     : 2025:01:26 08:36:47
Time Scale                      : 10000
Duration                        : 6.00 s
Preferred Rate                  : 1
Preferred Volume                : 100.00%
Preview Time                    : 0 s
Preview Duration                : 0 s
Poster Time                     : 0 s
Selection Time                  : 0 s
Selection Duration              : 0 s
Current Time                    : 0 s
Next Track ID                   : 3
Play Mode                       : SEQ_PLAY
Author                          : Galaxy S24
GPS Coordinates                 : 37 deg 31' 26.04" N, 129 deg 7' 26.40" E
Android Version                 : 14
Android Video Temporal Layers Count: (Binary data 4 bytes, use -b option to extract)
Android Capture Fps             : 30
Track Header Version            : 0
Track Create Date               : 2025:01:26 08:36:47
Track Modify Date               : 2025:01:26 08:36:47
Track ID                        : 1
Track Duration                  : 5.99 s
Track Layer                     : 0
Track Volume                    : 0.00%
Image Width                     : 1920
Image Height                    : 1080
Graphics Mode                   : srcCopy
Op Color                        : 0 0 0
Compressor ID                   : hvc1
Source Image Width              : 1920
Source Image Height             : 1080
X Resolution                    : 72
Y Resolution                    : 72
Bit Depth                       : 24
Color Representation            : nclx 1 1 1
Video Frame Rate                : 30.04
Matrix Structure                : 1 0 0 0 1 0 0 0 1
Media Header Version            : 0
Media Create Date               : 2025:01:26 08:36:47
Media Modify Date               : 2025:01:26 08:36:47
Media Time Scale                : 48000
Media Duration                  : 6.00 s
Handler Type                    : Audio Track
Handler Description             : SoundHandle
Balance                         : 0
Audio Format                    : mp4a
Audio Channels                  : 2
Audio Bits Per Sample           : 16
Audio Sample Rate               : 48000
Avg Bitrate                     : 14 Mbps
GPS Latitude                    : 37 deg 31' 26.04" N
GPS Longitude                   : 129 deg 7' 26.40" E
Rotation                        : 0
GPS Position                    : 37 deg 31' 26.04" N, 129 deg 7' 26.40" E
Image Size                      : 1920x1080
Megapixels                      : 2.1








exiftool -list '봄이의 노래.pptx'

ExifTool Version Number         : 12.16
File Name                       : 봄이의 노래.pptx
Directory                       : .
File Size                       : 194 KiB
File Modification Date/Time     : 2025:04:19 21:17:40+09:00
File Access Date/Time           : 2025:04:19 21:17:40+09:00
File Inode Change Date/Time     : 2025:04:19 21:17:40+09:00
File Permissions                : rw-r--r--
File Type                       : PPTX
File Type Extension             : pptx
MIME Type                       : application/vnd.openxmlformats-officedocument.presentationml.presentation
Zip Required Version            : 20
Zip Bit Flag                    : 0x0006
Zip Compression                 : Deflated
Zip Modify Date                 : 1980:01:01 00:00:00
Zip CRC                         : 0xaff1f72e
Zip Compressed Size             : 442
Zip Uncompressed Size           : 3322
Zip File Name                   : [Content_Types].xml
Preview Image                   : (Binary data 1851 bytes, use -b option to extract)
Template                        : 디지털 테마
Total Edit Time                 : 13 minutes
Words                           : 35
Application                     : Microsoft Office PowerPoint
Presentation Format             : 와이드스크린
Paragraphs                      : 4
Slides                          : 2
Notes                           : 0
Hidden Slides                   : 0
MM Clips                        : 0
Scale Crop                      : No
Heading Pairs                   : 사용한 글꼴, 4, 테마, 1, 슬라이드 제목, 2
Titles Of Parts                 : 맑은 고딕, 맑은 고딕 (제목), Arial, Bradley Hand ITC, New_3D02, 여(우).늑(대), 봄이의 노래
Company                         :
Links Up To Date                : No
Shared Doc                      : No
Hyperlinks Changed              : No
App Version                     : 16.0000
Title                           : PowerPoint 프레젠테이션
Creator                         : Spring
Last Modified By                : Spring
Revision Number                 : 5
Create Date                     : 2022:09:28 09:10:22Z
Modify Date                     : 2022:12:04 02:39:03Z
	
	
	
	
	
exiftool -list '쓰레기통 신호등 - 김승연.docx'
	
MIME Type                       : application/vnd.openxmlformats-officedocument.wordprocessingml.document
Zip Required Version            : 20
Zip Bit Flag                    : 0x0006
Zip Compression                 : Deflated
Zip Modify Date                 : 1980:01:01 00:00:00
Zip CRC                         : 0x576f9132
Zip Compressed Size             : 358
Zip Uncompressed Size           : 1445
Zip File Name                   : [Content_Types].xml
Title                           : 쓰레기통 신호등
Subject                         : 발명문서
Creator                         : Spring
Keywords                        : 발명Tag
Description                     : 발명
Last Modified By                : Spring
Revision Number                 : 14
Create Date                     : 2023:04:02 12:58:00Z
Modify Date                     : 2025:04:19 12:19:00Z
Template                        : Normal.dotm
Total Edit Time                 : 3.8 hours
Pages                           : 1
Words                           : 70
Characters                      : 400
Application                     : Microsoft Office Word
Doc Security                    : None
Lines                           : 3
Paragraphs                      : 1
Scale Crop                      : No
Heading Pairs                   : 제목, 1
Titles Of Parts                 : 쓰레기통 신호등
Company                         : 회사 없음
Links Up To Date                : No
Characters With Spaces          : 469
Shared Doc                      : No
Hyperlinks Changed              : No
App Version                     : 16.0000
	
	
	
exiftool -list lgu_202409140029.xlsx	
	
MIME Type                       : application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
Zip Required Version            : 20
Zip Bit Flag                    : 0x0006
Zip Compression                 : Deflated
Zip Modify Date                 : 1980:01:01 00:00:00
Zip CRC                         : 0x689dee62
Zip Compressed Size             : 350
Zip Uncompressed Size           : 1168
Zip File Name                   : [Content_Types].xml
Creator                         : Spring
Last Modified By                : Spring
Create Date                     : 2024:09:13 15:39:55Z
Modify Date                     : 2024:09:13 15:39:55Z
Application                     : Microsoft Excel
Doc Security                    : None
Scale Crop                      : No
Heading Pairs                   : 워크시트, 1
Titles Of Parts                 : _SELECT_A_CASE_WHEN_B_RECIPIENT
Links Up To Date                : No
Shared Doc                      : No
Hyperlinks Changed              : No
App Version                     : 16.0300

	
 */
	/**
	 * 다운로드
	 * @param url
	 * @param outputDir
	 * @return
	 * @throws IOException
	 */
    public static Path downloadFile(String url, String outputDir) throws IOException {
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        Path outputPath = Paths.get(outputDir, fileName);

        if (Files.exists(outputPath)) {
            log.info("이미 다운로드된 파일: " + outputPath);
            return outputPath;
        }
        
        // URL 유효성 검사
        final URL _url;
        try {
        	_url = new URL(url);
        } catch (MalformedURLException e) {
            log.error("잘못된 URL: " + url, e);
            throw new IllegalArgumentException("URL 형식이 올바르지 않습니다.");
        }

        log.info("다운로드 시작: " + url);
        try (InputStream in = _url.openStream()) {
            Files.createDirectories(Paths.get(outputDir));
            Files.copy(in, outputPath, StandardCopyOption.REPLACE_EXISTING);
        }

        log.info("다운로드 완료: " + outputPath);
        return outputPath;
    }
    
    
    /**
     * 압축 해제
     * @param zipPath
     * @param targetDir
     * @throws IOException
     */
    public static void unzip(Path zipPath, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipPath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path outPath = targetDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(outPath);
                } else {
                    Files.createDirectories(outPath.getParent());
                    Files.copy(zis, outPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
	
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
	
	private static Properties parserExifToolResult( List<String> cmdRes ) {
		final Properties result = new Properties();
		
		// Date                            : 2020-02-17
		for( String line : cmdRes ) {
			final int index = line.indexOf(":");
			if( index < 0 )
				continue;
			
			final String header = line.substring(0, index -1 ).trim();
			final String value = line.substring(index+1, line.length()).trim();
			
			if( header.length() < 1 || value.length() < 1 )
				continue;
			
			result.setProperty(header, value);
		}
		
		return result;
	}
	
	public static BnFileDocument getFileInfoDocument(File file, BnFile bnFile ) throws Exception {

		final List<String> cmdRes = CommandUtil.workExe("exiftool"
				, "-Title"
				, "-Subject"
				, "-Creator"
				, "-LastModifiedBy"
				, "-CreateDate"
				, "-ModifyDate"
				, "-Keywords"
				, "-Description"
				, "-HeadingPairs" 
				, "-Company"
				, file.getAbsolutePath()
				);
		
		if( BurrowUtils.isEmpty(cmdRes) ) {
			return null;
		}
		
		final Properties values = StorageUtils.parserExifToolResult(cmdRes);
		if( values.size() < 1 ) {
			return null;
		}
		
		final BnFileDocument result = new BnFileDocument();
		result.setFileNo( bnFile.getNo() );
		
		result.setTitle( values.getProperty("Title", null) );
		result.setSubject( values.getProperty("Subject", null) );
		result.setCreator( values.getProperty("Creator", null) );
		result.setCreateDate( values.getProperty("CreateDate", null) );
		result.setModifyDate( values.getProperty("ModifyDate", null) );
		result.setKeywords( values.getProperty("Keywords", null) );
		result.setDescription( values.getProperty("Description", null) );
		result.setHeadingPairs( values.getProperty("HeadingPairs", null) );
		result.setCompany( values.getProperty("Company", null) );
		
		return result;
	}


	public static AbsBnFileInfo getFileInfoImage(File file, BnFile bnFile) throws Exception{
		final List<String> cmdRes = CommandUtil.workExe("exiftool"
				, "-Make"
				, "-CameraModelName"
				, "-Orientation"
				, "-FNumber"
				, "-ExposureTime"
				, "-SensingMethod"
				, "-Flash"
				, "-LightSource"
				, "-WhiteBalance" 
				, "-ShutterSpeed"
				, "-Iso"
				, "-ImageWidth"
				, "-ImageHeight"
				, "-CreateDate"
				, "-ModifyDate"
				, "-GpsLatitude"
				, "-GpsLongitude"
				, file.getAbsolutePath()
				);
		
		if( BurrowUtils.isEmpty(cmdRes) ) {
			return null;
		}
		
		final Properties values = StorageUtils.parserExifToolResult(cmdRes);
		if( values.size() < 1 ) {
			return null;
		}
		
		final BnFileImage result = new BnFileImage();
		result.setFileNo( bnFile.getNo() );
		
		result.setMake( values.getProperty("Make", null) );
		result.setCameraModelName( values.getProperty("CameraModelName", null) );
		result.setOrientation( values.getProperty("Orientation", null) );
		result.setFNumber( values.getProperty("FNumber", null) );
		result.setExposureTime( values.getProperty("ExposureTime", null) );
		result.setSensingMethod( values.getProperty("SensingMethod", null) );
		result.setFlash( values.getProperty("Flash", null) );
		result.setLightSource( values.getProperty("LightSource", null) );
		result.setWhiteBalance( values.getProperty("WhiteBalance", null) );
		result.setShutterSpeed( values.getProperty("ShutterSpeed", null) );
		result.setIso( values.getProperty("Iso", null) );
		result.setImageWidth( values.getProperty("ImageWidth", null) );
		result.setImageHeight( values.getProperty("ImageHeight", null) );
		result.setCreateDate( values.getProperty("CreateDate", null) );
		result.setModifyDate( values.getProperty("ModifyDate", null) );
		result.setGpsLatitude( BurrowUtils.dmsToDecimal(values.getProperty("GpsLatitude", null)) );
		result.setGpsLongitude( BurrowUtils.dmsToDecimal(values.getProperty("GpsLongitude", null)) );
		
		return result;
	}

	public static AbsBnFileInfo getFileInfoVideo(File file, BnFile bnFile) throws Exception{
		
		final List<String> cmdRes = CommandUtil.workExe("exiftool"
				, "-MimeType"
				, "-Author"
				, "-Duration"
				, "-PreferredRate"
				, "-PreferredVolume"
				, "-AudioFormat"
				, "-AudioBitsPerSample"
				, "-AudioSampleRate"
				, "-LayoutFlags" 
				, "-AudioChannels"
				, "-CompressorVersion"
				, "-CameraModelName"
				, "-FirmwareVersion"
				, "-AvgBitrate"
				, "-Rotation"
				, "-ImageWidth"
				, "-ImageHeight"
				, "-CreateDate"
				, "-ModifyDate"
				, "-GpsLatitude"
				, "-GpsLongitude"
				, file.getAbsolutePath()
				);
		
		if( BurrowUtils.isEmpty(cmdRes) ) {
			return null;
		}
		
		final Properties values = StorageUtils.parserExifToolResult(cmdRes);
		if( values.size() < 1 ) {
			return null;
		}
		
		final BnFileVideo result = new BnFileVideo();
		result.setFileNo( bnFile.getNo() );
		
		result.setMimeType( values.getProperty("MimeType", null) );
		result.setAuthor( values.getProperty("Author", null) );
		result.setDuration( values.getProperty("Duration", null) );
		result.setPreferredRate( values.getProperty("PreferredRate", null) );
		result.setPreferredVolume( values.getProperty("PreferredVolume", null) );
		result.setAudioFormat( values.getProperty("AudioFormat", null) );
		result.setAudioBitsPerSample( values.getProperty("AudioBitsPerSample", null) );
		result.setAudioSampleRate( values.getProperty("AudioSampleRate", null) );
		result.setLayoutFlags( values.getProperty("LayoutFlags", null) );
		result.setAudioChannels( values.getProperty("AudioChannels", null) );
		result.setCompressorVersion( values.getProperty("CompressorVersion", null) );
		result.setCameraModelName( values.getProperty("CameraModelName", null) );
		result.setFirmwareVersion( values.getProperty("FirmwareVersion", null) );
		result.setAvgBitrate( values.getProperty("AvgBitrate", null) );
		result.setRotation( values.getProperty("Rotation", null) );
		result.setImageWidth( values.getProperty("ImageWidth", null) );
		result.setImageHeight( values.getProperty("ImageHeight", null) );
		result.setCreateDate( values.getProperty("CreateDate", null) );
		result.setModifyDate( values.getProperty("ModifyDate", null) );
		result.setGpsLatitude( BurrowUtils.dmsToDecimal(values.getProperty("GpsLatitude", null)) );
		result.setGpsLongitude( BurrowUtils.dmsToDecimal(values.getProperty("GpsLongitude", null)) );
		
		return result;
	}

	public static AbsBnFileInfo getFileInfoAudio(File file, BnFile bnFile) throws Exception{
		final List<String> cmdRes = CommandUtil.workExe("exiftool"
				, "-MimeType"
				, "-SampleRate"
				, "-Channels"
				, "-BitsPerSample"
				, "-TotalSamples"
				, "-Title"
				, "-Artist"
				, "-Album"
				, "-Genre" 
				, "-AlbumArtist"
				, "-DiscNumber"
				, "-AlbumDate"
				, "-Organization"
				, "-TrackNumber"
				, "-Duration"
				, file.getAbsolutePath()
				);
		
		if( BurrowUtils.isEmpty(cmdRes) ) {
			return null;
		}
		
		final Properties values = StorageUtils.parserExifToolResult(cmdRes);
		if( values.size() < 1 ) {
			return null;
		}
		
		final BnFileAudio result = new BnFileAudio();
		result.setFileNo( bnFile.getNo() );
		
		result.setMimeType( values.getProperty("MimeType", null) );
		result.setSampleRate( values.getProperty("SampleRate", null) );
		result.setChannels( values.getProperty("Channels", null) );
		result.setBitsPerSample( values.getProperty("BitsPerSample", null) );
		result.setTotalSamples( values.getProperty("TotalSamples", null) );
		result.setTitle( values.getProperty("Title", null) );
		result.setArtist( values.getProperty("Artist", null) );
		result.setAlbum( values.getProperty("Album", null) );
		result.setGenre( values.getProperty("Genre", null) );
		result.setAlbumArtist( values.getProperty("AlbumArtist", null) );
		result.setDiscNumber( values.getProperty("DiscNumber", null) );
		result.setAlbumDate( values.getProperty("AlbumDate", null) );
		result.setOrganization( values.getProperty("Organization", null) );
		result.setTrackNumber( values.getProperty("TrackNumber", null) );
		result.setDuration( values.getProperty("Duration", null) );
		
		return result;
	}

	public static AbsBnFileInfo getFileInfoArchive(File file, BnFile bnFile) throws Exception{
		
		if( ! "zip".equals( bnFile.getExtension() )){
			return null;
		}
		
		final BnFileArchive result = new BnFileArchive();
		result.setFileNo(bnFile.getNo());
		
		try (ZipFile zip = new ZipFile(file)) {
			Enumeration<? extends ZipEntry> entries = zip.entries();
			
			long totalUncompressed = 0L;
			long totalCompressed = 0L;
			int count = 0;
			
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (!entry.isDirectory()) {
					count++;
					totalUncompressed += entry.getSize(); // -1 if unknown
					totalCompressed += entry.getCompressedSize(); // -1 if unknown
                }
            }
			
			result.setTotalEntries(count);
			result.setUncompressedSize(totalUncompressed);
			
			if (totalUncompressed > 0 && totalCompressed > 0) {
				result.setCompressionRatio( (float) totalCompressed / totalUncompressed );
			}
			
			result.setComment( zip.getComment() );
        }
		
		return result;
	}
	
	private static byte [] getFileThumbnailByJava(File file, BnFile bnFile) throws Exception{
    	final BufferedImage sourceImage = ImageIO.read(file);
    	final BufferedImage thumb = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    	final Graphics2D g2d = thumb.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(sourceImage, 0, 0, width, height, null);
        g2d.dispose();
        
        // 이미지 -> binary
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(thumb, ThumFormat, baos); // "jpeg" 또는 "png" 등
        byte [] result = baos.toByteArray();
        baos.close();
        
        return result;
	}

	private static byte [] getFileThumbnailByExif(File file, BnFile bnFile) throws Exception{
		//exiftool -ThumbnailImage -b 20190417_123809.jpg | xxd -p | tr -d '\n'
		
		final List<String> cmdRes = CommandUtil.workExe("exiftool"
				, "-ThumbnailImage"
				, "-b"
				, file.getAbsolutePath()
				, "|"
				, "xxd"
				, "-p"
				, "|"
				, "tr"
				, "-d"
				, "'\n'"
				);
		
		if( BurrowUtils.isEmpty(cmdRes) ) {
			return null;
		}
		
		final String hexString = cmdRes.get(0);
		
        
        return hexStringToByteArray(hexString);
	}
	
	private static byte[] hexStringToByteArray(String hex) {
	    
		final int len = hex.length();
	    
		if (len % 2 != 0) {
	        throw new IllegalArgumentException("Hex string must have even length. " + len);
	    }
	    
	    final byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
	                            + Character.digit(hex.charAt(i + 1), 16));
	    }
	    return data;
	}


	public static byte [] getFileThumbnail(File file, BnFile bnFile) throws Exception{
		byte [] result = null;
		
		final String ext = bnFile.getExtension();
		switch( ext ) {
		case "jpg":case "jpeg":case "tiff":
		case "cr2":case "nef":case "arw":
		case "orf":case "rw2":case "dng":
			result = getFileThumbnailByExif( file, bnFile );
			break;
		}
		
		if( result == null ) {
			switch( ext ) {
			case "jpg":case "jpeg":case "png":case "bmp":case "wbmp":case "gif" :{
				result = getFileThumbnailByJava( file, bnFile );
				break;
			}
			case "ppt":case "pptx":{
				try (FileInputStream fis = new FileInputStream(file);
						XMLSlideShow ppt = new XMLSlideShow(fis)) {

			            final Dimension pageSize = ppt.getPageSize();
			            final XSLFSlide slide = ppt.getSlides().get(0); // 첫 슬라이드

			            final BufferedImage img = new BufferedImage(pageSize.width, pageSize.height, BufferedImage.TYPE_INT_ARGB);
			            final Graphics2D graphics = img.createGraphics();
			            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

			            slide.draw(graphics);
			            graphics.dispose();

			            // 이미지 -> Base64
			            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			            ImageIO.write(img, ThumFormat, baos); // "jpeg" 또는 "png" 등
			            result = baos.toByteArray();
			            baos.close();
			        }
				break;
			}
			}
		}
		
		return result;
	}
	
}

