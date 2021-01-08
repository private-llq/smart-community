package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IShopLeaseService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.shop.ShopLeaseEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseLeaseQO;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.shop.IndexShopVO;
import com.jsy.community.vo.shop.ShopDetailLeaseVO;
import com.jsy.community.vo.shop.ShopLeaseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
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
@Login
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
	
	@Login(allowAnonymous = true)
	@ApiOperation("根据筛选条件查询商铺列表")
	@PostMapping("/getShopByCondition")
	public CommonResult getShopByCondition(@RequestBody BaseQO<HouseLeaseQO> baseQO,
	                                       @ApiParam("小区名或地址") @RequestParam(name = "query", required = false) String query,
	                                       @ApiParam("区域id") @RequestParam(required = false, defaultValue = "500103") Integer areaId) {
		PageInfo<IndexShopVO> pageInfo = shopLeaseService.getShopByCondition(baseQO, query, areaId);
		if (pageInfo == null) {
			return CommonResult.ok(null);
		}
		// 当月租金大于10000变成XX.XX万元
		List<IndexShopVO> records = pageInfo.getRecords();
		for (IndexShopVO record : records) {
			if (record.getMonthMoney().doubleValue() > 10000d) {
				String s = String.format("%.2f", record.getMonthMoney().doubleValue() / 10000) + "万";
				record.setMonthMoneyString(s);
			} else if (record.getMonthMoney().compareTo(new BigDecimal(0.00)) == 0) {
				String s = "面议";
				record.setMonthMoneyString(s);
			} else {
				String s = "" + record.getMonthMoney();
				int i = s.lastIndexOf(".");
				String substring = s.substring(0, i) + "元";
				record.setMonthMoneyString(substring);
			}
		}
		// 当月租金大于10000变成XX.XX万元
		return CommonResult.ok(pageInfo);
	}
	
	@Login(allowAnonymous = true)
	@ApiOperation("根据查询条件查询商铺列表")
	@PostMapping("/getShopBySearch")
	public CommonResult getShopBySearch(@RequestBody BaseQO<ShopLeaseEntity> baseQO,
	                                    @ApiParam("小区名或地址") @RequestParam(name = "query", required = false) String query,
	                                    @ApiParam("区域id") @RequestParam(required = false, defaultValue = "500103") Integer areaId) {
		PageInfo<IndexShopVO> pageInfo = shopLeaseService.getShopBySearch(baseQO, query, areaId);
		
		// 当月租金大于10000变成XX.XX万元
		List<IndexShopVO> records = pageInfo.getRecords();
		for (IndexShopVO record : records) {
			if (record.getMonthMoney().doubleValue() > 10000d) {
				String s = String.format("%.2f", record.getMonthMoney().doubleValue() / 10000) + "万";
				record.setMonthMoneyString(s);
			} else if (record.getMonthMoney().compareTo(new BigDecimal(0.00)) == 0) {
				String s = "面议";
				record.setMonthMoneyString(s);
			} else {
				String s = "" + record.getMonthMoney();
				int i = s.lastIndexOf(".");
				String substring = s.substring(0, i) + "元";
				record.setMonthMoneyString(substring);
			}
		}
		// 当月租金大于10000变成XX.XX万元
		return CommonResult.ok(pageInfo);
	}
	
	
	@Login(allowAnonymous = true)
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
	public CommonResult addShop(@RequestBody ShopLeaseVO shop) {
		shop.setUid(UserUtils.getUserId());
		ValidatorUtils.validateEntity(shop, ShopLeaseVO.addShopValidate.class);
		shopLeaseService.addShop(shop);
		return CommonResult.ok();
	}
	
	@ApiOperation("查询店铺详情")
	@GetMapping("/getShop")
	@Login(allowAnonymous = true)
	public CommonResult getShop(@ApiParam("店铺id") @RequestParam Long shopId) {
		Map<String, Object> map = shopLeaseService.getShop(shopId);
		if (map == null) {
			return CommonResult.ok(null);
		}
		
		// 当月租金大于10000变成XX.XX万元
		ShopLeaseVO shop = (ShopLeaseVO) map.get("shop");
		ShopDetailLeaseVO shopDetailLeaseVO = new ShopDetailLeaseVO();
		BeanUtils.copyProperties(shop,shopDetailLeaseVO);
		
		BigDecimal monthMoney = shopDetailLeaseVO.getMonthMoney();
		if (monthMoney.doubleValue() > 10000d) {
			String s = String.format("%.2f", monthMoney.doubleValue() / 10000) + "万";
			shopDetailLeaseVO.setMonthMoneyString(s);
		} else if (monthMoney.compareTo(new BigDecimal(0.00)) == 0) {
			String s = "面议";
			shopDetailLeaseVO.setMonthMoneyString(s);
		} else {
			String s = "" + shop.getMonthMoney();
			int i = s.lastIndexOf(".");
			String substring = s.substring(0, i) + "元";
			shopDetailLeaseVO.setMonthMoneyString(substring);
		}
		// 当月租金大于10000变成XX.XX万元
		
		// 当转让费大于10000变成XX.XX万元
		BigDecimal transferMoney = shopDetailLeaseVO.getTransaferMoney();
		if (transferMoney.doubleValue() > 10000d) {
			String s = String.format("%.2f", transferMoney.doubleValue() / 10000) + "万";
			shopDetailLeaseVO.setTransferMoneyString(s);
		} else if (transferMoney.compareTo(new BigDecimal(0.00)) == 0) {
			String s = "面议";
			shopDetailLeaseVO.setTransferMoneyString(s);
		} else {
			String s = "" + shop.getMonthMoney();
			int i = s.lastIndexOf(".");
			String substring = s.substring(0, i) + "元";
			shopDetailLeaseVO.setTransferMoneyString(substring);
		}
		// 当转让费大于10000变成XX.XX万元
		
		// 封装数据
		Integer furnishingStyle = shop.getFurnishingStyle(); // 装修程度
		if (furnishingStyle == 0) {
			shopDetailLeaseVO.setFurnishingStyle("毛坯房");
		} else if (furnishingStyle == 1) {
			shopDetailLeaseVO.setFurnishingStyle("普通装修");
		} else {
			shopDetailLeaseVO.setFurnishingStyle("精装修");
		}
		
		
		Integer purpose = shop.getPurpose(); // 用途
		if (purpose == 0) {
			shopDetailLeaseVO.setPurpose("普通住宅");
		} else {
			shopDetailLeaseVO.setPurpose("商业住宅");
		}
		
		Integer lift = shop.getLift(); // 电梯
		if (lift == 0) {
			shopDetailLeaseVO.setLift("有");
		} else {
			shopDetailLeaseVO.setLift("无");
		}
		
		Integer ownership = shop.getOwnership(); // 权属
		if (ownership==0) {
			shopDetailLeaseVO.setOwnership("商品房");
		} else if (ownership == 1) {
			shopDetailLeaseVO.setOwnership("学区房");
		} else {
			shopDetailLeaseVO.setOwnership("家住房");
		}
		// 封装数据
		map.put("shop",shopDetailLeaseVO);
		return CommonResult.ok(map);
	}
	
	@ApiOperation("商铺修改")
	@PostMapping("/updateShop")
	@Login
	public CommonResult updateShop(@RequestBody ShopLeaseVO shop,
	                               @ApiParam("店铺id") @RequestParam Long shopId) {
		shop.setUid(UserUtils.getUserId());
		ValidatorUtils.validateEntity(shop, ShopLeaseVO.updateShopValidate.class);
		shopLeaseService.updateShop(shop, shopId);
		return CommonResult.ok();
	}
	
	
	@ApiOperation("下架商铺")
	@DeleteMapping("/cancelShop")
	@Login
	public CommonResult cancelShop(@ApiParam("店铺id") @RequestParam Long shopId,
	                               @ApiParam("社区id") @RequestParam Long communityId,
	                               @ApiParam("房屋id") @RequestParam Long houseId) {
		String userId = UserUtils.getUserId();
		shopLeaseService.cancelShop(userId, shopId, communityId, houseId);
		return CommonResult.ok();
	}
	
	
	@ApiOperation("查询业主发布的房源列表")
	@GetMapping("/listShop")
	@Login
	public CommonResult listShop() {
		String userId = UserUtils.getUserId();
		List<Map<String, Object>> map = shopLeaseService.listShop(userId);
		return CommonResult.ok(map);
	}
	
	@ApiOperation("测试分布式事物---暂时先不删。用于测试")
	@GetMapping("/testTransaction")
	@Login(allowAnonymous = true)
	public CommonResult testTransaction() {
		shopLeaseService.testTransaction();
		return CommonResult.ok();
	}
	
	
}

