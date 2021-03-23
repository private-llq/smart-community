//package com.jsy.community.controller;
//
//
//import com.jsy.community.constant.UploadBucketConst;
//import com.jsy.community.entity.AppMenuEntity;
//import com.jsy.community.service.IAppMenuService;
//import com.jsy.community.utils.MinioUtils;
//import com.jsy.community.utils.ValidatorUtils;
//import com.jsy.community.vo.CommonResult;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
///**
// * <p>
// * 菜单 前端控制器
// * </p>
// *
// * @author lihao
// * @since 2020-11-24
// */
//@Api(tags = "APP大后台菜单控制器")
//@Slf4j
//@RestController
//@RequestMapping("/admin/adminMenu")
//public class AppMenuController {
//
//	@Autowired
//	private IAppMenuService adminMenuService;
//
//	@ApiOperation("后台树形结构查询所有菜单")
//	@GetMapping("/listAdminMenu")
//	public CommonResult<List<FrontParentMenu>> listAdminMenu() {
//		List<FrontParentMenu> parentMenus = adminMenuService.listAdminMenu();
//		return CommonResult.ok(parentMenus);
//	}
//
//	@ApiOperation("子菜单白天图片上传")
//	@PostMapping("/uploadDayMenuImg")
//	public CommonResult uploadDayMenuImg(@RequestParam("file") MultipartFile file) {
//		String upload = MinioUtils.upload(file, UploadBucketConst.APP_MENU_BUCKET);
//		return CommonResult.ok(upload);
//	}
//
//	@ApiOperation("子菜单黑夜图片上传")
//	@PostMapping("/uploadNightMenuImg")
//	public CommonResult uploadNightMenuImg(@RequestParam("file") MultipartFile file, CommonResult result) {
//		String upload = MinioUtils.upload(file, UploadBucketConst.APP_MENU_BUCKET);
//		return CommonResult.ok(upload);
//	}
//
//	@ApiOperation("新增父菜单")
//	@PostMapping("/insertAdminMenu")
//	public CommonResult insertAdminMenu(@RequestBody AppMenuEntity adminMenu) {
//		if (StringUtils.isEmpty(adminMenu.getMenuName())) {
//			return CommonResult.error("父菜单名不能为空");
//		}
//		adminMenuService.insertAdminMenu(adminMenu);
//		return CommonResult.ok();
//	}
//
//	@ApiOperation("新增子菜单")
//	@PostMapping("/insertChildMenu")
//	public CommonResult insertChildMenu(@RequestBody AppMenuEntity adminMenu) {
//		ValidatorUtils.validateEntity(adminMenu, AppMenuEntity.addAdmin.class);
//		adminMenuService.insertChildMenu(adminMenu);
//		return CommonResult.ok();
//	}
//
//	@ApiOperation("删除菜单信息")
//	@DeleteMapping("/removeAdminMenu")
//	public CommonResult removeAdminMenu(@RequestParam("id") Long id) {
//		adminMenuService.removeAdminMenu(id);
//		return CommonResult.ok();
//	}
//
//	@ApiOperation("修改父菜单信息")
//	@PostMapping("/updateAdminMenu")
//	public CommonResult updateAdminMenu(@RequestBody AppMenuEntity adminMenu) {
//		if (StringUtils.isEmpty(adminMenu.getMenuName())) {
//			return CommonResult.error("父菜单名不能为空");
//		}
//		adminMenuService.updateAdminMenu(adminMenu);
//		return CommonResult.ok();
//	}
//}
//
