package com.utime.burrowNest;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BurrowNestApplication {

	// 콘솔 접속 jdbc:h2:file:D://Projects/Burrow/BurrowNest/data/burrownest.h2;AUTO_SERVER=TRUE
	//           jdbc:h2:file:D:/Projects/Burrow/BurrowNest/data/burrownest.h2;AUTO_SERVER=TRUE
	// http://localhost:9709/Burrow/DbConsoleH2/
	// https://www.h2database.com/html/datatypes.html
	
/*
{
  "servers": {
    "github": {
      "url": "https://api.githubcopilot.com/mcp/",
      "scope": "workspace",
      "requestInit": {
        "headers": {
          "Authorization": "Bearer ghp_......."
        }
      }
    }
  }
} 

 */
	
	public static void main(String[] args) throws InterruptedException, IOException {
		
		SpringApplication.run(BurrowNestApplication.class, args);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("프로그램이 종료됩니다. ");
        }));
	}

}