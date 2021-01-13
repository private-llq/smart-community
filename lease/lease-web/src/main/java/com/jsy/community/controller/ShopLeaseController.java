package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IShopLeaseService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseLeaseQO;
import com.jsy.community.qo.shop.ShopQO;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.shop.IndexShopVO;
import com.jsy.community.vo.shop.ShopLeaseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
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
	
	// 商铺头图
	private static final String BUCKETNAME_HEAD = "shop-head";  //暂时写死  后面改到配置文件中  BUCKETNAME命名规范：只能小写，数字，-
	
	// 商铺室内图
	private static final String BUCKETNAME_MIDDLE = "shop-middle";
	
	// 商铺其他图
	private static final String BUCKETNAME_OTHER = "shop-other";
	
	@Login(allowAnonymous = true)
	@ApiOperation("根据筛选条件查询商铺列表")
	@PostMapping("/getShopByCondition")
	public CommonResult getShopByCondition(@RequestBody BaseQO<HouseLeaseQO> baseQO,
	                                       @ApiParam("小区名或地址") @RequestParam(name = "query", required = false) String query,
	                                       @ApiParam("区域id") @RequestParam(required = false, defaultValue = "500103") Integer areaId) {
		PageInfo<IndexShopVO> pageInfo = shopLeaseService.getShopByCondition(baseQO, query, areaId);
		if (pageInfo != null) {
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
		}
		return CommonResult.ok(pageInfo);
	}
	
	@Login(allowAnonymous = true)
	@ApiOperation("商铺头图上传")
	@PostMapping("/uploadHeadImg")
	public CommonResult uploadHeadImg(@RequestParam("file") MultipartFile[] files) {
		String[] filePaths = MinioUtils.uploadForBatch(files, BUCKETNAME_HEAD);
		return CommonResult.ok(filePaths);
	}
	
	@Login(allowAnonymous = true)
	@ApiOperation("商铺室内图上传")
	@PostMapping("/uploadMiddleImg")
	public CommonResult uploadMiddleImg(@RequestParam("file") MultipartFile[] files) {
		String[] filePaths = MinioUtils.uploadForBatch(files, BUCKETNAME_MIDDLE);
		return CommonResult.ok(filePaths);
	}
	
	@Login(allowAnonymous = true)
	@ApiOperation("商铺其他图上传")
	@PostMapping("/uploadOtherImg")
	public CommonResult uploadOtherImg(@RequestParam("file") MultipartFile[] files) {
		String[] filePaths = MinioUtils.uploadForBatch(files, BUCKETNAME_OTHER);
		return CommonResult.ok(filePaths);
	}
	
	@ApiOperation("商铺发布")
	@PostMapping("/addShop")
	public CommonResult addShop(@RequestBody ShopQO shop) {
		shop.setUid(UserUtils.getUserId());
		shop.setSource(1);// 个人发布
		ValidatorUtils.validateEntity(shop, ShopQO.addShopValidate.class);
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

		BigDecimal monthMoney = shop.getMonthMoney();
		if (monthMoney.doubleValue() > 10000d) {
			String s = String.format("%.2f", monthMoney.doubleValue() / 10000) + "万";
			shop.setMonthMoneyString(s);
		} else if (monthMoney.compareTo(new BigDecimal(0.00)) == 0) {
			String s = "面议";
			shop.setMonthMoneyString(s);
		} else {
			String s = "" + shop.getMonthMoney();
			int i = s.lastIndexOf(".");
			String substring = s.substring(0, i) + "元";
			shop.setMonthMoneyString(substring);
		}
		// 当月租金大于10000变成XX.XX万元

		// 当转让费大于10000变成XX.XX万元
		BigDecimal transferMoney = shop.getTransferMoney();
		if (transferMoney.doubleValue() > 10000d) {
			String s = String.format("%.2f", transferMoney.doubleValue() / 10000) + "万";
			shop.setTransferMoneyString(s);
		} else if (transferMoney.compareTo(new BigDecimal(0.00)) == 0) {
			String s = "面议";
			shop.setTransferMoneyString(s);
		} else {
			String s = "" + shop.getMonthMoney();
			int i = s.lastIndexOf(".");
			String substring = s.substring(0, i) + "元";
			shop.setTransferMoneyString(substring);
		}
		// 当转让费大于10000变成XX.XX万元

		map.put("shop",shop);
		return CommonResult.ok(map);
	}
	
	@ApiOperation("商铺修改")
	@PostMapping("/updateShop")
	@Login
	public CommonResult updateShop(@RequestBody ShopQO shop,
	                               @ApiParam("店铺id") @RequestParam Long shopId) {
		shop.setUid(UserUtils.getUserId());
		shop.setSource(1);
		ValidatorUtils.validateEntity(shop, ShopQO.updateShopValidate.class);
		shopLeaseService.updateShop(shop, shopId);
		return CommonResult.ok();
	}


	@ApiOperation("下架商铺")
	@DeleteMapping("/cancelShop")
	@Login
	public CommonResult cancelShop(@ApiParam("店铺id") @RequestParam Long shopId) {
		String userId = UserUtils.getUserId();
		shopLeaseService.cancelShop(userId, shopId);
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
	
	@ApiOperation("更多筛选")
	@GetMapping("/moreOption")
	@Login(allowAnonymous = true)
	public CommonResult moreOption() {
		Map<String,Object> map = shopLeaseService.moreOption();
		return CommonResult.ok(map);
	}

}


