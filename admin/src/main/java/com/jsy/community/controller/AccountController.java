//package com.jsy.community.controller;
//
//import com.jsy.community.annotation.ApiJSYController;
//import com.jsy.community.annotation.auth.Login;
//import com.jsy.community.entity.admin.AdminUserEntity;
//import com.jsy.community.qo.BaseQO;
//import com.jsy.community.qo.admin.AdminUserQO;
//import com.jsy.community.service.IAccountService;
//import com.jsy.community.utils.UserUtils;
//import com.jsy.community.utils.ValidatorUtils;
//import com.jsy.community.vo.CommonResult;
//import io.swagger.annotations.Api;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.util.CollectionUtils;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//
///**
// * @author DKS
// * @description 账号管理
// * @since 2020-11-27 16:02
// **/
//@RequestMapping("/account")
//@Api(tags = "账号管理控制器")
//// @ApiJSYController
//@Slf4j
//@Login
//@RestController
//public class AccountController {
//
//	@Resource
//	private IAccountService accountService;
//
//	/**
//	* @Description: 账号条件查询
//	 * @Param: [baseQO]
//	 * @Return: com.jsy.community.vo.CommonResult
//	 * @Author: chq459799974
//	 * @Date: 2021/3/17
//	**/
//	@Login
//	@PostMapping("query")
//	public CommonResult queryAccount(@RequestBody BaseQO<AdminUserQO> baseQO){
//		if(baseQO.getQuery() == null){
//			baseQO.setQuery(new AdminUserQO());
//		}
//		baseQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
//		baseQO.getQuery().setCommunityIdList(UserUtils.getAdminCommunityIdList());
//		baseQO.getQuery().setCompanyId(UserUtils.getAdminCompanyId());
//		return CommonResult.ok(accountService.queryOperator(baseQO));
//	}
//
//	/**
//	* @Description: 添加账号
//	 * @Param: [adminUserEntity]
//	 * @Return: com.jsy.community.vo.CommonResult
//	 * @Author: chq459799974
//	 * @Date: 2021/3/17
//	**/
//	@Login
//	@PostMapping("")
//	public CommonResult addAccount(@RequestBody AdminUserEntity adminUserEntity){
//		ValidatorUtils.validateEntity(adminUserEntity);
//		accountService.addOperator(adminUserEntity);
//		return CommonResult.ok("添加成功");
//	}
//
//	/**
//	* @Description: 编辑账号
//	 * @Param: [adminUserEntity]
//	 * @Return: com.jsy.community.vo.CommonResult
//	 * @Author: chq459799974
//	 * @Date: 2021/3/17
//	**/
//	@Login
//	@PutMapping("")
//	public CommonResult updateAccount(@RequestBody AdminUserEntity adminUserEntity){
//		if(!CollectionUtils.isEmpty(adminUserEntity.getCommunityIdList())){
//			//验证社区权限
//			UserUtils.validateCommunityIds(adminUserEntity.getCommunityIdList());
//		}
//		accountService.updateOperator(adminUserEntity);
//		return CommonResult.ok("操作成功");
//	}
//
//	/**
//	 * @Description: 删除账号
//	 * @Param: [sysUserEntity]
//	 * @Return: com.jsy.community.vo.CommonResult
//	 * @Author: DKS
//	 * @Date: 2021/10/13
//	 **/
//	@Login
//	@DeleteMapping("delete")
//	public CommonResult deleteAccount(Long id){
//		accountService.deleteOperator(id);
//		return CommonResult.ok("操作成功");
//	}
//}
