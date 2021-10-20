package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.entity.PayCompanyEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.service.IPayCompanyService;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 缴费单位 前端控制器
 * </p>
 *
 * @author lihao
 * @since 2020-12-11
 */
@Api(tags = "缴费单位控制器")
@Slf4j
@RestController
@RequestMapping("/payCompany")
@ApiJSYController
public class PayCompanyController {
	
	@Autowired
	private IPayCompanyService payCompanyService;
	
	@ApiOperation("添加缴费单位")
	@PostMapping("/addPayCompany")
	@businessLog(operation = "新增",content = "新增了【缴费单位】")
	public CommonResult addPayCompany(@RequestBody PayCompanyEntity companyEntity){
		payCompanyService.addPayCompany(companyEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("查询所有缴费单位信息")
	@PostMapping("/getPayCompany")  // TODO 做了一个根据缴费单位名称的模糊分页查询
	public CommonResult<PageInfo<PayCompanyEntity>> getPayCompany(@RequestBody BaseQO<PayCompanyEntity> baseQO){
		PageInfo<PayCompanyEntity> pageInfo = payCompanyService.getPayCompany(baseQO);
		return CommonResult.ok(pageInfo);
	}
	
	
	
	
	
	
}

