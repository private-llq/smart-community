package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @author chq459799974
 * @description 字典资源查询(枚举、常量、Redis)
 * @since 2020-11-25 14:48
 **/
@RestController
@Api(tags = "字典资源控制器")
@RequestMapping("source")
@ApiJSYController
//@ConditionalOnProperty(value = "jsy.web.enable", havingValue = "true")
public class SourceController {
	
	//初始化静态代码块
	@PostConstruct
	public void initSource(){
		System.out.println(BusinessEnum.CarTypeEnum.carTypeList);
		System.out.println(BusinessEnum.CheckStatusEnum.checkStatusList);
		System.out.println(BusinessEnum.VisitReasonEnum.visitReasonList);
		System.out.println(BusinessEnum.CommunityAccessEnum.communityAccessList);
		System.out.println(BusinessEnum.BuildingAccessEnum.buildingAccessList);
		System.out.println(BusinessEnum.RelationshipEnum.relationshipList);
		System.out.println(BusinessEnum.EntryTypeEnum.entryTypeList);
	}
	
	@ApiOperation("字典资源查询")
	@GetMapping("typeSource")
	public CommonResult<List<Map<String, Object>>> typeSource(@RequestParam String typeName){
		return CommonResult.ok(BusinessEnum.sourceMap.get(typeName));
	}
}
