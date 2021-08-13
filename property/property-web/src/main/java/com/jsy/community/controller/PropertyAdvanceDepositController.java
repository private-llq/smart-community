package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IPropertyAdvanceDepositService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyAdvanceDepositEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyAdvanceDepositQO;
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
 * @description: 物业预存款余额
 * @author: DKS
 * @create: 2021-08-11 16:15
 **/
@Api(tags = "物业预存款余额")
@RestController
@RequestMapping("/advance/deposit")
@ApiJSYController
@Login
public class PropertyAdvanceDepositController {
    
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyAdvanceDepositService propertyAdvanceDepositService;
    
    /**
     * @program: com.jsy.community
     * @description: 预存款充值余额
     * @author: DKS
     * @create: 2021-08-11 16:23
     **/
    @Login
    @ApiOperation("新增预存款充值余额")
    @PostMapping("/add/recharge")
    public CommonResult addRechargePropertyAdvanceDeposit(@RequestBody PropertyAdvanceDepositEntity propertyAdvanceDepositEntity){
        if(propertyAdvanceDepositEntity.getBalance() == null || propertyAdvanceDepositEntity.getHouseId() == null || propertyAdvanceDepositEntity.getMobile() == null){
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少类型参数");
        }
        ValidatorUtils.validateEntity(propertyAdvanceDepositEntity);
        AdminInfoVo loginUser = UserUtils.getAdminUserInfo();
        propertyAdvanceDepositEntity.setCommunityId(loginUser.getCommunityId());
        propertyAdvanceDepositEntity.setCreateBy(loginUser.getUid());
        boolean result = propertyAdvanceDepositService.addRechargePropertyAdvanceDeposit(propertyAdvanceDepositEntity);
        return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"新增物业押金账单失败");
    }
    
    /**
     * @program: com.jsy.community
     * @description: 修改预存款充值余额
     * @author: DKS
     * @create: 2021-08-11 17:31
     **/
    @Login
    @ApiOperation("修改预存款充值余额")
    @PutMapping("/update/recharge")
    public CommonResult updateRechargePropertyAdvanceDeposit(@RequestBody PropertyAdvanceDepositEntity propertyAdvanceDepositEntity){
        ValidatorUtils.validateEntity(propertyAdvanceDepositEntity);
        AdminInfoVo loginUser = UserUtils.getAdminUserInfo();
        propertyAdvanceDepositEntity.setCommunityId(loginUser.getCommunityId());
        propertyAdvanceDepositEntity.setUpdateBy(UserUtils.getUserId());
        return propertyAdvanceDepositService.updateRechargePropertyAdvanceDeposit(propertyAdvanceDepositEntity)
            ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"修改预存款充值余额失败");
    }
    
    /**
     * @Description: 分页查询预存款余额
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.PropertyAdvanceDepositEntity>>
     * @Author: DKS
     * @Date: 2021/08/12
     **/
    @Login
    @ApiOperation("分页查询物业押金账单")
    @PostMapping("/query")
    public CommonResult<PageInfo<PropertyAdvanceDepositEntity>> queryPropertyDeposit(@RequestBody BaseQO<PropertyAdvanceDepositQO> baseQO) {
        PropertyAdvanceDepositQO query = baseQO.getQuery();
        if(query == null){
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少查询类型");
        }
        query.setCommunityId(UserUtils.getAdminCommunityId());
        return CommonResult.ok(propertyAdvanceDepositService.queryPropertyAdvanceDeposit(baseQO));
    }
}
