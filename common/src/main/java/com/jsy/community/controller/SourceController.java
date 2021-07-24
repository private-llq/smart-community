package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.PaymentEnum;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.HashMap;
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
		System.out.println(BusinessEnum.CarTypeEnum.CAR_TYPE_LIST);
		System.out.println(BusinessEnum.CheckStatusEnum.checkStatusList);
		System.out.println(BusinessEnum.VisitReasonEnum.visitReasonList);
		System.out.println(BusinessEnum.CommunityAccessEnum.communityAccessList);
		System.out.println(BusinessEnum.BuildingAccessEnum.buildingAccessList);
		System.out.println(BusinessEnum.RelationshipEnum.relationshipList);
		System.out.println(BusinessEnum.EntryTypeEnum.entryTypeList);
		System.out.println(PaymentEnum.TradeFromEnum.tradeFromList);
		System.out.println(BusinessEnum.FamilyTypeEnum.familyTypeList);
		System.out.println(BusinessEnum.MemberTallyEnum.tallyList);
		System.out.println(BusinessEnum.MaritalStatusEnum.maritalStatusList);
	}
	
	@ApiOperation("字典资源查询")
	@GetMapping("typeSource")
	public CommonResult<List<Map<String, Object>>> typeSource(@RequestParam String typeName){
		List<Map<String, Object>> maps = BusinessEnum.sourceMap.get(typeName);
		if(CollectionUtils.isEmpty(maps)){
			return CommonResult.ok(PaymentEnum.sourceMap.get(typeName));
		}
		return CommonResult.ok(maps);
	}
	@ApiOperation("字典资源查询")
	@GetMapping("typeSources")
	public CommonResult typeSources(@RequestParam String typeKey){
		HashMap<String, Object> map = new HashMap<>();
		String[] split = typeKey.split(",");
		if (split!=null){
			for (String s : split) {
				List<Map<String, Object>> maps = BusinessEnum.sourceMap.get(s);
				if(!CollectionUtils.isEmpty(maps)){
					 map.put(s,BusinessEnum.sourceMap.get(s));
				}
			}
		}
		return CommonResult.ok(map);
	}
}
