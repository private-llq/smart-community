package com.jsy.community.controller;

import com.jsy.community.annotation.PropertyFinanceLog;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.IFinanceBillService;
import com.jsy.community.api.IPropertyFeeRuleConstService;
import com.jsy.community.api.IPropertyFeeRuleService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyFeeRuleEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.FeeRuleQO;
import com.jsy.community.qo.property.FeeRuleRelevanceQO;
import com.jsy.community.qo.property.UpdateRelevanceQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:  小区物业收费规则
 * @author: Hu
 * @create: 2021-04-20 16:36
 **/
@Api(tags = "小区物业收费规则")
@RestController
@RequestMapping("/feeRule")
// @ApiJSYController
public class PropertyFeeRuleController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFeeRuleService propertyFeeRuleService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFeeRuleConstService propertyFeeRuleConstService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IFinanceBillService financeBillService;

    @ApiOperation("查询当前小区物业收费规则")
    @PostMapping("/list")
    @Permit("community:property:feeRule:list")
    public CommonResult feeRule(@RequestBody BaseQO<FeeRuleQO> baseQO){
        AdminInfoVo userInfo = UserUtils.getAdminInfo();
        Map<Object, Object> map=propertyFeeRuleService.findList(baseQO,userInfo.getCommunityId());

        return CommonResult.ok(map);
    }

    @ApiOperation("查询一条详情")
    @GetMapping("/selectOne")
    @Permit("community:property:feeRule:selectOne")
    public CommonResult selectOne(@RequestParam("id")Long id){
        PropertyFeeRuleEntity propertyFeeRuleEntity=propertyFeeRuleService.selectByOne(id);
        return CommonResult.ok(propertyFeeRuleEntity);
    }

    @ApiOperation("删除关联的房屋或者车辆")
    @DeleteMapping("/deleteRelevance")
    @Permit("community:property:feeRule:deleteRelevance")
    public CommonResult deleteRelevance(@RequestParam("id")Long id){
        propertyFeeRuleService.deleteRelevance(id);
        return CommonResult.ok();
    }

    @ApiOperation("添加关联的房屋或者车辆")
    @PostMapping("/addRelevance")
    @Permit("community:property:feeRule:addRelevance")
    public CommonResult addRelevance(@RequestBody UpdateRelevanceQO updateRelevanceQO){
        propertyFeeRuleService.addRelevance(updateRelevanceQO);
        return CommonResult.ok();
    }
    @ApiOperation("查询当前小区所有的房屋")
    @GetMapping("/getHouse")
    @Permit("community:property:feeRule:getHouse")
    public CommonResult getHouse(){
        return CommonResult.ok(propertyFeeRuleService.getHouse(UserUtils.getAdminCommunityId()));
    }

    @ApiOperation("查询当前小区所有的车位")
    @GetMapping("/getCarPosition")
    @Permit("community:property:feeRule:getCarPosition")
    public CommonResult getCarPosition(){
        return CommonResult.ok(propertyFeeRuleService.getCarPosition(UserUtils.getAdminCommunityId()));
    }


    @ApiOperation("查询关联目标")
    @PostMapping("/selectRelevance")
    @Permit("community:property:feeRule:selectRelevance")
    public CommonResult selectRelevance(@RequestBody FeeRuleRelevanceQO feeRuleRelevanceQO){
        List<Object> list = propertyFeeRuleService.selectRelevance(feeRuleRelevanceQO);
        return CommonResult.ok(list);
    }

    @ApiOperation("启用或者停用")
    @GetMapping("/startOrOut")
    @PropertyFinanceLog(operation = "收费项目：",type = 1)
    @Permit("community:property:feeRule:startOrOut")
    public CommonResult startOrOut(@RequestParam("status")Integer status,@RequestParam("id") Long id){
        AdminInfoVo userInfo = UserUtils.getAdminInfo();
        propertyFeeRuleService.startOrOut(userInfo,status,id);
        return CommonResult.ok();
    }
    @ApiOperation("启用或者停用报表展示")
    @GetMapping("/statementStatus")
    @Permit("community:property:feeRule:statementStatus")
    public CommonResult statementStatus(@RequestParam("status")Integer status,@RequestParam("id") Long id){
        AdminInfoVo userInfo = UserUtils.getAdminInfo();
        propertyFeeRuleService.statementStatus(userInfo,status,id);
        return CommonResult.ok();
    }

    @ApiOperation("修改")
    @PutMapping("/updateById")
    @businessLog(operation = "编辑",content = "更新了【物业收费规则】")
    @Permit("community:property:feeRule:updateById")
    public CommonResult updateById(@RequestBody PropertyFeeRuleEntity propertyFeeRuleEntity){
        ValidatorUtils.validateEntity(propertyFeeRuleEntity);
        AdminInfoVo userInfo = UserUtils.getAdminInfo();
        propertyFeeRuleEntity.setCommunityId(UserUtils.getAdminCommunityId());
        propertyFeeRuleService.updateOneRule(userInfo,propertyFeeRuleEntity);
        return CommonResult.ok();
    }

    @ApiOperation("新增")
    @PostMapping("/save")
    @businessLog(operation = "新增",content = "新增了【物业收费规则】")
    @Permit("community:property:feeRule:save")
    public CommonResult save(@RequestBody PropertyFeeRuleEntity propertyFeeRuleEntity){
        ValidatorUtils.validateEntity(propertyFeeRuleEntity, PropertyFeeRuleEntity.PropertyFeeRule.class);
        AdminInfoVo userInfo = UserUtils.getAdminInfo();
        propertyFeeRuleEntity.setCommunityId(UserUtils.getAdminCommunityId());
        propertyFeeRuleEntity.setName(BusinessEnum.FeeRuleNameEnum.getName(propertyFeeRuleEntity.getType()));
        propertyFeeRuleService.saveOne(userInfo,propertyFeeRuleEntity);
        return CommonResult.ok();
    }

    @ApiOperation("删除")
    @DeleteMapping("/delete")
    @businessLog(operation = "删除",content = "删除了【物业收费规则】")
    @Permit("community:property:feeRule:delete")
    public CommonResult delete(@RequestParam Long id){
        AdminInfoVo userInfo = UserUtils.getAdminInfo();
        propertyFeeRuleService.delete(id);
        return CommonResult.ok();
    }

    @ApiOperation("查询公共常量")
    @PostMapping("/getConst")
    @Permit("community:property:feeRule:getConst")
    public CommonResult getConst(){
        return CommonResult.ok(propertyFeeRuleConstService.listAll());
    }

    @LoginIgnore
    @ApiOperation("查询公共常量")
    @GetMapping("/get")
    public CommonResult get(){
        financeBillService.updateAnnual();
        financeBillService.updateTemporary();
        financeBillService.updateMonth();
        financeBillService.pushMonth();
        financeBillService.pushAnnual();
        return CommonResult.ok();
    }



}
