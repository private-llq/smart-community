package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IPropertyFinanceCountService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyFinanceCountEntity;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.property.StatisticsVO;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * @author chq459799974
 * @description 财务板块 - 统计
 * @since 2021-04-26 17:12
 **/
@Api(tags = "财务板块 - 统计")
@RestController
@RequestMapping("/finance/count")
@ApiJSYController
public class PropertyFinanceCountController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IPropertyFinanceCountService propertyFinanceCountService;
	
	
	@ApiOperation("缴费统计")
	@PostMapping("/orderPaid")
	@Permit("community:property:finance:count:orderPaid")
	public CommonResult orderPaid(@RequestBody PropertyFinanceCountEntity query){
		ValidatorUtils.validateEntity(query, PropertyFinanceCountEntity.QueryValidate.class);
		//第一次默认查询本日
		if(query.getStartDate() == null){
			query.setStartDate(LocalDate.now());
		}
		if (query.getEndDate() == null) {
			query.setEndDate(LocalDate.now());
		}
		query.setCommunityId(UserUtils.getAdminCommunityId());
		StatisticsVO statisticsVO = new StatisticsVO();
		switch (query.getQueryType()) {
			case 1:
				statisticsVO = propertyFinanceCountService.orderPaidCount(query);
				break;
			case 2:
				statisticsVO = propertyFinanceCountService.orderReceivableCount(query);
				break;
			case 3:
				statisticsVO = propertyFinanceCountService.statementCount(query);
				break;
			default:
				break;
		}
		return CommonResult.ok(statisticsVO, "查询成功");
	}

}
