package com.jsy.community.controller;


import com.jsy.community.entity.AdminMenuEntity;
import com.jsy.community.service.IAdminMenuService;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.menu.FrontParentMenu;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 菜单 前端控制器
 * </p>
 *
 * @author lihao
 * @since 2020-11-24
 */
@Api(tags = "APP菜单控制器")
@Slf4j
@RestController
@RequestMapping("/community/adminMenu")
public class AdminMenuController {
	
	@Autowired
	private IAdminMenuService adminMenuService;
	
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	
	private static final String BUCKETNAME = "app-menu-img"; //暂时写死  后面改到配置文件中  BUCKETNAME命名规范：只能小写，数字，-
	
	@ApiOperation("后台树形结构查询所有菜单")
	@GetMapping("/listAdminMenu")
	public CommonResult<List<FrontParentMenu>> listAdminMenu() {
		List<FrontParentMenu> parentMenus = adminMenuService.listAdminMenu();
		return CommonResult.ok(parentMenus);
	}
	
	@ApiOperation("新增父菜单")
	@PostMapping("/insertAdminMenu")
	public CommonResult insertAdminMenu(@RequestBody AdminMenuEntity adminMenu) {
		ValidatorUtils.validateEntity(adminMenu,AdminMenuEntity.addAdmin.class);
		adminMenuService.insertAdminMenu(adminMenu);
		return CommonResult.ok();
	}
	
	@ApiOperation("删除菜单信息")
	@DeleteMapping("/removeAdminMenu")
	public CommonResult removeAdminMenu(@RequestParam("id") Long id) {
		adminMenuService.removeAdminMenu(id);
		return CommonResult.ok();
	}
	
	@ApiOperation("新增子菜单")
	@PostMapping("/insertChildMenu")
	public CommonResult insertChildMenu(@RequestBody AdminMenuEntity adminMenu){
		ValidatorUtils.validateEntity(adminMenu,AdminMenuEntity.addAdmin.class);
		adminMenuService.insertChildMenu(adminMenu);
		return CommonResult.ok();
	}
	
	@ApiOperation("子菜单图片上传")
	@PostMapping("/uploadMenuImg")
	public CommonResult uploadMenuImg(@RequestParam("file") MultipartFile file){
		try {
			String filePath = MinioUtils.upload(file, BUCKETNAME);
			stringRedisTemplate.opsForSet().add("menu_img_part",filePath);// 文件上传成功后，将其图片名称存入redis
			return CommonResult.ok(filePath);
		} catch (Exception e) {
			e.printStackTrace();
			return CommonResult.error("上传失败");
		}
	}
}

