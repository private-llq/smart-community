package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author YuLF
 * @since 2020-12-07 14:13
 */
@RestController
@ApiJSYController
@RequestMapping("/test")
public class TestController {

    @GetMapping(params = "test01")
    public String test01(){
        return "test01";
    }
    @GetMapping(params = "test02")
    public String test02(){
        return "test02";
    }

}
