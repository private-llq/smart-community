package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IRelationService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.proprietor.RelationCarsQO;
import com.jsy.community.qo.proprietor.RelationQO;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.RelationVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

/**
 * @Description: 添加家属控制器
 * @author: Hu
 * @since: 2020/12/10 16:35
 * @Param:
 * @return:
 */
@Api(tags = "添加家属信息")
@RestController
@RequestMapping("/relation")
@ApiJSYController
public class RelationController {
    //图片上传验证
    private final String[] img ={"jpg","png","jpeg"};
    //护照验证
    private final String REG = "^1[45][0-9]{7}$|([P|p|S|s]\\d{7}$)|([S|s|G|g|E|e]\\d{8}$)|([Gg|Tt|Ss|Ll|Qq|Dd|Aa|Ff]\\d{8}$)|([H|h|M|m]\\d{8,10})$";
    //身份证验证
    private final String ID_NUMBER = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IRelationService relationService;

    /**
     * @Description: 添加家属和车辆信息
     * @author: Hu
     * @since: 2021/2/23 17:31
     * @Param:
     * @return:
     */
    @ApiOperation("添加家属信息")
    @PostMapping("/add")
    @Login
    public CommonResult addRelation(@RequestBody RelationQO relationQo){
        ValidatorUtils.validateEntity(relationQo, RelationQO.RelationValidated.class);
        relationQo.getCars().forEach( car -> ValidatorUtils.validateEntity(car, RelationCarsQO.proprietorCarValidated.class) );
        String userId = UserUtils.getUserId();
        relationQo.setUserId(userId);
        UserHouseEntity entity=relationService.getHouse(relationQo);
        if (entity==null){
            throw new JSYException("请填写正确的社区或者房间！");
        }
        if (entity.getCheckStatus()!=1){
            throw new JSYException("当前房屋未认证，请先认证！");
        }
        if (relationQo.getIdentificationType()==1){
            if (!relationQo.getIdCard().matches(ID_NUMBER)) {
                return CommonResult.error("身份证错误，请检查重新填写！");
            }
        }else {
            if (!relationQo.getIdCard().matches(REG)) {
                return CommonResult.error("护照错误，请检查重新填写！");
            }
        }
        relationService.addRelation(relationQo);
        return CommonResult.ok();
    }
    @ApiOperation("删除家属信息及其车辆")
    @DeleteMapping("/delete")
    @Login
    public CommonResult delete(@RequestParam("id") Long id){
        String userId = UserUtils.getUserId();
        relationService.deleteHouseMemberCars(id,userId);
        return CommonResult.ok();
    }
    @ApiOperation("保存行驶证图片")
    @PostMapping("/uploadDrivingLicenseUrl")
    @Login
    public CommonResult uploadDrivingLicenseUrl(@RequestParam("file") MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        String s = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (!Arrays.asList(img).contains(s)) {
            return CommonResult.error("请上传图片！可用后缀"+Arrays.toString(img));
        }
        String upload = MinioUtils.upload(file, "wocao");
        return CommonResult.ok(upload,"上传成功");
    }
    @ApiOperation("删除车辆")
    @DeleteMapping("/delCar")
    @Login
    public CommonResult delCar(@RequestParam("id") Long id){
        String userId = UserUtils.getUserId();
        relationService.delCar(userId, id);
        return CommonResult.ok();
    }
    @ApiOperation("查询一个家属详情")
    @GetMapping("/selectUserRelationDetails")
    @Login
    public CommonResult selectRelationOne(@RequestParam("id") Long id){
        String userId = UserUtils.getUserId();
        RelationVO relationVO = relationService.selectOne(id, userId);
        return CommonResult.ok(relationVO);
    }
    @ApiOperation("修改家属信息加汽车信息")
    @PutMapping("/updateUserRelationDetails")
    @Login
    public CommonResult updateByRelationId(@RequestBody RelationQO relationQo){
        ValidatorUtils.validateEntity(relationQo, RelationQO.RelationValidated.class);
        relationQo.getCars().forEach( car -> ValidatorUtils.validateEntity(car,RelationCarsQO.proprietorCarValidated.class) );
        String userId = UserUtils.getUserId();
        relationQo.setUserId(userId);
        UserHouseEntity entity=relationService.getHouse(relationQo);
        if (entity==null){
            return CommonResult.error("请填写正确的社区或者房间！");
        }
        if (entity.getCheckStatus()!=1){
            return CommonResult.error("当前房屋未认证，请先认证！");
        }
        if (relationQo.getIdentificationType()==1){
            if (!relationQo.getIdCard().matches(ID_NUMBER)) {
                return CommonResult.error("身份证错误，请检查重新填写！");
            }
        }else {
            if (!relationQo.getIdCard().matches(REG)) {
                return CommonResult.error("护照错误，请检查重新填写！");
            }
        }
        relationService.updateUserRelationDetails(relationQo);
        return CommonResult.ok();
    }
}
