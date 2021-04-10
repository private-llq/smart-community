package com.jsy.community.controller;import com.jsy.community.annotation.ApiJSYController;import com.jsy.community.annotation.auth.Login;import com.jsy.community.api.IAppMenuService;import com.jsy.community.constant.Const;import com.jsy.community.entity.AppMenuEntity;import com.jsy.community.utils.UserUtils;import com.jsy.community.vo.CommonResult;import com.jsy.community.vo.menu.AppMenuVO;import io.swagger.annotations.Api;import io.swagger.annotations.ApiOperation;import lombok.extern.slf4j.Slf4j;import org.apache.dubbo.config.annotation.DubboReference;import org.springframework.web.bind.annotation.*;import java.util.List;/** * <p> * 菜单 前端控制器 * </p> * * @author lihao * @since 2020-11-24 */@Api(tags = "APP物业菜单控制器")@Slf4j@RestController@ApiJSYController@RequestMapping("/community/adminMenu")@Loginpublic class AppMenuController {		@DubboReference(version = Const.version, group = Const.group_property, check = false)	private IAppMenuService appMenuService;		// 查询所有菜单	@ApiOperation("查询所有菜单")	@GetMapping("/listMenu")	public CommonResult listMenu() {		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();		List<AppMenuEntity> list = appMenuService.listMenu(communityId);		return CommonResult.ok(list);	}		// 添加菜单	@ApiOperation("批量添加/编辑菜单")	@PostMapping("/appMenu")	public CommonResult appMenu(@RequestBody List<AppMenuVO> appMenuVOS) {		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();		for (AppMenuVO appMenuVO : appMenuVOS) {			appMenuVO.setCommunityId(communityId);		}		appMenuService.appMenu(appMenuVOS);		return CommonResult.ok();	}}