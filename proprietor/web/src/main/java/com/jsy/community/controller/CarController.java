package com.jsy.community.controller;

import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICarService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Api(tags = "车辆控制器")
@RestController
@Slf4j
@Login(allowAnonymous = true)
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
    @PostMapping(value = "/car", produces = "application/json;charset=utf-8")
    public CommonResult<?> addProprietorCar(@ApiParam(value = "车辆登记参数对象", required = true) @RequestBody CarEntity carEntity) {
        //1.效验前端新增车辆参数合法性
        ValidatorUtils.validateEntity(carEntity, CarEntity.addCarValidated.class);
        //2.效验被登记的车辆是否存在登记过
        if (carPlateExist(carEntity.getCarPlate())) {
            return CommonResult.error(JSYError.NOT_IMPLEMENTED.getCode(), "车辆车牌已经登记存在!");
        }
        //3.登记新增车辆操作
        return CommonResult.ok(carService.addProprietorCar(carEntity));
    }


    /**
     * 修改业主固定车辆
     * @param carEntity 前端请求参数对象
     * @return 返回修改影响行数
     */
    @ApiOperation(value = "修改固定车辆方法", produces = "application/json;charset=utf-8")
    @PutMapping("/car")
    public CommonResult<Integer> updateProprietorCar(@ApiParam(value = "修改固定车辆提供的参数对象", required = true) @RequestBody CarEntity carEntity) {
        //效验前端新增车辆参数合法性
        ValidatorUtils.validateEntity(carEntity, CarEntity.updateCarValidated.class);
        return CommonResult.ok(carService.updateProprietorCar(carEntity));
    }


    /**
     * 业主车辆分页查询
     * 所有参数类型为String 不由Spring处理 自定义处理效验
     * @param page        当前页
     * @param pageSize    页显示条数
     * @param uid         业主id
     * @param checkStatus 是否已经审核
     * @return 返回当前页业主车辆数据
     */
    @ApiOperation("所属人固定车辆查询方法")
    @GetMapping(value = "/car/{uid}/{checkStatus}/{page}/{pageSize}")
    public CommonResult<?> queryProprietorCar(@PathVariable("uid") String uid, @PathVariable("checkStatus") String checkStatus, @PathVariable("page") String page, @PathVariable("pageSize") String pageSize) {
        //参数数字效验
        if (isNumber(uid) && isNumber(page) && isNumber(pageSize) && isNumber(checkStatus)) {
            //Map传递请求参数
            Map<String, Object> paramMap = new HashMap<>(4);
            paramMap.put("page", page);
            paramMap.put("pageSize", pageSize);
            paramMap.put("uid", uid);
            paramMap.put("checkStatus", checkStatus);
            List<CarEntity> records = carService.queryProprietorCar(paramMap).getRecords();
            return CommonResult.ok(records);
        }
        //非法参数
        return CommonResult.error(JSYError.BAD_REQUEST.getCode(), JSYError.BAD_REQUEST.getMessage());
    }


    /**
     * 判断字符串是否是一个完整的数字
     * @param str 字符串
     * @return 返回这个字符串是否是字符串的布尔值
     */
    private Boolean isNumber(String str) {
        return Pattern.compile("^-?\\d+(\\.\\d+)?$").matcher(str).matches();
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
     * @param id 车辆id
     * @return 返回逻辑删除影响行
     */
    @ApiOperation("所属人固定车辆删除方法")
    @DeleteMapping(value = "/car/{id}")
    public CommonResult<?> deleteProprietorCar(@PathVariable("id") long id) {
        Integer res = carService.deleteProprietorCar(id);
        return res > 0 ? CommonResult.ok(res) : CommonResult.error(JSYError.DUPLICATE_KEY.getCode(), "车辆不存在!");
    }

    /**
     * 车辆图片上传接口
     * @param carImage  车辆图片
     * @return          返回图片上传成功后的访问路径地址
     */
    @ApiOperation("所属人车辆图片上传接口")
    @PostMapping("/car/carImageUpload")
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
        //4.临时本地上传方式
        FileOutputStream fileOutputStream = null;
        try {
            //图片文件流
            byte[] bytes = carImage.getBytes();
            fileOutputStream = new FileOutputStream(new File("D:" + File.separator + "TestFileDirectory" + File.separator + fileName));
            fileOutputStream.write(bytes);
            //上传成功返回访问路径
            return CommonResult.ok("https://www.baidu.com/" + fileName);
        } catch (IOException e) {
            return CommonResult.error(JSYError.NOT_IMPLEMENTED.getCode(), "文件上传失败!" + e.getMessage());
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }

}
