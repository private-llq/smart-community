package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IPropertyDepositService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyDepositEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyDepositQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @program: com.jsy.community
 * @description: 物业押金账单
 * @author: DKS
 * @create: 2021-08-10 17:35
 **/
@Api(tags = "物业押金账单")
@RestController
@RequestMapping("/deposit")
@ApiJSYController
@Login
public class PropertyDepositController {
    
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyDepositService propertyDepositService;
    
    /**
     * @program: com.jsy.community
     * @description: 新增物业押金账单
     * @author: DKS
     * @create: 2021-08-10 17:35
     **/
    @Login
    @ApiOperation("新增物业押金账单")
    @PostMapping("/add")
    public CommonResult addPropertyDeposit(@RequestBody PropertyDepositEntity propertyDepositEntity){
        if(propertyDepositEntity.getDepositType() == null || propertyDepositEntity.getDepositTargetId() == null || propertyDepositEntity.getPayService() == null || propertyDepositEntity.getBillMoney() == null){
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少类型参数");
        }
        ValidatorUtils.validateEntity(propertyDepositEntity);
        AdminInfoVo loginUser = UserUtils.getAdminUserInfo();
        propertyDepositEntity.setCommunityId(loginUser.getCommunityId());
        propertyDepositEntity.setCreateBy(loginUser.getUid());
        boolean result = propertyDepositService.addPropertyDeposit(propertyDepositEntity);
        return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"新增物业押金账单失败");
    }
    
    /**
     * @program: com.jsy.community
     * @description: 修改物业押金账单
     * @author: DKS
     * @create: 2021-08-11 9:15
     **/
    @Login
    @ApiOperation("修改物业押金账单")
    @PutMapping("/update")
    public CommonResult updatePropertyDeposit(@RequestBody PropertyDepositEntity propertyDepositEntity){
        ValidatorUtils.validateEntity(propertyDepositEntity);
        AdminInfoVo loginUser = UserUtils.getAdminUserInfo();
        propertyDepositEntity.setCommunityId(loginUser.getCommunityId());
        propertyDepositEntity.setUpdateBy(UserUtils.getUserId());
        return propertyDepositService.updatePropertyDeposit(propertyDepositEntity)
            ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"修改物业押金账单失败");
    }
    
    /**
     * @program: com.jsy.community
     * @description: 删除物业押金账单
     * @author: DKS
     * @create: 2021-08-11 9:20
     **/
    @Login
    @ApiOperation("删除物业押金账单")
    @DeleteMapping("/delete")
    public CommonResult deletePropertyDeposit(@RequestParam Long id){
        return propertyDepositService.deletePropertyDeposit(id,UserUtils.getAdminCommunityId()) ? CommonResult.ok("删除成功") : CommonResult.error("删除失败");
    }
    
    /**
     * @Description: 分页查询物业押金账单
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.PropertyDepositEntity>>
     * @Author: DKS
     * @Date: 2021/08/11
     **/
    @Login
    @ApiOperation("分页查询物业押金账单")
    @PostMapping("/query")
    public CommonResult<PageInfo<PropertyDepositEntity>> queryPropertyDeposit(@RequestBody BaseQO<PropertyDepositQO> baseQO) {
        PropertyDepositQO query = baseQO.getQuery();
        if(query == null){
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少查询类型");
        }
        query.setCommunityId(UserUtils.getAdminCommunityId());
        return CommonResult.ok(propertyDepositService.queryPropertyDeposit(baseQO));
    }
}
