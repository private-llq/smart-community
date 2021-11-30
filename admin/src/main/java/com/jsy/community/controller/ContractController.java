package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.entity.proprietor.AssetLeaseRecordEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.ContractQO;
import com.jsy.community.service.IContractService;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author DKS
 * @description 合同管理
 * @since 2021/10/29  10:03
 **/
@Api(tags = "合同管理控制器")
@RestController
@RequestMapping("contract")
// @ApiJSYController
public class ContractController {
	@Resource
	private IContractService contractService;
	
	/**
	 * @Description: 【合同管理】条件查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.AssetLeaseRecordEntity>>
	 * @Author: DKS
	 *@Date: 2021/10/29 10:30
	 **/
	@Login
	@ApiOperation("【合同管理】条件查询")
	@PostMapping("query")
	public CommonResult<PageInfo<AssetLeaseRecordEntity>> queryContractPage(@RequestBody BaseQO<ContractQO> baseQO){
		return CommonResult.ok(contractService.queryContractPage(baseQO));
	}
}
