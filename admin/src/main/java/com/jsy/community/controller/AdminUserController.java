package com.jsy.community.controller;

import com.jsy.community.annotation.businessLog;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.AdminUserQO;
import com.jsy.community.service.AdminException;
import com.jsy.community.service.IAdminUserService;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author DKS
 * @description 账号管理
 * @since 2021-11-18 14:31
 **/
@RequestMapping("/account")
@Api(tags = "账号管理控制器")
// @ApiJSYController
@Slf4j
@RestController
public class AdminUserController {
	
	@Resource
	private IAdminUserService adminUserService;
	
	/**
	* @Description: 条件查询中台操作员
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: DKS
	 * @Date: 2021/11/18
	**/
	@PostMapping("query")
	@Permit("community:admin:account:query")
	public CommonResult queryOperator(@RequestBody BaseQO<AdminUserQO> baseQO){
		if(baseQO.getQuery() == null){
			baseQO.setQuery(new AdminUserQO());
		}
		baseQO.getQuery().setUid(UserUtils.getUserId());
		return CommonResult.ok(adminUserService.queryOperator(baseQO));
	}
	
	/**
	* @Description: 给中台添加操作员
	 * @Param: [adminUserQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: DKS
	 * @Date: 2021/11/18
	**/
	@PostMapping("insert")
	@businessLog(operation = "新增",content = "新增了【账号管理】")
	@Permit("community:admin:account:insert")
	public CommonResult addOperator(@RequestBody AdminUserQO adminUserQO){
		ValidatorUtils.validateEntity(adminUserQO);
		Integer integer = adminUserService.addOperator(adminUserQO);
		return CommonResult.ok(integer == 1 ? "添加成功" : "请使用原账号的密码登录");
	}
	
	/**
	* @Description: 编辑中台操作员
	 * @Param: [adminUserQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: DKS
	 * @Date: 2021/11/18
	**/
	@PutMapping("update")
	@businessLog(operation = "编辑",content = "更新了【账号管理】")
	@Permit("community:admin:account:update")
	public CommonResult updateOperator(@RequestBody AdminUserQO adminUserQO){
		if (StringUtils.isNotBlank(adminUserQO.getPassword())) {
			String pattern = "^(?=.*[A-Z0-9])(?=.*[a-z0-9])(?=.*[a-zA-Z])(.{6,12})$";
			if (!adminUserQO.getPassword().matches(pattern)) {
				throw new AdminException(JSYError.REQUEST_PARAM.getCode(), "请输入一个正确的6-12位密码,至少包含大写字母或小写字母或数字两种!");
			}
		}
		adminUserService.updateOperator(adminUserQO);
		return CommonResult.ok("操作成功");
	}
	
	/**
	 * @Description: 删除中台操作员
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: DKS
	 * @Date: 2021/11/18
	 **/
	@DeleteMapping("delete")
	@businessLog(operation = "删除",content = "删除了【账号管理】")
	@Permit("community:admin:account:delete")
	public CommonResult deleteOperator(Long id){
		adminUserService.deleteOperator(id);
		return CommonResult.ok("操作成功");
	}
}
