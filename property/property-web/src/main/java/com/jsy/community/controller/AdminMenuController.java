package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IAdminConfigService;
import com.jsy.community.api.IAdminUserService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.admin.AdminMenuEntity;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chq459799974
 * @description 系统菜单
 * @since 2020-12-14 10:57
 **/
@RestController
@RequestMapping("menu")
// @ApiJSYController
public class AdminMenuController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IAdminConfigService adminConfigService;
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IAdminUserService adminUserService;
	
	//TODO 准备挪到大后台，物业端不提供该接口
//	/**
//	* @Description: 新增
//	 * @Param: [sysMenuEntity]
//	 * @Return: com.jsy.community.vo.CommonResult
//	 * @Author: chq459799974
//	 * @Date: 2020/12/14
//	**/
//	@PostMapping("")
//	public CommonResult addMenu(@RequestBody AdminMenuEntity adminMenuEntity){
//		ValidatorUtils.validateEntity(adminMenuEntity);
//		boolean b = adminConfigService.addMenu(adminMenuEntity);
//		return b? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"添加失败");
//	}
	
	//TODO 准备挪到大后台，物业端不提供该接口
//	/**
//	* @Description: 级联删除
//	 * @Param: [id]
//	 * @Return: com.jsy.community.vo.CommonResult
//	 * @Author: chq459799974
//	 * @Date: 2020/12/14
//	**/
//	@DeleteMapping("")
//	public CommonResult delMenu(@RequestParam("id") Long id){
//		boolean b = adminConfigService.delMenu(id);
//		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"删除失败");
//	}
	
	//TODO 准备挪到大后台，物业端不提供该接口
//	/**
//	* @Description: 修改
//	 * @Param: [sysMenuQO]
//	 * @Return: com.jsy.community.vo.CommonResult
//	 * @Author: chq459799974
//	 * @Date: 2020/12/14
//	**/
//	@PutMapping("")
//	public CommonResult updateMenu(@RequestBody AdminMenuQO sysMenuQO){
//		boolean b = adminConfigService.updateMenu(sysMenuQO);
//		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"修改失败");
//	}
	
	//=================================== 物业端新版 begin ============================
	/**
	* @Description: 查询
	 * @Param: []
	 * @Return: com.jsy.community.vo.CommonResult<java.util.List<com.jsy.community.entity.sys.AdminMenuEntity>>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@GetMapping("")
	@LoginIgnore
	@Permit("community:property:menu")
	public CommonResult listOfMenu(Long id){
		Map<String, Object> returnMap = new HashMap<>(16);
		List<AdminMenuEntity> allMenu = adminConfigService.listOfMenu();
		returnMap.put("allMenu",allMenu);
		if(id != null){
			List<String> userMenu = adminUserService.queryUserMenuIdList(id);
			if(!CollectionUtils.isEmpty(userMenu)){
				returnMap.put("userMenu",userMenu);
			}
		}
		return CommonResult.ok(returnMap,"查询成功");
	}
	
	//=================================== 物业端新版 end ============================
}
