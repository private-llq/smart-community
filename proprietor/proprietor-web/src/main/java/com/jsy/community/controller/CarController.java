package com.jsy.community.controller;

import com.jsy.community.annotation.auth.Login;
import com.jsy.community.annotation.web.ApiProprietor;
import com.jsy.community.api.ICarService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "车辆控制器")
@RestController
@RequestMapping("/car")
@Slf4j
@Login(allowAnonymous = true)
@ApiProprietor
public class CarController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICarService carService;

    //允许上传的文件后缀种类，临时，后续从Spring配置文件中取值
    private final String[] carImageAllowSuffix = new String[]{"jpg", "jpeg", "png"};

    //允许上传的文件大小，临时，后续从配置文件中取值 由Spring控制
    private final int carImageMaxSizeKB = 500;

    /**
     * 新增业主固定车辆
     * @param carEntity 前端参数对象
     * @return 返回新增结果
     */
    @ApiOperation("新增固定车辆登记方法")
    @PostMapping(produces = "application/json;charset=utf-8")
    @Transactional(rollbackFor = {Exception.class}, isolation = Isolation.READ_COMMITTED)
    public CommonResult<Boolean> addProprietorCar(@RequestBody CarEntity carEntity) {
        //0.从request取uid
        carEntity.setUid(12L);
        //1.效验前端新增车辆参数合法性
        ValidatorUtils.validateEntity(carEntity, CarEntity.addCarValidated.class);
        //2.效验被登记的车辆是否存在登记过
        if (carPlateExist(carEntity.getCarPlate())) {
            return CommonResult.error(JSYError.NOT_IMPLEMENTED.getCode(), "车辆车牌已经登记存在!");
        }
        Integer integer = carService.addProprietorCar(carEntity);
        //3.登记新增车辆操作
        return integer > 0 ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }


    /**
     * 修改业主固定车辆
     * @param carEntity 前端请求参数对象
     * @return 返回修改影响行数
     */
    @ApiOperation(value = "修改固定车辆方法", produces = "application/json;charset=utf-8")
    @PutMapping()
    public CommonResult<Boolean> updateProprietorCar(@RequestBody CarEntity carEntity) {
        //从请求获取uid
        Long uid = 12L;
        carEntity.setUid(uid);
        //效验前端新增车辆参数合法性
        ValidatorUtils.validateEntity(carEntity, CarEntity.updateCarValidated.class);
        Integer integer = carService.updateProprietorCar(carEntity);
        return integer > 0 ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }


    @ApiOperation("所属人固定车辆查询方法")
    @PostMapping(value = "/page", produces = "application/json;charset=utf-8")
    public CommonResult<?> queryProprietorCar(@RequestBody BaseQO<CarEntity> carEntityBaseQO) {
        //0.从request取uid
        long uid = 12L;
        if( null == carEntityBaseQO.getQuery()){
            return CommonResult.error("没有选择社区!");
        }
        carEntityBaseQO.getQuery().setUid(uid);
        //1.查询参数非空数字效验
        ValidatorUtils.validatePageParam(carEntityBaseQO);
        List<CarEntity> records = carService.queryProprietorCar(carEntityBaseQO).getRecords();
        return CommonResult.ok(records);
    }


    /**
     * 根据车牌号检查车辆是否已经登记
     * @param carPlate 车牌号
     * @return 返回是否存在布尔值
     */
    private Boolean carPlateExist(String carPlate) {
        return carService.carIsExist(carPlate);
    }

    /**
     * 通过车辆ID 删除 车辆方法
     * 使用String类型接受，手动效验id，避免参数类型错误直接抛出500无信息提示
     * @param id 车辆id
     * @return 返回逻辑删除影响行
     */
    @ApiOperation("所属人固定车辆删除方法")
    @ApiImplicitParam(name = "id", value = "车辆固定id")
    @DeleteMapping(value = "{id}")
    public CommonResult<Boolean> deleteProprietorCar(@PathVariable("id") String id) {
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
    public CommonResult<?> carImageUpload(MultipartFile carImage) throws IOException {
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
        log.info("车辆图片上传文件名：" + fileName + " 车辆图片文件大小：" + fileSizeForKB + "KB");
        //3.文件后缀验证
        boolean extension = FilenameUtils.isExtension(fileName, carImageAllowSuffix);
        if (!extension) {
            return CommonResult.error(JSYError.REQUEST_PARAM.getCode(), "文件后缀不允许,可用后缀" + Arrays.asList(carImageAllowSuffix));
        }
        //4.调用上传车辆图片服务接口 进行上传文件
        String resultUrl = carService.carImageUpload(carImage.getBytes(), fileName);
        if (StringUtils.isBlank(resultUrl)) {
            return CommonResult.error(JSYError.NOT_IMPLEMENTED.getCode(), JSYError.NOT_IMPLEMENTED.getMessage());
        }
        return CommonResult.ok(resultUrl);
    }


}
