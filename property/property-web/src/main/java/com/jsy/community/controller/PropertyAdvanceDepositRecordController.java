package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IPropertyAdvanceDepositRecordService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyAdvanceDepositRecordEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyAdvanceDepositRecordQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: com.jsy.community
 * @description: 物业预存款余额明细记录表
 * @author: DKS
 * @create: 2021-08-12 14:15
 **/
@Api(tags = "物业预存款余额明细记录表")
@RestController
@RequestMapping("/advance/deposit/record")
@ApiJSYController
@Login
public class PropertyAdvanceDepositRecordController {
    
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyAdvanceDepositRecordService propertyAdvanceDepositRecordService;
    
    /**
     * @program: com.jsy.community
     * @description: 新增预存款变更明细记录
     * @author: DKS
     * @create: 2021-08-12 15:23
     **/
    @Login
    @ApiOperation("新增预存款变更明细记录")
    @PostMapping("/add")
    public CommonResult addPropertyAdvanceDepositRecord(@RequestBody PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity){
	    if(propertyAdvanceDepositRecordEntity.getType() == null){
		    throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少类型参数");
	    }
	    if (propertyAdvanceDepositRecordEntity.getType().equals(1) && propertyAdvanceDepositRecordEntity.getOrderId() == null) {
		    throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少账单id参数");
	    }
	    if(propertyAdvanceDepositRecordEntity.getAdvanceDepositId() == null){
		    throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少预存款id参数");
	    }
        ValidatorUtils.validateEntity(propertyAdvanceDepositRecordEntity);
        AdminInfoVo loginUser = UserUtils.getAdminUserInfo();
        propertyAdvanceDepositRecordEntity.setCommunityId(loginUser.getCommunityId());
        propertyAdvanceDepositRecordEntity.setCreateBy(loginUser.getUid());
        boolean result = propertyAdvanceDepositRecordService.addPropertyAdvanceDepositRecord(propertyAdvanceDepositRecordEntity);
        return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"新增预存款变更明细记录失败");
    }
    
    /**
     * @Description: 预存款分页查询变更明细
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.PropertyAdvanceDepositEntity>>
     * @Author: DKS
     * @Date: 2021/08/12
     **/
    @Login
    @ApiOperation("预存款分页查询变更明细")
    @PostMapping("/query")
    public CommonResult<PageInfo<PropertyAdvanceDepositRecordEntity>> queryPropertyDepositRecord(@RequestBody BaseQO<PropertyAdvanceDepositRecordQO> baseQO) {
	    PropertyAdvanceDepositRecordQO query = baseQO.getQuery();
        if(query == null){
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少查询类型");
        }
        query.setCommunityId(UserUtils.getAdminCommunityId());
        return CommonResult.ok(propertyAdvanceDepositRecordService.queryPropertyAdvanceDepositRecord(baseQO));
    }
}
