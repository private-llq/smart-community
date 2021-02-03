//package com.jsy.community.controller;
//
//
//import com.jsy.community.annotation.ApiJSYController;
//import com.jsy.community.annotation.auth.Login;
//import com.jsy.community.api.IIndexMenuService;
//import com.jsy.community.constant.Const;
//import com.jsy.community.entity.IndexMenuEntity;
//import com.jsy.community.vo.CommonResult;
//import com.jsy.community.vo.menu.FrontParentMenu;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import org.apache.dubbo.config.annotation.DubboReference;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
///**
// * @return
// * @Author lihao
// * @Description 前台菜单控制器
// * @Date 2020/11/14 22:10
// * @Param
// **/
//@Api(tags = "app前台菜单控制器")
//@RestController
//@RequestMapping("/menu")
//@Login(allowAnonymous = true)
//@ApiJSYController
//public class IndexMenuController {
//
//	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
//	private IIndexMenuService menuService;
//
//	@Autowired
//	private StringRedisTemplate redisTemplate;
//
//	// TODO: 2021/2/3 这里应该设计成  打开app，如果没有选择社区  就不展示菜单  而不是默认走社区1的菜单
//	@ApiOperation("查询首页展示的菜单选项")
//	@GetMapping("/listIndexMenu")
//	public CommonResult listIndexMenu(@ApiParam(value = "社区id")
//	                                  @RequestParam(value = "communityId", defaultValue = "1", required = false) Long communityId) {
//		List<IndexMenuEntity> list = menuService.listIndexMenu(communityId);
//		return CommonResult.ok(list);
//
//	}
//
//	@ApiOperation("更多菜单")
//	@GetMapping("/moreListMenu")
//	public CommonResult moreListMenu(@ApiParam(value = "社区id")
//	                                 @RequestParam(value = "communityId", defaultValue = "1", required = false) Long communityId) {
//		List<FrontParentMenu> list = menuService.moreIndexMenu(communityId);
//		return CommonResult.ok(list);
//	}
//
//}
//
