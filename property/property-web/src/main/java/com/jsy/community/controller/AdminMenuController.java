package com.jsy.community.controller;

import com.jsy.community.api.IAdminConfigService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.admin.AdminMenuEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.admin.AdminMenuQO;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chq459799974
 * @description 系统菜单
 * @since 2020-12-14 10:57
 **/
@RestController
@RequestMapping("menu")
public class AdminMenuController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IAdminConfigService adminConfigService;
	
	/**
	* @Description: 新增
	 * @Param: [sysMenuEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@PostMapping("")
	public CommonResult addMenu(@RequestBody AdminMenuEntity adminMenuEntity){
		ValidatorUtils.validateEntity(adminMenuEntity);
		boolean b = adminConfigService.addMenu(adminMenuEntity);
		return b? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"添加失败");
	}
	
	/**
	* @Description: 级联删除
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@DeleteMapping("")
	public CommonResult delMenu(@RequestParam("id") Long id){
		boolean b = adminConfigService.delMenu(id);
		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"删除失败");
	}
	
	/**
	* @Description: 修改
	 * @Param: [sysMenuQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@PutMapping("")
	public CommonResult updateMenu(@RequestBody AdminMenuQO sysMenuQO){
		boolean b = adminConfigService.updateMenu(sysMenuQO);
		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"修改失败");
	}
	
	/**
	* @Description: 查询
	 * @Param: []
	 * @Return: com.jsy.community.vo.CommonResult<java.util.List<com.jsy.community.entity.sys.AdminMenuEntity>>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@GetMapping("")
	public CommonResult<List<AdminMenuEntity>> listOfMenu(){
		return CommonResult.ok(adminConfigService.listOfMenu());
	}
}