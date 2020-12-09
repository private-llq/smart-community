package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IRepairOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.RepairOrderEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 报修订单信息 前端控制器
 * </p>
 *
 * @author lihao
 * @since 2020-12-08
 */
@Api(tags = "报修订单控制器")
@Slf4j
@RestController
@ApiJSYController
@RequestMapping("/repairOrder")
public class RepairOrderController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IRepairOrderService repairOrderService;
	
	/**
	 * @return com.jsy.community.vo.CommonResult<java.util.List<com.jsy.community.entity.RepairOrderEntity>>
	 * @Author lihao
	 * @Description 没有写查询条件  测试
	 * @Date 2020/12/9 15:52
	 * @Param [communityId]
	 **/
	@ApiOperation("查询所有报修申请")
	@GetMapping("/listRepairOrder")
	public CommonResult<List<RepairOrderEntity>> listRepairOrder(@ApiParam("社区id") @RequestParam Long communityId) {
		List<RepairOrderEntity> list = repairOrderService.listRepairOrder(communityId);
		return CommonResult.ok(list);
	}
	
	@ApiOperation("立即处理")
	@GetMapping("/dealOrder")
	public CommonResult dealOrder(@ApiParam("报修订单id") @RequestParam Long id){
		repairOrderService.dealOrder(id);
		return CommonResult.ok();
	}
	
	@ApiOperation("完成处理")
	@GetMapping("/successOrder")
	public CommonResult successOrder(@ApiParam("报修订单id") @RequestParam Long id){
		repairOrderService.successOrder(id);
		return CommonResult.ok();
	}
	
	@ApiOperation("查看下单人信息")
	@GetMapping("/getUser")
	public CommonResult getUser(@ApiParam("报修订单id") @RequestParam Long id){
		UserEntity userEntity = repairOrderService.getUser(id);
		return CommonResult.ok(userEntity);
	}
	
	@ApiOperation("查看图片信息")
	@GetMapping("/listOrderImg")
	public CommonResult listOrderImg(@ApiParam("报修订单id") @RequestParam Long id){
		String filePath = repairOrderService.listOrderImg(id);
		return CommonResult.ok(filePath);
	}
	
	
}

