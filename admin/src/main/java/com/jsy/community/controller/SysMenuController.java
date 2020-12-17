package com.jsy.community.controller;

import com.jsy.community.entity.sys.SysMenuEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.sys.SysMenuQO;
import com.jsy.community.service.ISysConfigService;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chq459799974
 * @description 系统菜单
 * @since 2020-12-14 10:57
 **/
@RestController
@RequestMapping("menu")
public class SysMenuController {
	
	@Autowired
	private ISysConfigService ISysConfigService;
	
	/**
	* @Description: 新增
	 * @Param: [sysMenuEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@PostMapping("")
	public CommonResult addMenu(@RequestBody SysMenuEntity sysMenuEntity){
		ValidatorUtils.validateEntity(sysMenuEntity);
		boolean b = ISysConfigService.addMenu(sysMenuEntity);
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
		boolean b = ISysConfigService.delMenu(id);
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
	public CommonResult updateMenu(@RequestBody SysMenuQO sysMenuQO){
		boolean b = ISysConfigService.updateMenu(sysMenuQO);
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
	public CommonResult<List<SysMenuEntity>> listOfMenu(){
		return CommonResult.ok(ISysConfigService.listOfMenu());
	}
}
