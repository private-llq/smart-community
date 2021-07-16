//package com.jsy.community.controller;
//
//
//import com.jsy.community.annotation.ApiJSYController;
//import com.jsy.community.annotation.auth.Login;
//import com.jsy.community.api.IUserHouseService;
//import com.jsy.community.constant.Const;
//import com.jsy.community.entity.UserHouseEntity;
//import com.jsy.community.qo.BaseQO;
//import com.jsy.community.utils.PageInfo;
//import com.jsy.community.vo.CommonResult;
//import com.jsy.community.vo.UserHouseVO;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import org.apache.dubbo.config.annotation.DubboReference;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
///**
// * <p>
// * 业主房屋认证 前端控制器
// * </p>
// *
// * @author lihao
// * @since 2020-11-25
// */
//@Api(tags = "业主房屋认证控制器")
//@RestController
//@RequestMapping("/userHouse")
//@Login(allowAnonymous = true)
//@ApiJSYController
//public class UserHouseController {
//
//	@DubboReference(version = Const.version, group = Const.group_property, check = false)
//	private IUserHouseService userHouseService;
//
//	/**
//	 * 待审核
//	 **/
//	private static final Integer WAITPASS = 0;
//
//	/**
//	 * 已通过
//	 **/
//	private static final Integer PASS = 1;
//
//	/**
//	 * 未通过
//	 **/
//	private static final Integer NOPASS = 2;
//
//	@ApiOperation("业主房屋认证审核列表")
//	@PostMapping("/selectUserHouse")
//	public CommonResult<PageInfo<UserHouseVO>> selectUserHouse(@RequestBody BaseQO<UserHouseEntity> baseQO,
//	                                                           @ApiParam(value = "社区id")
//	                                                           @RequestParam Long communityId) {
//		PageInfo<UserHouseVO> page = userHouseService.selectUserHouse(baseQO, communityId);
//		List<UserHouseVO> houseVOList = page.getRecords();
//		for (UserHouseVO userHouseVO : houseVOList) {
//			if (userHouseVO.getCheckStatus().equals(WAITPASS)) {
//				userHouseVO.setCheckStatusString("待审核");
//			} else if (userHouseVO.getCheckStatus().equals(PASS)) {
//				userHouseVO.setCheckStatusString("通过");
//			} else {
//				userHouseVO.setCheckStatusString("未通过");
//			}
//		}
//		return CommonResult.ok(page);
//	}
//
//	@ApiOperation("通过审核")
//	@GetMapping("/pass")
//	public CommonResult<Boolean> pass(@ApiParam(value = "待审核房屋id") @RequestParam Long id) {
//		// 房屋通过认证
//		Boolean b = userHouseService.pass(id);
//		return b ? CommonResult.ok() : CommonResult.error("您的订单不存在或已经审核完成");
//	}
//
//	@ApiOperation("不通过审核")
//	@GetMapping("/notPass")
//	public CommonResult<Boolean> notPass(@ApiParam(value = "待审核房屋id") @RequestParam Long id) {
//		Boolean b = userHouseService.notPass(id);
//		return b ? CommonResult.ok() : CommonResult.error("您的订单不存在或已经审核完成");
//	}
//
//
//}
//
