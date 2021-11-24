package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IPropertyFinanceLogService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FinanceLogEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.FinanceLogQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author DKS
 * @description 收款管理操作日志控制器
 * @since 2021/8/23  11:43
 **/
@Api(tags = "收款管理操作日志控制器")
@RestController
@RequestMapping("/finance/log")
@ApiJSYController
public class PropertyFinanceLogController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IPropertyFinanceLogService propertyFinanceLogService;
	
	/**
	 * @Description: 收款管理操作日志分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.FinanceLogEntity>>
	 * @Author: DKS
	 * @Date: 2021/08/23 16:22
	 **/
	@ApiOperation("收款管理操作日志分页查询")
	@PostMapping("/query")
	@Permit("community:property:finance:log:query")
	public CommonResult<PageInfo<FinanceLogEntity>> queryFinanceLogPage(@RequestBody BaseQO<FinanceLogQO> baseQO) {
		FinanceLogQO query = baseQO.getQuery();
		if(query == null){
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少查询类型");
		}
		query.setCommunityId(UserUtils.getAdminCommunityId());
		return CommonResult.ok(propertyFinanceLogService.queryFinanceLogPage(baseQO));
	}
}