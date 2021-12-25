package com.jsy.community.controller;

import com.jsy.community.api.IAdminConfigService;
import com.jsy.community.api.IAdminUserService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.admin.AdminMenuEntity;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.base.api.domain.PermitMenu;
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
	
	/**
	 * @Description: 根据角色类型查询物业或者小区菜单
	 * @author: DKS
	 * @since: 2021/12/25 9:32
	 * @Param: [roleType]
	 * @return: com.jsy.community.vo.CommonResult
	 */
	@GetMapping("/page")
	@Permit("community:property:menu:page")
	public CommonResult MenuPage(Integer roleType){
		Map<String, Object> returnMap = new HashMap<>();
		String id = UserUtils.getId();
		List<PermitMenu> permitMenus = adminConfigService.MenuPage(roleType, Long.valueOf(id));
		returnMap.put("allMenu", permitMenus);
		return CommonResult.ok(returnMap);
	}
}
