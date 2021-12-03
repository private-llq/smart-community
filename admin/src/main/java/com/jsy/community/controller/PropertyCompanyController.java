package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.entity.PropertyCompanyEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.PropertyCompanyQO;
import com.jsy.community.service.IPropertyCompanyService;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author DKS
 * @description 物业公司
 * @since 2021-10-15 10:31
 **/
@RequestMapping("property/company")
@Api(tags = "物业公司控制器")
@Slf4j
@RestController
@ApiJSYController
public class PropertyCompanyController {
	
	@Resource
	private IPropertyCompanyService propertyCompanyService;
	
	/**
	 * @Description: 物业公司条件查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: DKS
	 * @Date: 2021/10/15
	 **/
	@PostMapping("query")
	@Permit("community:admin:property:company:query")
	public CommonResult queryCompany(@RequestBody BaseQO<PropertyCompanyQO> baseQO){
		if(baseQO.getQuery() == null){
			baseQO.setQuery(new PropertyCompanyQO());
		}
		return CommonResult.ok(propertyCompanyService.queryCompany(baseQO));
	}
	
	/**
	 * @Description: 添加物业公司
	 * @Param: [propertyCompanyEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: DKS
	 * @Date: 2021/10/15
	 **/
	@PostMapping("add")
	@businessLog(operation = "新增",content = "新增了【物业公司】")
	@Permit("community:admin:property:company:add")
	public CommonResult addCompany(@RequestBody PropertyCompanyEntity propertyCompanyEntity){
		ValidatorUtils.validateEntity(propertyCompanyEntity);
		return CommonResult.ok(propertyCompanyService.addCompany(propertyCompanyEntity) ? "添加成功" : "添加失败");
	}
	
	/**
	 * @Description: 编辑物业公司
	 * @Param: [propertyCompanyEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: DKS
	 * @Date: 2021/10/15
	 **/
	@PutMapping("update")
	@businessLog(operation = "编辑",content = "更新了【物业公司】")
	@Permit("community:admin:property:company:update")
	public CommonResult updateCompany(@RequestBody PropertyCompanyEntity propertyCompanyEntity){
		ValidatorUtils.validateEntity(propertyCompanyEntity);
		return CommonResult.ok(propertyCompanyService.updateCompany(propertyCompanyEntity) ? "操作成功" : "操作失败");
	}
	
	/**
	 * @Description: 删除物业公司
	 * @Param: [sysUserEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: DKS
	 * @Date: 2021/10/15
	 **/
	@DeleteMapping("delete")
	@businessLog(operation = "删除",content = "删除了【物业公司】")
	@Permit("community:admin:property:company:delete")
	public CommonResult deleteCompany(Long id){
		propertyCompanyService.deleteCompany(id);
		return CommonResult.ok("删除成功");
	}
	
	/**
	 * @Description: 物业公司列表查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: DKS
	 * @Date: 2021/10/19
	 **/
	@GetMapping("list")
	@Permit("community:admin:property:company:list")
	public CommonResult queryCompanyList(){
		return CommonResult.ok(propertyCompanyService.queryCompanyList());
	}
}
