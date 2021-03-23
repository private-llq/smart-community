package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICarService;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.DrivingLicense;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.CarQO;
import com.jsy.community.utils.*;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 【已过期、按新需求改动】
 * @author YuLF
 * @date 2020/11/10 10:56
 */
@Api(tags = "车辆控制器")
@RestController
@RequestMapping("/car")
@Slf4j
@ApiJSYController
@Deprecated
public class CarController {
	
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private ICarService carService;


	@Resource
	private StringRedisTemplate stringRedisTemplate;

	@Resource
	private DrivingLicense drivingLicense;
	
	
	/**
	 * 新增业主固定车辆
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
		ValidatorUtils.validateEntity(carEntity, CarEntity.AddCarValidated.class);
		Integer integer = carService.addProprietorCar(carEntity);
		String filePath = carEntity.getCarImageUrl();
		if (!StringUtils.isEmpty(filePath)) {
			// 将图片地址存入redis 用于对比 便于清理无用图片
			stringRedisTemplate.opsForSet().add(BusinessConst.REDIS_CAR_IMAGE_BUCKET_NAME,filePath);
		}
		//3.登记新增车辆操作
		return integer > 0 ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
	}

	/**
	 * 修改业主固定车辆
	 * 业主端新方法：com.jsy.community.controller.UserController#proprietorUpdate
	 * @param qo 前端请求参数对象
	 * @return 返回修改影响行数
	 */
	@Login
	@Deprecated
	@ApiOperation(value = "修改固定车辆方法", produces = "application/json;charset=utf-8")
	@PutMapping()
	public CommonResult<Boolean> updateProprietorCar(@RequestBody CarQO qo) {
		//效验前端新增车辆参数合法性
		ValidatorUtils.validateEntity(qo, CarQO.UpdateCarValidated.class);
		Integer integer = carService.updateProprietorCar(qo, UserUtils.getUserId());
		return integer > 0 ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
	}
	
	@Login
	@ApiOperation("所属人固定车辆查询方法")
	@PostMapping(value = "/page", produces = "application/json;charset=utf-8")
	public CommonResult<?> queryProprietorCar(@RequestBody BaseQO<CarEntity> qo) {
		if (null == qo.getQuery()) {
			return CommonResult.error("没有选择社区!");
		}
		qo.getQuery().setUid(UserUtils.getUserId());
		//1.查询参数非空数字效验
		ValidatorUtils.validatePageParam(qo);
		List<CarEntity> records = carService.queryProprietorCar(qo).getRecords();
		return CommonResult.ok(records);
	}
	
	
	/**
	 * 通过车辆ID 删除 车辆方法
	 * 使用String类型接受，手动效验id，避免参数类型错误直接抛出500无信息提示
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
		Map<String, Object> params = new HashMap<>(2);
		params.put("uid", UserUtils.getUserId());
		params.put("id", id);
		Integer res = carService.deleteProprietorCar(params);
		return res > 0 ? CommonResult.ok() : CommonResult.error(JSYError.DUPLICATE_KEY.getCode(), "车辆不存在!");
	}
	
	/**
	 * 车辆图片上传接口
	 * @param carImage 车辆图片
	 * @return 返回图片上传成功后的访问路径地址
	 */
	@Login
	@ApiOperation("所属人车辆行驶证图片上传接口")
	@ApiImplicitParam(name = "carImage", value = "车辆行驶证文件")
	@PostMapping(value = "carImageUpload")
	public CommonResult<?> carImageUpload(@RequestParam("carImage") MultipartFile carImage)  {
		PicUtil.imageQualified(carImage);
		//4.调用上传服务接口 进行上传文件  返回访问路径
		String filePath = MinioUtils.upload(carImage, BusinessConst.CAR_IMAGE_BUCKET_NAME);
		// 将图片地址存入redis  用于对比 便于清理无用图片
		stringRedisTemplate.opsForSet().add(BusinessConst.REDIS_CAR_IMAGE_BUCKET_NAME, filePath);
		return CommonResult.ok(filePath,"上传成功!");
	}


	@Login
	@ApiOperation("所属人车辆图片批量上传接口")
	@ApiImplicitParam(name = "carImageForBatch", value = "所有车辆图片文件")
	@PostMapping(value = "carImageBatchUpload")
	public CommonResult<String[]> carImageUpload(MultipartFile[] carImages, HttpServletRequest request)  {
		String requestHeader = "user-agent";
		if (isMobileClient(request.getHeader(requestHeader))) {
			//如果是PC端访问上传接口 则需要验证文件后缀名
			for( MultipartFile multipartFile : carImages ){
				PicUtil.imageQualified(multipartFile);
			}
		}
		return CommonResult.ok(MinioUtils.uploadForBatch(carImages, BusinessConst.CAR_IMAGE_BUCKET_NAME),"上传成功!");
	}

	@Login
	@ApiOperation("行驶证识别")
	@PostMapping("drivingLicenseContent")
	public CommonResult<Map<String, Object>> getDrivingLicenseContent(MultipartFile drivingLicenseImage){
		//验证行驶证图片
		PicUtil.imageQualified(drivingLicenseImage);
		//上传行驶证
		String drivingLicenseImagePath = MinioUtils.upload(drivingLicenseImage, BusinessConst.CAR_DRIVING_LICENSE_BUCKET_NAME);
		//TODO : 用户中途取消操作服务器垃圾图片的更便捷的处理方式
		//识别行驶证
		return CommonResult.ok(PicContentUtil.getDrivingLicenseContent(drivingLicenseImagePath));
	}

	/**
	 * 判断是否是移动端访问请求
	 * @param userAgent 请求头
	 */
	private  boolean isMobileClient(String userAgent) {
		return !userAgent.contains("Android") || !userAgent.contains("iPhone") || !userAgent.contains("iPad");
	}

}
