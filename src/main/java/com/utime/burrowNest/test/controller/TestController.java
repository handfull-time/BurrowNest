package com.utime.burrowNest.test.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.utime.burrowNest.storage.service.impl.DirecotryManager;
import com.utime.burrowNest.storage.vo.DirectoryDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("Test") 
@RequiredArgsConstructor
public class TestController {

    final DirecotryManager direcotryManager;

    @GetMapping("Root.json")
    public List<DirectoryDto> getRoot() throws JsonProcessingException{
        final List<DirectoryDto> res = direcotryManager.getAccessibleDirectoriesForGroup(1);
        if (res == null ) {
            return Collections.emptyList();
        }

        // final String result = objMapper.writeValueAsString(res);

        return res;
    }

}
