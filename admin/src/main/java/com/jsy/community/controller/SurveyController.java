package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.service.ISurveyService;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 大后台 概况控制器
 * </p>
 *
 * @author DKS
 * @since 2021-11-09
 */
@Api(tags = "概况控制器")
@RestController
@ApiJSYController
@RequestMapping("/survey")
@Login
public class SurveyController {

    @Resource
    private ISurveyService surveyService;
    
    /**
     * @Description: 获取大后台概况
     * @author: DKS
     * @since: 2021/11/9 10:59
     * @Param: []
     * @return: com.jsy.community.vo.CommonResult
     */
    @Login
    @GetMapping("/getSurvey")
    public CommonResult getSurvey() {
        return CommonResult.ok(surveyService.getSurvey());
    }
}

