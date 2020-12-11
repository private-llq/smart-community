package com.jsy.community.controller;


import com.jsy.community.entity.PayTypeEntity;
import com.jsy.community.service.IPayTypeService;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 缴费类型 前端控制器
 * </p>
 *
 * @author lihao
 * @since 2020-12-11
 */
@Api(tags = "缴费类型控制器")
@Slf4j
@RestController
@RequestMapping("/payType")
public class PayTypeController {
	
	@Autowired
	private IPayTypeService payTypeService;
	
	@ApiOperation("根据城市id查询所有缴费类型 -- 测试环境")
	@GetMapping("/getPayType")
	public CommonResult<List<PayTypeEntity>> getPayType(@ApiParam("城市id") Long id) {
		List<PayTypeEntity> list = payTypeService.getPayType(id);
		return CommonResult.ok(list);
	}
	
	@ApiOperation("根据城市id添加缴费类型")
	@PostMapping("/addPayType")
	public CommonResult addPayType(@ApiParam("城市id") @RequestParam Long id,
	                               @RequestBody PayTypeEntity payType) {
		payTypeService.addPayType(id, payType);
		return CommonResult.ok();
	}
	
	/**
	 * @return
	 * @Author lihao
	 * @Description 增删改  后面在做
	 * @Date 2020/12/11 14:43
	 * @Param
	 **/
}

