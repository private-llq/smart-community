package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IRelationService;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.RelationQo;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.RegexUtils;
import com.jsy.community.utils.UserUtils;
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
    private final String[] img ={"jpg","png","jpeg"};

    //手机号验证
    private final String REGEX_MOBILE = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$";
    //护照验证
    private final String REG = "^1[45][0-9]{7}$|([P|p|S|s]\\d{7}$)|([S|s|G|g|E|e]\\d{8}$)|([Gg|Tt|Ss|Ll|Qq|Dd|Aa|Ff]\\d{8}$)|([H|h|M|m]\\d{8,10})$";
    //身份证验证
    private final String ID_NUMBER = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IRelationService relationService;






    @ApiOperation("添加家属信息")
    @PutMapping("/add")
    @Login
    public CommonResult addRelation(@RequestBody RelationQo relationQo){

        String userId = UserUtils.getUserId();
        relationQo.setUserId(userId);
        UserHouseEntity entity=relationService.getHouse(relationQo);
        if (entity==null){
            return CommonResult.error("请填写正确的社区或者房间！");
        }
        if (entity.getCheckStatus()!=1){
            return CommonResult.error("当前房屋未认证，请先认证！");
        }
        if ("".equals(relationQo.getName())&&relationQo.getName()==null){
            return CommonResult.error("姓名不能为空！");
        }else if (!relationQo.getName().matches(RegexUtils.REGEX_REAL_NAME)){
            return CommonResult.error("姓名不合法！请填写正确的姓名！");
        }
        if (relationQo.getPhoneTel()!=null&&!"".equals(relationQo.getPhoneTel())){
            if (!relationQo.getPhoneTel().matches(REGEX_MOBILE)){
                return CommonResult.error("请填写正确的手机号！");
            }
        }
        if (relationQo.getIdentificationType()==1) {
            if (!relationQo.getIdNumber().matches(ID_NUMBER)) {
                return CommonResult.error("请填写正确的身份证号码！");
            }
        }else if (!relationQo.getIdNumber().matches(REG)){
                return CommonResult.error("请填写正确的护照号码！仅支持中国大陆，不包含港澳台！");
            }

        relationQo.setPersonType(BusinessConst.PERSON_TYPE_RELATIVE);
        return relationService.addRelation(relationQo)?CommonResult.ok():CommonResult.error(JSYError.INTERNAL);
    }


    @ApiOperation("删除家属信息及其车辆")
    @PostMapping("/delete")
    @Login
    public CommonResult delete(@RequestParam("id") Long id){
        relationService.deleteHouseMemberCars(id);
        return CommonResult.ok();
    }


    @ApiOperation("保存车辆图片")
    @PostMapping("/upload")
    @Login
    public CommonResult upload(@RequestParam("file") MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        String s = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (!Arrays.asList(img).contains(s)) {
            return CommonResult.error("请上传图片！可用后缀"+ Arrays.toString(img));
        }
        String upload = MinioUtils.upload(file, "aaaa");
        return CommonResult.ok(upload);
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
        return CommonResult.ok(upload);
    }

    @ApiOperation("查询一个家属详情")
    @GetMapping("/selectUserRelationDetails")
    @Login
    public CommonResult selectRelationOne(@RequestParam("RelationId") Long RelationId){
        String userId = UserUtils.getUserId();
        RelationVO relationVO = relationService.selectOne(RelationId, userId);
        return CommonResult.ok(relationVO);
    }

    //    @ApiOperation("修改一个家属信息")
//    @PostMapping("/updateByRelationId")
//    @Login
//    public CommonResult updateByRelationId(@RequestBody HouseMemberEntity houseMemberEntity){
//        String userId = UserUtils.getUserId();
//        relationService.updateByRelationId(houseMemberEntity);
//        return CommonResult.ok();
//    }
    @ApiOperation("修改家属信息加汽车信息")
    @PostMapping("/updateUserRelationDetails")
    @Login
    public CommonResult updateByRelationId(@RequestBody RelationQo relationQo){
        String userId = UserUtils.getUserId();
        relationQo.setUserId(userId);
        UserHouseEntity entity=relationService.getHouse(relationQo);
        if (entity==null){
            return CommonResult.error("请填写正确的社区或者房间！");
        }
        if (entity.getCheckStatus()!=1){
            return CommonResult.error("当前房屋未认证，请先认证！");
        }
        if ("".equals(relationQo.getName())&&relationQo.getName()==null){
            return CommonResult.error("姓名不能为空！");
        }else if (!relationQo.getName().matches(RegexUtils.REGEX_REAL_NAME)){
            return CommonResult.error("姓名不合法！请填写正确的姓名！");
        }
        if (relationQo.getPhoneTel()!=null&&!"".equals(relationQo.getPhoneTel())){
            if (!relationQo.getPhoneTel().matches(REGEX_MOBILE)){
                return CommonResult.error("请填写正确的手机号！");
            }
        }
        if (relationQo.getIdentificationType()==1) {
            if (!relationQo.getIdNumber().matches(ID_NUMBER)) {
                return CommonResult.error("请填写正确的身份证号码！");
            }
        }else if (!relationQo.getIdNumber().matches(REG)){
            return CommonResult.error("请填写正确的护照号码！仅支持中国大陆，不包含港澳台！");
        }
        relationService.updateUserRelationDetails(relationQo);
        return CommonResult.ok();
    }

//    @ApiOperation("修改一个家属信息传入一个家属id做表单回填")
//    @GetMapping("/updateFormBackFillId")
//    @Login
//    public CommonResult updateFormBackFillId(@RequestParam("RelationId") Long RelationId){
//        String userId = UserUtils.getUserId();
//        HouseMemberEntity houseMemberEntity = relationService.updateFormBackFillId(RelationId);
//        return CommonResult.ok();
//    }



}
