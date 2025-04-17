package com.utime.burrowNest.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;


/**
 * command 명령어를 수행한다.
 */
@Slf4j
public class CommandUtil {

//	// 설치 여부 확인
//	final List<String> output = CommandUtil.workExe("npm", "list", "-g", "terser");
//    boolean isInstalled = output.stream().anyMatch(line -> line.contains("terser@"));
//    if (isInstalled) {
//        log.info("Terser is already installed.");
//        return;
//    } 
//    
//    // 설치하자.
//    final List<String> installOutput = CommandUtil.workExe("npm", "install", "-g", "terser");
//
//    log.info("Terser install Output: " + String.join("\n", installOutput));	
	
  /**
     * 단일 명령 실행
     * @param command : 명령어
     * @return 결과 응답 값 리스트
     */
	public static List<String> workExe(final String command){
        return workExe(new String[]{command});
	}
	
	/**
     * 다중 명령 실행
     * @param commands : 명령어 배열
     * @return 결과 응답 값 리스트
	 */
	public static List<String> workExe(final String ... commands ){

        final List<String> outList = new ArrayList<>();
        final List<String> errorList = new ArrayList<>();

		Exception err = null;
		final Runtime rt = Runtime.getRuntime();
		try {
			// 명령을 실행 할 때 실행될 경로를 입력 해 주고 명령어를 입력해야 한다. 그렇지 않을 경우 명령어를 인식하지 못한다.
			log.info( "Command exec... " + String.join(" ", commands));
            final Process proc = rt.exec(commands);
			
			final StreamGobbler errorPrc = new StreamGobbler( proc.getErrorStream(), errorList);
			final StreamGobbler outputPrc = new StreamGobbler( proc.getInputStream(), outList);

			errorPrc.start();
			outputPrc.start();

			proc.waitFor();
			errorPrc.join();
			outputPrc.join();
			proc.destroy();

        } catch (IOException | InterruptedException e) {
            log.error("", e);
			err = e;
		}finally{
			rt.freeMemory();
		}

		if( err != null ){
            log.info("CommandUtil Exception: " + err.getMessage());
			return errorList;
		}

        if (!errorList.isEmpty()) {
			log.info( "Error : " + errorList.get(0));
			return errorList;
		}

        if (outList.isEmpty()) {
			log.info("Error : Data is empty");
		}

		return outList;
	}


	/**
	 * 커맨드 내용을 얻어오는 Thread
	 *
	 */
	private static class StreamGobbler extends Thread{
        private final InputStream inputStream;
        private final List<String> dataResult;

        public StreamGobbler(InputStream is, List<String> dataResult) {
            this.inputStream = is;
            this.dataResult = dataResult;
		}

		@Override
		public void run() {
			BufferedReader br = null;
			try{
                br = new BufferedReader(new InputStreamReader(this.inputStream));

                String line;
				while( (line = br.readLine()) != null ){
                    this.dataResult.add(line);
				}

			}catch( IOException e ){
				log.error("", e);
			}finally{
				try {
					if( br != null )
						br.close();
                    this.inputStream.close();
                } catch (Exception e) {
                    log.error("", e);
				}
			}
		}
	}
}