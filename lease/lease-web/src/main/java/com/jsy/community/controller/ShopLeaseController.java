package com.jsy.community.controller;


import com.alibaba.fastjson.JSON;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.Log;
import com.jsy.community.annotation.RequireRecentBrowse;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IShopLeaseService;
import com.jsy.community.api.LeaseException;
import com.jsy.community.constant.*;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.log.ProprietorLog;
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
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
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
	
	/**
	 * @Author lihao
	 * @Description 头图最大数量
	 * @Date 2021/1/13 15:49
	 **/
	private static final Integer HEAD_MAX = 3;
	
	/**
	 * @Author lihao
	 * @Description 室内图最大数量
	 * @Date 2021/1/13 15:50
	 **/
	private static final Integer MIDDLE_MAX = 8;
	
	/**
	 * @Author lihao
	 * @Description 其他图最大数量
	 * @Date 2021/1/13 15:50
	 **/
	private static final Integer OTHER_MAX = 8;
	
	/**
	 * @Author lihao
	 * @Description 金额临界值  大于此值变成 XX万
	 * @Date 2021/1/13 15:55
	 **/
	private static final double NORM_MONEY = 10000.00;
	
	/**
	 * @Author lihao
	 * @Description 金额临界值  等于此值变成 面议
	 * @Date 2021/1/13 15:55
	 **/
	private static final double MIN_MONEY = 0.00;
	
	/**
	 * @Author lihao
	 * @Description 商铺发布图片最大临界值
	 * @Date 2021/2/7 15:55
	 **/
	private static final Integer IMG_MAX = 19;
	
	@Login(allowAnonymous = true)
	@ApiOperation("根据筛选条件查询商铺列表")
	@PostMapping("/getShopByCondition")
	public CommonResult<PageInfo> getShopByCondition(@RequestBody BaseQO<HouseLeaseQO> baseQO,
	                                       @ApiParam("小区名或地址") @RequestParam(name = "query", required = false) String query,
	                                       @ApiParam("区域id") @RequestParam(required = false, defaultValue = "500103") Integer areaId) {
		PageInfo<IndexShopVO> pageInfo = shopLeaseService.getShopByCondition(baseQO, query, areaId);
		if (pageInfo != null) {
			// 当月租金大于10000变成XX.XX万元
			List<IndexShopVO> records = pageInfo.getRecords();
			for (IndexShopVO record : records) {
				if (record.getMonthMoney().doubleValue() > NORM_MONEY) {
					String s = String.format("%.2f", record.getMonthMoney().doubleValue() / NORM_MONEY) + "万";
					record.setMonthMoneyString(s);
				} else if (record.getMonthMoney().compareTo(new BigDecimal(MIN_MONEY)) == 0) {
					String s = "面议";
					record.setMonthMoneyString(s);
				} else {
					String s = "" + record.getMonthMoney();
					int i = s.lastIndexOf(".");
					String substring = s.substring(0, i) + "元";
					record.setMonthMoneyString(substring);
				}
			}
		} else {
			return CommonResult.ok(new PageInfo<IndexShopVO>().setCurrent(baseQO.getPage()).setSize(baseQO.getSize()));
		}
		return CommonResult.ok(pageInfo);
	}
	
	@Login(allowAnonymous = true)
	@ApiOperation("商铺头图上传")
	@PostMapping("/uploadHeadImg")
	public CommonResult uploadHeadImg(@RequestParam("file") MultipartFile[] files) {
		if (files.length > HEAD_MAX) {
			return CommonResult.error("头图最多上传3张图");
		}
		String[] strings = MinioUtils.uploadForBatch(files, UploadBucketConst.SHOP_HEAD_BUCKET);
		for (String string : strings) {
			redisTemplate.opsForSet().add(UploadRedisConst.SHOP_HEAD_IMG_PART, string);
		}
		return CommonResult.ok(strings);
	}
	
	@Login(allowAnonymous = true)
	@ApiOperation("商铺室内图上传")
	@PostMapping("/uploadMiddleImg")
	public CommonResult uploadMiddleImg(@RequestParam("file") MultipartFile[] files) {
		if (files.length > MIDDLE_MAX) {
			return CommonResult.error("室内图最多上传8张图");
		}
		String[] filePaths = MinioUtils.uploadForBatch(files, UploadBucketConst.SHOP_MIDDLE_BUCKET);
		for (String filePath : filePaths) {
			redisTemplate.opsForSet().add(UploadRedisConst.SHOP_MIDDLE_IMG_PART, filePath);
		}
		return CommonResult.ok(filePaths);
	}
	
	@Login(allowAnonymous = true)
	@ApiOperation("商铺其他图上传")
	@PostMapping("/uploadOtherImg")
	public CommonResult uploadOtherImg(@RequestParam("file") MultipartFile[] files) {
		if (files.length > OTHER_MAX) {
			return CommonResult.error("其他图最多上传8张图");
		}
		String[] filePaths = MinioUtils.uploadForBatch(files, UploadBucketConst.SHOP_OTHER_BUCKET);
		for (String filePath : filePaths) {
			redisTemplate.opsForSet().add(UploadRedisConst.SHOP_OTHER_IMG_PART, filePath);
		}
		return CommonResult.ok(filePaths);
	}
	
	
	@ApiOperation("商铺发布")
	@PostMapping("/addShop")
	@Log(operationType = LogTypeConst.INSERT, module = LogModule.LEASE, isSaveRequestData = true)
	public CommonResult addShop(@RequestBody ShopQO shop) {
		String[] imgPath = shop.getImgPath();
		if (imgPath == null || imgPath.length <= 0) {
			throw new LeaseException("请添加所有图片");
		}
		if (imgPath.length > IMG_MAX) {
			throw new LeaseException("您添加的图片数量过多");
		}
		// 图片名称必须是 shop-head-img 或 shop-middle-img 或 shop-other-img组成的
		for (String s : imgPath) {
			boolean b = s.contains("shop-head-img") || s.contains("shop-middle-img") || s.contains("shop-other-img");
			if (!b) {
				throw new LeaseException("您添加的图片不符合规范，请重新添加");
			}
		}
		
		// 必须要至少有一个 shop-head-img 和 shop-middle-img 和 shop-other-img
		List<String> heads = new ArrayList<>();
		List<String> middles = new ArrayList<>();
		List<String> others = new ArrayList<>();
		int headCount = 0;
		int middleCount = 0;
		int otherCount = 0;
		for (String s : imgPath) {
			if (s.contains("shop-head-img")) {
				heads.add(s);
				headCount += 1;
			}
			if (s.contains("shop-middle-img")) {
				middles.add(s);
				middleCount += 1;
			}
			if (s.contains("shop-other-img")) {
				others.add(s);
				otherCount += 1;
			}
		}
		if (headCount == 0 || middleCount == 0 || otherCount == 0) {
			throw new LeaseException("请添加所有图片");
		}
		if (headCount > HEAD_MAX) {
			throw new LeaseException("头图数量超过规定数量");
		}
		if (headCount > MIDDLE_MAX) {
			throw new LeaseException("室内图数量超过规定数量");
		}
		if (headCount > OTHER_MAX) {
			throw new LeaseException("其他图数量超过规定数量");
		}
		
		shop.setUid(UserUtils.getUserId());
		// 业主发布
		shop.setSource(1);
		ValidatorUtils.validateEntity(shop, ShopQO.addShopValidate.class);
		shopLeaseService.addShop(shop);
		
		// 对图片进行处理
		for (String head : heads) {
			redisTemplate.opsForSet().add(UploadRedisConst.SHOP_HEAD_IMG_ALL, head);
		}
		for (String middle : middles) {
			redisTemplate.opsForSet().add(UploadRedisConst.SHOP_MIDDLE_IMG_ALL, middle);
		}
		for (String other : others) {
			redisTemplate.opsForSet().add(UploadRedisConst.SHOP_OTHER_IMG_ALL, other);
		}
		
		return CommonResult.ok();
	}
	
	
	@ApiOperation("查询店铺详情")
	@RequireRecentBrowse
	@GetMapping("/getShop")
//	@Login(allowAnonymous = true)
	public CommonResult getShop(@ApiParam("店铺id") @RequestParam Long shopId) {
		Map<String, Object> map = shopLeaseService.getShop(shopId);
		if (map == null) {
			return CommonResult.ok(null);
		}
		
		// 当月租金大于10000变成XX.XX万元
		ShopLeaseVO shop = (ShopLeaseVO) map.get("shop");
		
		BigDecimal monthMoney = shop.getMonthMoney();
		if (monthMoney.doubleValue() > NORM_MONEY) {
			String s = String.format("%.2f", monthMoney.doubleValue() / NORM_MONEY) + "万";
			shop.setMonthMoneyString(s);
		} else if (monthMoney.compareTo(new BigDecimal(MIN_MONEY)) == 0) {
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
		if (transferMoney.doubleValue() > NORM_MONEY) {
			String s = String.format("%.2f", transferMoney.doubleValue() / NORM_MONEY) + "万";
			shop.setTransferMoneyString(s);
			
		} else if (transferMoney.doubleValue() < MIN_MONEY) {
			String s = "面议";
			shop.setTransferMoneyString(s);
		} else {
			String s = "" + shop.getTransferMoney();
			int i = s.lastIndexOf(".");
			String substring = s.substring(0, i) + "元";
			shop.setTransferMoneyString(substring);
		}
		// 当转让费大于10000变成XX.XX万元
		
		map.put("shop", shop);
		return CommonResult.ok(map);
	}
	
	@ApiOperation("商铺修改")
	@PostMapping("/updateShop")
	@Login
	@Log(operationType = LogTypeConst.UPDATE, module = LogModule.LEASE, isSaveRequestData = true)
	public CommonResult updateShop(@RequestBody ShopQO shop) {
		String[] imgPath = shop.getImgPath();
		
		if (imgPath == null || imgPath.length <= 0) {
			throw new LeaseException("请添加所有图片");
		}
		
		// 图片名称必须是 shop-head-img 或 shop-middle-img 或 shop-other-img组成的
		for (String s : imgPath) {
			boolean b = s.contains("shop-head-img") || s.contains("shop-middle-img") || s.contains("shop-other-img");
			if (!b) {
				throw new LeaseException("您添加的图片不符合规范，请重新添加");
			}
		}
		// 必须要至少有一个 shop-head-img 和 shop-middle-img 和 shop-other-img
		int headCount = 0;
		int middleCount = 0;
		int otherCount = 0;
		for (String s : imgPath) {
			if (s.contains("shop-head-img")) {
				headCount += 1;
			}
			if (s.contains("shop-middle-img")) {
				middleCount += 1;
			}
			if (s.contains("shop-other-img")) {
				otherCount += 1;
			}
		}
		if (headCount == 0 || middleCount == 0 || otherCount == 0) {
			throw new LeaseException("请添加所有图片");
		}
		
		shop.setUid(UserUtils.getUserId());
		shop.setSource(1);
		ValidatorUtils.validateEntity(shop, ShopQO.updateShopValidate.class);
		shopLeaseService.updateShop(shop);
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
		if (map.size() == 0) {
			return CommonResult.ok("你还没有发布过房源");
		}
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
		Map<String, Object> map = shopLeaseService.moreOption();
		return CommonResult.ok(map);
	}
	
	@ApiOperation("查询商铺类型和行业[发布的时候添加]")
	@GetMapping("/getPublishTags")
	@Login(allowAnonymous = true)
	public CommonResult getPublishTags() {
		Map<String, Object> map = shopLeaseService.getPublishTags();
		return CommonResult.ok(map);
	}
	
	@ApiOperation("根据区域id查询小区列表")
	@GetMapping("/getCommunity")
	public CommonResult getCommunity(Long areaId) {
		List<CommunityEntity> communityList = shopLeaseService.getCommunity(areaId);
		return CommonResult.ok(communityList);
	}
	
	@ApiOperation("测试httpclient")
	@PostMapping("/httpclient")
	@Login(allowAnonymous = true)
	public CommonResult getHttpclient(@RequestBody ProprietorLog log) {
		System.out.println(log);
		System.out.println("1");
		return CommonResult.ok();
	}
	
	/**
	 * POST---有参测试(对象参数)
	 *
	 * @date 2018年7月13日 下午4:18:50
	 */
	public static void mai2n(String[] args) {
		// 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		
		// 创建Post请求
		HttpPost httpPost = new HttpPost("http://localhost:8001/api/v1/lease/shop/httpclient");
		ProprietorLog user = new ProprietorLog();
		user.setName("潘晓婷");
		
		// 我这里利用阿里的fastjson，将Object转换为json字符串;
		// (需要导入com.alibaba.fastjson.JSON包)
		String jsonString = JSON.toJSONString(user);
		
		StringEntity entity = new StringEntity(jsonString, "UTF-8");
		
		// post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
		httpPost.setEntity(entity);
		
		httpPost.setHeader("Content-Type", "application/json;charset=utf8");
		
		// 响应模型
		CloseableHttpResponse response = null;
		try {
			// 由客户端执行(发送)Post请求
			response = httpClient.execute(httpPost);
			// 从响应模型中获取响应实体
			HttpEntity responseEntity = response.getEntity();
			
			System.out.println("响应状态为:" + response.getStatusLine());
			if (responseEntity != null) {
				System.out.println("响应内容长度为:" + responseEntity.getContentLength());
				System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 释放资源
				if (httpClient != null) {
					httpClient.close();
				}
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	//********************************
	public static RestHighLevelClient getClient() {
		HttpHost httpHost = new HttpHost("127.0.0.1", 9200);
		RestClientBuilder clientBuilder = RestClient.builder(httpHost);
		RestHighLevelClient client = new RestHighLevelClient(clientBuilder);
		return client;
	}
	//
	
	public static void main3(String[] args) {
		RestHighLevelClient client = ShopLeaseController.getClient();
		System.out.println(client);
	}
	
	public static void main(String[] args) throws IOException {
		//1. 准备关于索引的settings
		Settings settings = Settings.builder()
			.put("number_of_shards", 3)
			.put("number_of_replicas", 1).build();
		
		//2. 准备关于索引的结构mappings
		XContentBuilder mappings = JsonXContent.contentBuilder()
			.startObject()
			.startObject("properties")
			.startObject("name")
			.field("type", "text")
			.endObject()
			.startObject("age")
			.field("type", "integer")
			.endObject()
			.startObject("birthday")
			.field("type", "date")
			.field("format", "yyyy-MM-dd")
			.endObject()
			.endObject()
			.endObject();
		
		//3. 将settings和mappings封装到一个Request对象
		CreateIndexRequest request = new CreateIndexRequest("person")
			.settings(settings)
			.mapping("man", mappings);
		
		//4. 通过client对象去连接ES并执行创建索引
		RestHighLevelClient client = ShopLeaseController.getClient();
		CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
		System.out.println(response.toString());
	}
}


