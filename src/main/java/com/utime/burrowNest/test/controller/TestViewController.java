package com.utime.burrowNest.test.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.burrowNest.storage.service.StorageService;
import com.utime.burrowNest.storage.vo.DirectoryDto;
import com.utime.burrowNest.user.dao.UserDao;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("Test") 
@RequiredArgsConstructor
public class TestViewController {

    final StorageService ss;
    
    final UserDao userDao;

    @GetMapping("drive")
    public String drive(@RequestParam(required=false) String uid, Model model) {
    	List<DirectoryDto> dir = ss.getDirectory(null, uid);
        model.addAttribute("uid", dir.get(0).getUid());
        return "Storage/dir-tree";
    }

    @ResponseBody
    @GetMapping("List.json")
    public List<DirectoryDto> list(@RequestParam(required=false) String uid) {
    	List<DirectoryDto> dir = ss.getDirectory(null, uid);
        return dir;
    }
}
