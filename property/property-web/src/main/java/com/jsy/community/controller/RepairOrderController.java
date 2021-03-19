package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IRepairOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommonConst;
import com.jsy.community.entity.RepairOrderEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.RepairOrderQO;
import com.jsy.community.utils.PageInfo;
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
	
	@ApiOperation("报修事项查询")
	@GetMapping("/listRepairType")
	public CommonResult listRepairType(@ApiParam("报修类别") Integer typeId) {  // TODO: 2021/3/16  不传：查所有  传0：个人报修事项  传1：公共报修事项
		List<CommonConst> constList = repairOrderService.listRepairType(typeId);
		return CommonResult.ok(constList);
	}
	
	@ApiOperation("分页查询所有报修申请")
	@PostMapping("/listRepairOrder") // TODO: 2021/3/18 时间查询那里好像没通过测试
	public CommonResult<PageInfo<RepairOrderEntity>> listRepairOrder(@RequestBody BaseQO<RepairOrderQO> repairOrderQO) {
		PageInfo<RepairOrderEntity> pageInfo = repairOrderService.listRepairOrder(repairOrderQO);
		return CommonResult.ok(pageInfo);
	}
	
	// TODO: 2021/3/19 用于设置   派单人信息没有回显
	@ApiOperation("根据id查询报修详情")
	@GetMapping("/getRepairById")
	public CommonResult getRepairById(@ApiParam("报修订单id") Long id) {
		RepairOrderEntity orderEntity = repairOrderService.getRepairById(id);
		return CommonResult.ok(orderEntity);
	}
	
	@ApiOperation("立即处理")
	@GetMapping("/dealOrder")
	// TODO: 2021/3/18 还没做有派单功能  以及 设置费用
	public CommonResult dealOrder(@ApiParam("报修订单id") @RequestParam Long id) {
		repairOrderService.dealOrder(id);
		return CommonResult.ok();
	}
	
	// TODO: 2021/3/19  根据id 更改订单的派单人信息 与 费用
	@ApiOperation("报修订单设置")
	@PostMapping("/updateOrder")
	public CommonResult updateOrder(@ApiParam("报修订单id") Long id){
		return  null;
	}
	
	@ApiOperation("完成处理")
	@GetMapping("/successOrder")
	public CommonResult successOrder(@ApiParam("报修订单id") @RequestParam Long id) {
		repairOrderService.successOrder(id);
		return CommonResult.ok();
	}
	
	@ApiOperation("查看进程")
	@GetMapping("/checkCase")
	public CommonResult checkCase(@ApiParam("报修订单id") @RequestParam Long id){
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	// TODO: 2021/3/19 下面两个不用了
	@ApiOperation("查看下单人信息")
	@GetMapping("/getUser")
	public CommonResult getUser(@ApiParam("报修订单id") @RequestParam Long id) {
		UserEntity userEntity = repairOrderService.getUser(id);
		return CommonResult.ok(userEntity);
	}
	
	@ApiOperation("查看图片信息")
	@GetMapping("/listOrderImg")
	public CommonResult listOrderImg(@ApiParam("报修订单id") @RequestParam Long id) {
		String img = repairOrderService.getOrderImg(id);
		return CommonResult.ok(img);
	}
	
	
}

