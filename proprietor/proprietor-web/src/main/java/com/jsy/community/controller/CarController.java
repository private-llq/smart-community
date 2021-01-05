package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICarService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.CarQO;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author YuLF
 * @date 2020/11/10 10:56
 */
@Api(tags = "车辆控制器")
@RestController
@RequestMapping("/car")
@Slf4j
@ApiJSYController
public class CarController {
	
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private ICarService carService;
	
	//允许上传的文件后缀种类，临时，后续从Spring配置文件中取值
	private final String[] carImageAllowSuffix = new String[]{"jpg", "jpeg", "png"};

	private static final String BUCKET_NAME = "car-img"; //暂时写死  后面改到配置文件中  BUCKETNAME命名规范：只能小写，数字，-
	
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	
	
	/**
	 * 新增业主固定车辆
	 *
	 * @param carEntity 前端参数对象
	 * @return 返回新增结果
	 */
	@Login
	@ApiOperation("新增固定车辆登记方法")
	@PostMapping(produces = "application/json;charset=utf-8")
	public CommonResult<Boolean> addProprietorCar(@RequestBody CarEntity carEntity) {
		//0.从JWT取uid
		carEntity.setUid(UserUtils.getUserId());
		//1.效验前端新增车辆参数合法性
		ValidatorUtils.validateEntity(carEntity, CarEntity.addCarValidated.class);
		Integer integer = carService.addProprietorCar(carEntity);
		String filePath = carEntity.getCarImageUrl();
		if (!StringUtils.isEmpty(filePath)) {
			stringRedisTemplate.opsForSet().add("car_img_all",filePath); // 将图片地址存入redis 用于对比 便于清理无用图片
		}
		//3.登记新增车辆操作
		return integer > 0 ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
	}
	
	
	/**
	 * 修改业主固定车辆
	 *
	 * @param carQO 前端请求参数对象
	 * @return 返回修改影响行数
	 */
	@Login
	@ApiOperation(value = "修改固定车辆方法", produces = "application/json;charset=utf-8")
	@PutMapping()
	public CommonResult<Boolean> updateProprietorCar(@RequestBody CarQO carQO) {
		//效验前端新增车辆参数合法性
		ValidatorUtils.validateEntity(carQO, CarQO.updateCarValidated.class);
		Integer integer = carService.updateProprietorCar(carQO, UserUtils.getUserId());
		return integer > 0 ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
	}
	
	@Login
	@ApiOperation("所属人固定车辆查询方法")
	@PostMapping(value = "/page", produces = "application/json;charset=utf-8")
	public CommonResult<?> queryProprietorCar(@RequestBody BaseQO<CarEntity> carEntityBaseQO) {
		if (null == carEntityBaseQO.getQuery()) {
			return CommonResult.error("没有选择社区!");
		}
		carEntityBaseQO.getQuery().setUid(UserUtils.getUserId());
		//1.查询参数非空数字效验
		ValidatorUtils.validatePageParam(carEntityBaseQO);
		List<CarEntity> records = carService.queryProprietorCar(carEntityBaseQO).getRecords();
		return CommonResult.ok(records);
	}
	
	
	/**
	 * 通过车辆ID 删除 车辆方法
	 * 使用String类型接受，手动效验id，避免参数类型错误直接抛出500无信息提示
	 *
	 * @param id 车辆id
	 * @return 返回逻辑删除影响行
	 */
	@Login
	@ApiOperation("所属人固定车辆删除方法")
	@ApiImplicitParam(name = "id", value = "车辆固定id")
	@DeleteMapping()
	public CommonResult<Boolean> deleteProprietorCar(@RequestParam String id) {
		//从请求获得uid
		if (!ValidatorUtils.isInteger(id)) {
			return CommonResult.error(JSYError.REQUEST_PARAM);
		}
		//条件参数列表 key=列名  增加删除车辆记录 where条件只改动这里
		Map<String, Object> params = new HashMap<>();
		params.put("uid", UserUtils.getUserId());
		params.put("id", id);
		Integer res = carService.deleteProprietorCar(params);
		return res > 0 ? CommonResult.ok() : CommonResult.error(JSYError.DUPLICATE_KEY.getCode(), "车辆不存在!");
	}
	
	/**
	 * 车辆图片上传接口
	 *
	 * @param carImage 车辆图片
	 * @return 返回图片上传成功后的访问路径地址
	 */
	@Login
	@ApiOperation("所属人车辆图片上传接口")
	@ApiImplicitParam(name = "carImage", value = "车辆图片文件")
	@PostMapping(value = "carImageUpload")
	public CommonResult<?> carImageUpload(@RequestParam("carImage") MultipartFile carImage, HttpServletRequest request)  {
		//1.接口非空验证
		if (null == carImage) {
			return CommonResult.error(JSYError.BAD_REQUEST);
		}
		//2.文件大小验证
		long fileSizeForKB = carImage.getSize() / 1024;
		//允许上传的文件大小，临时，后续从配置文件中取值 由Spring控制
		int carImageMaxSizeKB = 500;
		if (fileSizeForKB > carImageMaxSizeKB) {
			return CommonResult.error(JSYError.REQUEST_PARAM.getCode(), "文件太大了,最大：" + carImageMaxSizeKB + "KB");
		}
		String fileName = carImage.getOriginalFilename();
		log.info("车辆图片上传文件名：" + fileName + " 车辆图片文件大小：" + fileSizeForKB + "KB");
		//3.文件后缀验证
		if (isMobileClient(request.getHeader("user-agent"))) {
			//如果是PC端访问上传接口 则需要验证文件后缀名
			boolean extension = FilenameUtils.isExtension(fileName, carImageAllowSuffix);
			if (!extension) {
				return CommonResult.error(JSYError.REQUEST_PARAM.getCode(), "文件后缀不允许,可用后缀" + Arrays.asList(carImageAllowSuffix));
			}
		}
		//4.调用上传服务接口 进行上传文件  返回访问路径
		String filePath = MinioUtils.upload(carImage, BUCKET_NAME);
		stringRedisTemplate.opsForSet().add("car_img_part",filePath); // 将图片地址存入redis  用于对比 便于清理无用图片
		return CommonResult.ok(filePath,"上传成功!");
	}


	@Login
	@ApiOperation("所属人车辆图片批量上传接口")
	@ApiImplicitParam(name = "carImageForBatch", value = "所有车辆图片文件")
	@PostMapping(value = "carImageBatchUpload")
	public CommonResult<String[]> carImageUpload(MultipartFile[] carImages, HttpServletRequest request)  {
		if (isMobileClient(request.getHeader("user-agent"))) {
			//如果是PC端访问上传接口 则需要验证文件后缀名
			for( MultipartFile multipartFile : carImages ){
				if(multipartFile == null || multipartFile.isEmpty() || Objects.equals(multipartFile.getOriginalFilename(), "")){
					throw new JSYException(1, "上传的图片不能为空!");
				}
				if (!FilenameUtils.isExtension(multipartFile.getOriginalFilename(), carImageAllowSuffix)) {
					throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "文件后缀不允许,可用后缀" + Arrays.asList(carImageAllowSuffix));
				}
			}
		}
		return CommonResult.ok(MinioUtils.uploadForBatch(carImages, BUCKET_NAME),"上传成功!");
	}


	/**
	 * 判断是否是移动端访问请求
	 * @param userAgent 请求头
	 */
	private  boolean isMobileClient(String userAgent) {
		return !userAgent.contains("Android") || !userAgent.contains("iPhone") || !userAgent.contains("iPad");
	}

}
