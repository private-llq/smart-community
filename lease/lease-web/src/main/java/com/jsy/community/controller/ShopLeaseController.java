package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.constant.Const;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.shop.ShopLeaseVo;
import com.jsy.lease.api.IShopLeaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lihao
 * @since 2020-12-17
 */
@Slf4j
@ApiJSYController
@RestController
@RequestMapping("/shop")
@Api(tags = "商铺租售控制器")
public class ShopLeaseController {
	
	@DubboReference(version = Const.version, group = Const.group_lease, check = false)
	private IShopLeaseService shopLeaseService;
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	private static final String BUCKETNAME = "shop-img"; //暂时写死  后面改到配置文件中  BUCKETNAME命名规范：只能小写，数字，-
	
	@ApiOperation("商铺图片上传")
	@PostMapping("/uploadShopImg")
	public CommonResult addShopImg(@RequestParam("file") MultipartFile[] files) {
		String[] filePaths = MinioUtils.uploadForBatch(files, BUCKETNAME);
		for (String s : filePaths) {
			redisTemplate.opsForSet().add("shop_img_part", s);
		}
		return CommonResult.ok(filePaths);
	}
	
	@ApiOperation("商铺发布")
	@PostMapping("/addShop")
	public CommonResult addShop(@RequestBody ShopLeaseVo shop) {
		shopLeaseService.addShop(shop);
		return CommonResult.ok();
	}
	
	
	@ApiOperation("查询业主自己发布的店铺详情")
	@GetMapping("/getShop")
	public CommonResult getShop(@ApiParam("店铺id") @RequestParam Long shopId) {
		ShopLeaseVo shop = shopLeaseService.getShop(shopId);
		return CommonResult.ok(shop);
	}
	
	@ApiOperation("商铺修改")
	@PostMapping("/updateShop")
	public CommonResult updateShop(@RequestBody ShopLeaseVo shop,
	                               @ApiParam("店铺id") @RequestParam Long shopId) {
		shopLeaseService.updateShop(shop, shopId);
		return CommonResult.ok();
	}
	
	
	@ApiOperation("下架商铺")
	@DeleteMapping("/cancelShop")
	public CommonResult cancelShop(@ApiParam("店铺id") @RequestParam Long shopId) {
		shopLeaseService.cancelShop(shopId);
		return CommonResult.ok();
	}
	
	
	@ApiOperation("查询业主发布的房源列表")
	@GetMapping("/listShop")
	public CommonResult listShop() {
		List<Map<String, Object>> map = shopLeaseService.listShop();
		return CommonResult.ok(map);
	}


//	重庆掌上12333 实业登记  若成功  满一年有补贴

	
}

