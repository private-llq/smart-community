package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICarService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.exception.JSYError;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author YuLF
 * @date 2020/11/10 10:56
 */
@Api(tags = "车辆控制器")
@RestController
@RequestMapping("/car")
@Slf4j
@Login(allowAnonymous = true)
@ApiJSYController
public class CarController {


    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICarService carService;

    //允许上传的文件后缀种类，临时，后续从Spring配置文件中取值
    private final String[] carImageAllowSuffix = new String[]{"jpg", "jpeg", "png"};

    @Resource
    private MinioController minioController;

    //允许上传的文件大小，临时，后续从配置文件中取值 由Spring控制
    private final int carImageMaxSizeKB = 500;
    
    private static final String BUCKETNAME = "car-img"; //暂时写死  后面改到配置文件中  BUCKETNAME命名规范：只能小写，数字，-
    
    
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
        ValidatorUtils.validateEntity(carEntity, CarEntity.addCarValidated.class);
        Integer integer = carService.addProprietorCar(carEntity);
        //3.登记新增车辆操作
        return integer > 0 ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }


    /**
     * 修改业主固定车辆
     * @param carQO 前端请求参数对象
     * @return 返回修改影响行数
     */
    @ApiOperation(value = "修改固定车辆方法", produces = "application/json;charset=utf-8")
    @PutMapping()
    public CommonResult<Boolean> updateProprietorCar(@RequestBody CarQO carQO) {
        //效验前端新增车辆参数合法性
        ValidatorUtils.validateEntity(carQO, CarQO.updateCarValidated.class);
        Integer integer = carService.updateProprietorCar(carQO, UserUtils.getUserId());
        return integer > 0 ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }


    @ApiOperation("所属人固定车辆查询方法")
    @PostMapping(value = "/page", produces = "application/json;charset=utf-8")
    public CommonResult<?> queryProprietorCar(@RequestBody BaseQO<CarEntity> carEntityBaseQO) {
        if( null == carEntityBaseQO.getQuery()){
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
     * @param id 车辆id
     * @return 返回逻辑删除影响行
     */
    @ApiOperation("所属人固定车辆删除方法")
    @ApiImplicitParam(name = "id", value = "车辆固定id")
    @DeleteMapping()
    public CommonResult<Boolean> deleteProprietorCar(@RequestParam String id) {
        //从请求获得uid
        Long uid = 12L;
        if( !ValidatorUtils.isInteger(id)  ){
            return CommonResult.error(JSYError.REQUEST_PARAM);
        }
        //条件参数列表 key=列名  增加删除车辆记录 where条件只改动这里
        Map<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        params.put("id",id);
        Integer res = carService.deleteProprietorCar(params);
        return res > 0 ? CommonResult.ok() : CommonResult.error(JSYError.DUPLICATE_KEY.getCode(), "车辆不存在!");
    }

    /**
     * 车辆图片上传接口
     *
     * @param carImage 车辆图片
     * @return 返回图片上传成功后的访问路径地址
     */
    @ApiOperation("所属人车辆图片上传接口")
    @ApiImplicitParam(name = "carImage", value = "车辆图片文件")
    @PostMapping(value = "carImageUpload")
    public CommonResult<?> carImageUpload(MultipartFile carImage, HttpServletRequest request) throws IOException {
        //1.接口非空验证
        if (null == carImage) {
            return CommonResult.error(JSYError.BAD_REQUEST);
        }
        //2.文件大小验证
        long fileSizeForKB = carImage.getSize() / 1024;
        if (fileSizeForKB > carImageMaxSizeKB) {
            return CommonResult.error(JSYError.REQUEST_PARAM.getCode(), "文件太大了,最大：" + carImageMaxSizeKB + "KB");
        }
        String fileName = carImage.getOriginalFilename();
        log.info("用户请求头：{}", request.getHeader("user-agent"));
        log.info("车辆图片上传文件名：" + fileName + " 车辆图片文件大小：" + fileSizeForKB + "KB");
        //3.文件后缀验证
        if(!isMobileClient(request.getHeader("user-agent"))){
            //如果是PC端访问上传接口 则需要验证文件后缀名
            boolean extension = FilenameUtils.isExtension(fileName, carImageAllowSuffix);
            if (!extension) {
                return CommonResult.error(JSYError.REQUEST_PARAM.getCode(), "文件后缀不允许,可用后缀" + Arrays.asList(carImageAllowSuffix));
            }
        }
        //4.调用上传服务接口 进行上传文件  返回访问路径
	    try {
		    String filePath = MinioUtils.upload(carImage, BUCKETNAME);
		    return CommonResult.ok(filePath);
	    } catch (Exception e) {
		    e.printStackTrace();
		    return CommonResult.error("上传失败");
	    }
    }

    /**
     * 判断是否是移动端访问请求
     * @param userAgent  请求头
     */
    public boolean isMobileClient(String userAgent){
        if (userAgent.contains("Android")) {
            return true;
        } else return userAgent.contains("iPhone") || userAgent.contains("iPad");
    }

}
