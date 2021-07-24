package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.Desensitization;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IPropertyRelationService;
import com.jsy.community.aspectj.DesensitizationType;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.HouseMemberQO;
import com.jsy.community.qo.property.PropertyRelationQO;
import com.jsy.community.qo.property.RelationListQO;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.PicUtil;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 物业成员查询接口
 * @author: Hu
 * @create: 2021-03-05 11:18
 **/
@Api(tags = "物业家属查询")
@RestController
@RequestMapping("/members")
@ApiJSYController
public class PropertyRelationController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyRelationService propertyRelationService;

    @ApiOperation("分页查询")
    @PostMapping("/pageList")
    @Login
    public CommonResult pageList(@RequestBody BaseQO<HouseMemberQO> baseQO){
    return CommonResult.ok(propertyRelationService.pageList(baseQO));
    }

    @ApiOperation("迁入")
    @GetMapping("/immigration")
    @Login
    public CommonResult immigration(@RequestParam Long id){
        propertyRelationService.immigration(id);
        return CommonResult.ok();
    }

    @ApiOperation("迁出")
    @GetMapping("/emigration")
    @Login
    public CommonResult emigration(@RequestParam Long id){
        propertyRelationService.emigration(id);
        return CommonResult.ok();
    }

    @ApiOperation("批量迁出")
    @PostMapping("/emigrations")
    @Login
    public CommonResult emigrations(@RequestParam Long[] ids){
        propertyRelationService.emigrations(ids);
        return CommonResult.ok();
    }

    @ApiOperation("新增")
    @PostMapping("/save")
    @Login
    public CommonResult save(@RequestBody HouseMemberEntity houseMemberEntity){
        propertyRelationService.save(houseMemberEntity);
        return CommonResult.ok();
    }
    @ApiOperation("修改")
    @PutMapping("/update")
    @Login
    public CommonResult update(@RequestBody HouseMemberEntity houseMemberEntity){
        propertyRelationService.update(houseMemberEntity);
        return CommonResult.ok();
    }

    @ApiOperation("查询一条")
    @GetMapping("/findOne")
    @Login
    public CommonResult findOne(@RequestParam Long id){
        HouseMemberEntity entity = propertyRelationService.findOne(id);
        return CommonResult.ok(entity);
    }

    @ApiOperation("新增入住图")
    @PostMapping("/enterPicture")
    @Login
    public CommonResult enterPicture(@RequestParam("file") MultipartFile file) {
        if(PicUtil.checkSizeAndType(file,2048)){
            String upload = MinioUtils.upload(file, "enterimge");
            return  CommonResult.ok(upload,"上传成功");
        }
        return  CommonResult.ok("上传失败！");
    }
    @ApiOperation("新增身份证图")
    @PostMapping("/idCardImage")
    @Login
    public CommonResult idCardImage(@RequestParam("file") MultipartFile file) {
        if(PicUtil.checkSizeAndType(file,2048)){
            String upload = MinioUtils.upload(file, "idcardimage");
            return  CommonResult.ok(upload,"上传成功");
        }
        return  CommonResult.ok("上传失败！");
    }
    @ApiOperation("查询成员列表")
    @PostMapping("/list")
    @Login
    @Desensitization(type = {DesensitizationType.PHONE,DesensitizationType.ID_CARD,DesensitizationType.PHONE,DesensitizationType.ID_CARD}, field = {"mobile","idCard","ownerMobile","ownerIdCard"})
    public CommonResult list(@RequestBody BaseQO<PropertyRelationQO> baseQO){
        System.out.println(baseQO);
        Map map=propertyRelationService.list(baseQO,UserUtils.getAdminUserInfo().getCommunityId());
        return CommonResult.ok(map);
    }
    @ApiOperation("房屋下拉框")
    @PostMapping("/getHouseId")
    @Login
    public CommonResult getHouseId(@RequestBody BaseQO<RelationListQO> baseQO){
        AdminInfoVo adminInfoVo = UserUtils.getAdminUserInfo();
        List list =propertyRelationService.getHouseId(baseQO,adminInfoVo);
        return CommonResult.ok(list);
    }
    @ApiOperation("楼栋下拉框")
    @PostMapping("/getBuildingId")
    @Login
    public CommonResult getBuildingId(@RequestBody BaseQO<RelationListQO> baseQO){
        AdminInfoVo adminInfoVo = UserUtils.getAdminUserInfo();
        List list =propertyRelationService.getBuildingId(baseQO,adminInfoVo);
        return CommonResult.ok(list);
    }
    @ApiOperation("单元下拉框")
    @PostMapping("/getUnitId")
    @Login
    public CommonResult getUnitId(@RequestBody BaseQO<RelationListQO> baseQO){
        AdminInfoVo adminInfoVo = UserUtils.getAdminUserInfo();
        List list =propertyRelationService.getUnitId(baseQO,adminInfoVo);
        return CommonResult.ok(list);
    }
}
