package com.jsy.community.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author YuLF
 * @since 2020-12-15 17:47
 */
@RequestMapping("/test")
@Api(tags = "房屋租售控制器")
@Slf4j
@RestController
public class TestController {

    @GetMapping("test01")
    public String test01(){
        return "测试";
    }

    public static void main(String[] args) {

    }

}
