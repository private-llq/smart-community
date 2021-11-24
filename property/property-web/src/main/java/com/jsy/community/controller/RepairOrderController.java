package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IRepairOrderService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommonConst;
import com.jsy.community.entity.RepairOrderEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.RepairOrderQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.repair.RepairPlanVO;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
	
	// 不传：查所有  传0：个人报修事项  传1：公共报修事项
	@ApiOperation("报修事项查询")
	@GetMapping("/listRepairType")
	@Permit("community:property:repairOrder:listRepairType")
	public CommonResult listRepairType(@ApiParam("报修类别") Integer typeId) {
		List<CommonConst> constList = repairOrderService.listRepairType(typeId);
		return CommonResult.ok(constList);
	}
	
	@ApiOperation("分页查询所有报修申请")
	@PostMapping("/listRepairOrder")
	@Permit("community:property:repairOrder:listRepairOrder")
	public CommonResult<PageInfo<RepairOrderEntity>> listRepairOrder(@RequestBody BaseQO<RepairOrderQO> repairOrderQO) {
		if (repairOrderQO.getQuery() != null) {
			repairOrderQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
			PageInfo<RepairOrderEntity> pageInfo = repairOrderService.listRepairOrder(repairOrderQO);
			return CommonResult.ok(pageInfo);
		} else {
			repairOrderQO.setQuery(new RepairOrderQO().setCommunityId(UserUtils.getAdminUserInfo().getCommunityId()));
			PageInfo<RepairOrderEntity> pageInfo = repairOrderService.listRepairOrder(repairOrderQO);
			return CommonResult.ok(pageInfo);
		}
	}
	
	@ApiOperation("驳回")
	@GetMapping("/rejectOrder")
	@Permit("community:property:repairOrder:rejectOrder")
	public CommonResult rejectOrder(@ApiParam("报修订单id") @RequestParam Long id,
	                                @ApiParam("驳回原因") String reason) {
		if (reason.length() > 100) {
			throw new PropertyException("驳回原因过长");
		}
		String uid = UserUtils.getAdminUserInfo().getUid();
		String number = UserUtils.getAdminUserInfo().getNumber();
		String realName = UserUtils.getAdminUserInfo().getRealName();
		repairOrderService.rejectOrder(id, reason, uid, number, realName);
		return CommonResult.ok();
	}
	
	
	@ApiOperation("根据id查询报修详情")
	@GetMapping("/getRepairById")
	@Permit("community:property:repairOrder:getRepairById")
	public CommonResult getRepairById(@ApiParam("报修订单id") Long id) {
		RepairOrderEntity orderEntity = repairOrderService.getRepairById(id);
		return CommonResult.ok(orderEntity);
	}
	
	@ApiOperation("维修")
	@GetMapping("/dealOrder")
	@Permit("community:property:repairOrder:dealOrder")
	public CommonResult dealOrder(@ApiParam("报修订单id") @RequestParam Long id,
	                              @ApiParam("处理人id") @RequestParam String dealId,
	                              @ApiParam("费用") BigDecimal money) {
		String uid = UserUtils.getAdminUserInfo().getUid();
		repairOrderService.dealOrder(id, dealId, money, uid);
		return CommonResult.ok();
	}
	
	@ApiOperation("派单人员查询")
	@GetMapping("/getRepairPerson")
	@Permit("community:property:repairOrder:getRepairPerson")
	public CommonResult getRepairPerson(String condition) {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		List<Map<String, Object>> adminUserList = repairOrderService.getRepairPerson(condition, communityId);
		return CommonResult.ok(adminUserList);
	}
	
	@ApiOperation("报修订单设置")
	@PostMapping("/updateOrder")
	@Permit("community:property:repairOrder:updateOrder")
	public CommonResult updateOrder(@ApiParam("报修订单id") Long id) {
		return null;
	}
	
	
	@ApiOperation("完成处理")
	@GetMapping("/successOrder")
	@Permit("community:property:repairOrder:successOrder")
	public CommonResult successOrder(@ApiParam("报修订单id") @RequestParam Long id) {
		String uid = UserUtils.getAdminUserInfo().getUid();
		repairOrderService.successOrder(id, uid);
		return CommonResult.ok();
	}
	
	@ApiOperation("查看进程")
	@GetMapping("/checkCase")
	@Permit("community:property:repairOrder:checkCase")
	public CommonResult checkCase(@ApiParam("报修订单id") @RequestParam Long id) {
		RepairPlanVO repairPlanVO = repairOrderService.checkCase(id);
		return CommonResult.ok(repairPlanVO);
	}
	
	
	
	
	
	
	
	
	// TODO: 2021/3/19 下面两个不用了
	@Deprecated
	@ApiOperation("查看下单人信息")
	@GetMapping("/getUser")
	public CommonResult getUser(@ApiParam("报修订单id") @RequestParam Long id) {
		UserEntity userEntity = repairOrderService.getUser(id);
		return CommonResult.ok(userEntity);
	}
	
	@Deprecated
	@ApiOperation("查看图片信息")
	@GetMapping("/listOrderImg")
	public CommonResult listOrderImg(@ApiParam("报修订单id") @RequestParam Long id) {
		String img = repairOrderService.getOrderImg(id);
		return CommonResult.ok(img);
	}
	
}

