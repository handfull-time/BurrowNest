package com.utime.burrowNest.test.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.utime.burrowNest.storage.service.StorageService;
import com.utime.burrowNest.storage.service.impl.DirecotryManager;
import com.utime.burrowNest.storage.vo.AbsPath;
import com.utime.burrowNest.storage.vo.DirectoryDto;
import com.utime.burrowNest.user.dao.UserDao;
import com.utime.burrowNest.user.vo.UserVo;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("Test") 
@RequiredArgsConstructor
public class TestController {

    final DirecotryManager direcotryManager;
    
    final StorageService ss;
    
    final UserDao userDao;

    @GetMapping("Root.json")
    public List<DirectoryDto> getRoot() throws JsonProcessingException{
        final List<DirectoryDto> res = direcotryManager.getAccessibleDirectoriesForGroup(1);
        if (res == null ) {
            return Collections.emptyList();
        }

        // final String result = objMapper.writeValueAsString(res);
        
        return res;
    }
    
    @GetMapping("FileList.json")
    public List<AbsPath> getFileList(@RequestParam("uid") String uid) throws JsonProcessingException{
    	UserVo user = userDao.getUserFormId("admin");
    	List<AbsPath> result = ss.getFiles(user, uid);
        
        return result;
    }

}
