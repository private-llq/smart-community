package com.jsy.community.controller;

import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.IPropertyDepositService;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyDepositEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyDepositQO;
import com.jsy.community.util.QRCodeGenerator;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.StringUtils;
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
// @ApiJSYController
public class PropertyDepositController {
    
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyDepositService propertyDepositService;
    
    /**
     * @program: com.jsy.community
     * @description: 新增物业押金账单
     * @author: DKS
     * @create: 2021-08-10 17:35
     **/
    @ApiOperation("新增物业押金账单")
    @PostMapping("/add")
    @businessLog(operation = "新增",content = "新增了【物业押金账单】")
    @Permit("community:property:deposit:add")
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
    @ApiOperation("修改物业押金账单")
    @PutMapping("/update")
    @businessLog(operation = "编辑",content = "更新了【物业押金账单】")
    @Permit("community:property:deposit:update")
    public CommonResult updatePropertyDeposit(@RequestBody PropertyDepositEntity propertyDepositEntity){
        ValidatorUtils.validateEntity(propertyDepositEntity);
        AdminInfoVo loginUser = UserUtils.getAdminUserInfo();
        propertyDepositEntity.setCommunityId(loginUser.getCommunityId());
        propertyDepositEntity.setUpdateBy(UserUtils.getId());
        return propertyDepositService.updatePropertyDeposit(propertyDepositEntity)
            ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"修改物业押金账单失败");
    }
    
    /**
     * @program: com.jsy.community
     * @description: 删除物业押金账单
     * @author: DKS
     * @create: 2021-08-11 9:20
     **/
    @ApiOperation("删除物业押金账单")
    @DeleteMapping("/delete")
    @businessLog(operation = "删除",content = "删除了【物业押金账单】")
    @Permit("community:property:deposit:delete")
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
    @ApiOperation("分页查询物业押金账单")
    @PostMapping("/query")
    @Permit("community:property:deposit:query")
    public CommonResult<PageInfo<PropertyDepositEntity>> queryPropertyDeposit(@RequestBody BaseQO<PropertyDepositQO> baseQO) {
        PropertyDepositQO query = baseQO.getQuery();
        if(query == null){
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少查询类型");
        }
        query.setCommunityId(UserUtils.getAdminCommunityId());
        return CommonResult.ok(propertyDepositService.queryPropertyDeposit(baseQO));
    }
    
    /**
     * @Description: 生成押金凭证二维码
     * @Param: [url]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: DKS
     * @Date: 2021/08/16
     **/
    @ApiOperation("生成押金凭证二维码")
    @GetMapping("/generate/qr/code")
    @Permit("community:property:deposit:generate:qr:code")
    public CommonResult GenerateQRCode(String url) {
        // TODO:url是app路径地址,后面需要写死路径，没有入参，前端只需要返回qrCodeUrl
        String qrCodeUrl = "";
        try {
            byte[] bytes = QRCodeGenerator.generateQRCode(url, 300, 300);
            qrCodeUrl = MinioUtils.uploadDeposit(bytes, BusinessConst.DEPOSIT_QR_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isEmpty(qrCodeUrl)) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"生成押金凭证二维码失败");
        }
        return CommonResult.ok(qrCodeUrl);
    }
    
    /**
     * @Description: 通过id获取押金凭证数据
     * @Param: [id]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: DKS
     * @Date: 2021/08/20
     **/
    @ApiOperation("通过id获取押金凭证数据")
    @GetMapping("/getDepositById")
    @Permit("community:property:deposit:getDepositById")
    public CommonResult getDepositById(Long id) {
        PropertyDepositEntity propertyDepositEntity = new PropertyDepositEntity();
        propertyDepositEntity.setCommunityId(UserUtils.getAdminCommunityId());
        return CommonResult.ok(propertyDepositService.getDepositById(id));
    }
}
