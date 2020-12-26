package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICommonConstService;
import com.jsy.community.constant.Const;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 公共常量表 前端控制器
 * </p>
 *
 * @author lihao
 * @since 2020-12-25
 */
@Api(tags = "公共常量控制器")
@ApiJSYController
@Slf4j
@RestController
@RequestMapping("/const")
public class CommonConstController {
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private ICommonConstService commonConstService;
}

