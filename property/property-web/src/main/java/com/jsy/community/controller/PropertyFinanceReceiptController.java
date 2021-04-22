package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IPropertyFinanceReceiptService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyFinanceReceiptEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chq459799974
 * @description 财务板块 - 收款单
 * @since 2021-04-21 17:30
 **/
@Api(tags = "财务板块 - 收款单")
@RestController
@RequestMapping("/finance/receipt")
@ApiJSYController
@Login
public class PropertyFinanceReceiptController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IPropertyFinanceReceiptService propertyFinanceReceiptService;
	
	/**
	* @Description: 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/4/22
	**/
	@ApiOperation("分页查询")
	@PostMapping("page")
	public CommonResult queryPage(@RequestBody BaseQO<PropertyFinanceReceiptEntity> baseQO){
		if(baseQO.getQuery() == null){
			baseQO.setQuery(new PropertyFinanceReceiptEntity());
		}
		baseQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
		return CommonResult.ok(propertyFinanceReceiptService.queryPage(baseQO),"查询成功");
	}
}
