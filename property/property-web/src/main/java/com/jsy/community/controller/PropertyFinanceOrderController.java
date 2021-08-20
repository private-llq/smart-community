package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IPropertyFinanceOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyCollectionFormEntity;
import com.jsy.community.entity.property.PropertyFinanceFormChargeEntity;
import com.jsy.community.entity.property.PropertyFinanceFormEntity;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.FinanceOrderOperationQO;
import com.jsy.community.qo.property.FinanceOrderQO;
import com.jsy.community.qo.property.StatementNumQO;
import com.jsy.community.util.excel.impl.FinanceExcelImpl;
import com.jsy.community.utils.DateCalculateUtil;
import com.jsy.community.utils.ExcelUtil;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.jsy.community.vo.property.PropertyFinanceOrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 物业房间账单
 * @author: Hu
 * @create: 2021-04-20 16:35
 **/
@Api(tags = "物业房间账单")
@RestController
@RequestMapping("/financeOrder")
@ApiJSYController
@Login
public class PropertyFinanceOrderController {
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceOrderService propertyFinanceOrderService;

    @Autowired
    private FinanceExcelImpl financeExcel;

    @ApiOperation("查询房屋所有未缴账单")
    @PostMapping("/list")
    @Login
    public CommonResult list(@RequestBody BaseQO<FinanceOrderQO> baseQO){
        AdminInfoVo userInfo = UserUtils.getAdminUserInfo();
        Map<String, Object> map=propertyFinanceOrderService.findList(userInfo,baseQO);
        return CommonResult.ok(map);
    }
    @ApiOperation("修改订单优惠金额")
    @PutMapping("/updateOrder")
    @Login
    public CommonResult updateOrder(@RequestParam("id") Long id, @RequestParam("coupon")BigDecimal coupon){
        AdminInfoVo userInfo = UserUtils.getAdminUserInfo();
        propertyFinanceOrderService.updateOrder(id,coupon);
        return CommonResult.ok();
    }
    @ApiOperation("删除一条账单")
    @DeleteMapping("/delete")
    @Login
    public CommonResult delete(@RequestParam("id") Long id){
        AdminInfoVo userInfo = UserUtils.getAdminUserInfo();
        propertyFinanceOrderService.delete(id);
        return CommonResult.ok();
    }
    @ApiOperation("删除多条条账单")
    @DeleteMapping("/deletes")
    @Login
    public CommonResult deletes(@RequestBody FinanceOrderOperationQO financeOrderOperationQO){
        AdminInfoVo userInfo = UserUtils.getAdminUserInfo();
        propertyFinanceOrderService.deletes(financeOrderOperationQO);
        return CommonResult.ok();
    }
    @ApiOperation("修改一条订单状态")
    @PutMapping("/update")
    @Login
    public CommonResult update(@RequestParam("id") Long id){
        AdminInfoVo userInfo = UserUtils.getAdminUserInfo();
        propertyFinanceOrderService.update(id);
        return CommonResult.ok();
    }
    @ApiOperation("条件修改订单状态")
    @PutMapping("/updates")
    @Login
    public CommonResult updates(@RequestBody FinanceOrderOperationQO financeOrderOperationQO){
        AdminInfoVo userInfo = UserUtils.getAdminUserInfo();
        propertyFinanceOrderService.updates(financeOrderOperationQO);
        return CommonResult.ok();
    }
    @ApiOperation("查询一条已交账单详情")
    @GetMapping("/getOrderNum")
    @Login
    public CommonResult getOrderNum(@RequestParam("id") Long id){
        AdminInfoVo userInfo = UserUtils.getAdminUserInfo();
        PropertyFinanceOrderVO propertyFinanceOrderVO=propertyFinanceOrderService.getOrderNum(userInfo,id);
        return CommonResult.ok(propertyFinanceOrderVO);
    }
    
    /**
     * @Description: 分页查询已缴费 (缴费模块)
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/4/24
     **/
    @ApiOperation("分页查询已缴费")
    @PostMapping("paid")
    public CommonResult queryPaid(@RequestBody BaseQO<PropertyFinanceOrderEntity> baseQO){
        if(baseQO.getQuery() == null){
            baseQO.setQuery(new PropertyFinanceOrderEntity());
        }
        baseQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
        return CommonResult.ok(propertyFinanceOrderService.queryPaid(baseQO),"查询成功");
    }
    
    /**
    * @Description: 分页查询 (财务模块)
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/4/23
    **/
    @ApiOperation("分页查询")
    @PostMapping("page")
    public CommonResult queryUnionPage(@RequestBody BaseQO<PropertyFinanceOrderEntity> baseQO){
        if(baseQO.getQuery() == null){
            baseQO.setQuery(new PropertyFinanceOrderEntity());
        }
        baseQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
        return CommonResult.ok(propertyFinanceOrderService.queryPage(baseQO),"查询成功");
    }

    /**
     *@Author: Pipi
     *@Description: 分页获取结算单的账单列表
     *@Param: baseQO:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/4/24 11:42
     **/
    @Login
    @ApiOperation("分页获取结算单的账单列表")
    @PostMapping("/getPageByStatemenNum")
    public CommonResult getPageByStatemenNum(@RequestBody BaseQO<StatementNumQO> baseQO) {
        ValidatorUtils.validatePageParam(baseQO);
        if (baseQO.getQuery() == null) {
            return CommonResult.error(JSYError.BAD_REQUEST);
        }
        ValidatorUtils.validateEntity(baseQO.getQuery());
        return CommonResult.ok(propertyFinanceOrderService.queryPageByStatemenNum(baseQO),"查询成功");
    }

    /**
     *@Author: Pipi
     *@Description: 物业财务-导出账单
     *@Param: :
     *@Return: org.springframework.http.ResponseEntity<byte[]>
     *@Date: 2021/4/25 15:49
     **/
    @Login
    @ApiOperation("物业财务-导出账单")
    @PostMapping("/downloadOrderList")
    public ResponseEntity<byte[]> downloadOrderList(@RequestBody PropertyFinanceOrderEntity propertyFinanceOrderEntity) {
        propertyFinanceOrderEntity.setCommunityId(UserUtils.getAdminCommunityId());
        List<PropertyFinanceOrderEntity> orderEntities = propertyFinanceOrderService.queryExportExcelList(propertyFinanceOrderEntity);
        //设置excel 响应头信息
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        //设置响应类型为附件类型直接下载这种
        multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("账单表.xlsx", StandardCharsets.UTF_8));
        //设置响应的文件mime类型为 xls类型
        multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
        Workbook workbook = new XSSFWorkbook();
        workbook = financeExcel.exportMaterOrder(orderEntities);
        //把workbook工作簿转换为字节数组 放入响应实体以附件形式输出
        try {
            return new ResponseEntity<>(ExcelUtil.readWorkbook(workbook), multiValueMap, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
        }
    }
    
    /**
     *@Author: DKS
     *@Description: 获取财务报表-小区收入
     *@Param:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/8/17 16:00
     **/
    @Login
    @ApiOperation("获取财务报表-小区收入")
    @PostMapping("/getFinanceForm/community/income")
    public CommonResult getFinanceFormCommunityIncome(@RequestBody PropertyFinanceFormEntity propertyFinanceFormEntity) {
        try {
            if (propertyFinanceFormEntity.getYear() != null) {
                String firstYearDateOfAmount = DateCalculateUtil.getFirstYearDateOfAmount(propertyFinanceFormEntity.getYear());
                String lastYearDateOfAmount = DateCalculateUtil.getLastYearDateOfAmount(propertyFinanceFormEntity.getYear());
                propertyFinanceFormEntity.setStartTime(LocalDate.parse(firstYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyFinanceFormEntity.setEndTime(LocalDate.parse(lastYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            if (propertyFinanceFormEntity.getMonth() != null) {
                String firstMouthDateOfAmount = DateCalculateUtil.getFirstMouthDateOfAmount(propertyFinanceFormEntity.getMonth());
                String lastMouthDateOfAmount = DateCalculateUtil.getLastMouthDateOfAmount(propertyFinanceFormEntity.getMonth());
                propertyFinanceFormEntity.setStartTime(LocalDate.parse(firstMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyFinanceFormEntity.setEndTime(LocalDate.parse(lastMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ValidatorUtils.validateEntity(propertyFinanceFormEntity);
        if (propertyFinanceFormEntity.getStartTime() == null && propertyFinanceFormEntity.getEndTime() == null) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少查询类型");
        }
        propertyFinanceFormEntity.setCommunityId(UserUtils.getAdminCommunityId());
        return CommonResult.ok(propertyFinanceOrderService.getFinanceFormCommunityIncome(propertyFinanceFormEntity),"查询成功");
    }
    
    /**
     *@Author: DKS
     *@Description: 获取财务报表-小区收费报表
     *@Param:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/8/18 11:08
     **/
    @Login
    @ApiOperation("获取财务报表-小区收费报表")
    @PostMapping("/getFinanceForm/community/charge")
    public CommonResult getFinanceFormCommunityCharge(@RequestBody PropertyFinanceFormChargeEntity propertyFinanceFormChargeEntity) {
        try {
            if (propertyFinanceFormChargeEntity.getMonth() != null) {
                String firstMouthDateOfAmount = DateCalculateUtil.getFirstMouthDateOfAmount(propertyFinanceFormChargeEntity.getMonth());
                String lastMouthDateOfAmount = DateCalculateUtil.getLastMouthDateOfAmount(propertyFinanceFormChargeEntity.getMonth());
                propertyFinanceFormChargeEntity.setStartTime(LocalDate.parse(firstMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyFinanceFormChargeEntity.setEndTime(LocalDate.parse(lastMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ValidatorUtils.validateEntity(propertyFinanceFormChargeEntity);
        if (propertyFinanceFormChargeEntity.getStartTime() == null || propertyFinanceFormChargeEntity.getEndTime() == null || propertyFinanceFormChargeEntity.getType() == null) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少查询类型");
        }
        propertyFinanceFormChargeEntity.setCommunityId(UserUtils.getAdminCommunityId());
        
        List<PropertyFinanceFormChargeEntity> propertyFinanceFormChargeEntityList = null;
        switch (propertyFinanceFormChargeEntity.getType()) {
            case 1:
                // 按账单生成时间
                propertyFinanceFormChargeEntityList = propertyFinanceOrderService.getFinanceFormCommunityChargeByOrderGenerateTime(propertyFinanceFormChargeEntity);
                break;
            case 2:
                // 按账单周期时间
                propertyFinanceFormChargeEntityList = propertyFinanceOrderService.getFinanceFormCommunityChargeByOrderPeriodTime(propertyFinanceFormChargeEntity);
                break;
            default:
                break;
        }
        if (propertyFinanceFormChargeEntityList == null) {
            throw new JSYException(JSYError.NOT_FOUND.getCode(),"查询为空");
        }
        return CommonResult.ok(propertyFinanceFormChargeEntityList,"查询成功");
    }
    
    /**
     *@Author: DKS
     *@Description: 获取收款报表-收款报表
     *@Param:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/8/19 9:31
     **/
    @Login
    @ApiOperation("获取收款报表-收款报表")
    @PostMapping("/getCollectionForm/collection")
    public CommonResult getCollectionFormCollection(@RequestBody PropertyCollectionFormEntity propertyCollectionFormEntity) {
        try {
            if (propertyCollectionFormEntity.getYear() != null) {
                String firstYearDateOfAmount = DateCalculateUtil.getFirstYearDateOfAmount(propertyCollectionFormEntity.getYear());
                String lastYearDateOfAmount = DateCalculateUtil.getLastYearDateOfAmount(propertyCollectionFormEntity.getYear());
                propertyCollectionFormEntity.setStartTime(LocalDate.parse(firstYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyCollectionFormEntity.setEndTime(LocalDate.parse(lastYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            if (propertyCollectionFormEntity.getMonth() != null) {
                String firstMouthDateOfAmount = DateCalculateUtil.getFirstMouthDateOfAmount(propertyCollectionFormEntity.getMonth());
                String lastMouthDateOfAmount = DateCalculateUtil.getLastMouthDateOfAmount(propertyCollectionFormEntity.getMonth());
                propertyCollectionFormEntity.setStartTime(LocalDate.parse(firstMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyCollectionFormEntity.setEndTime(LocalDate.parse(lastMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            if (propertyCollectionFormEntity.getDay() != null) {
                ZoneId zone = ZoneId.systemDefault();
                Instant instant = propertyCollectionFormEntity.getDay().atStartOfDay().atZone(zone).toInstant();
                String firstDate = DateCalculateUtil.getFirstDate(Date.from(instant));
                String lastDate = DateCalculateUtil.getLastDate(Date.from(instant));
                propertyCollectionFormEntity.setStartTime(LocalDate.parse(firstDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyCollectionFormEntity.setEndTime(LocalDate.parse(lastDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ValidatorUtils.validateEntity(propertyCollectionFormEntity);
        if (propertyCollectionFormEntity.getStartTime() == null && propertyCollectionFormEntity.getEndTime() == null) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少查询类型");
        }
        propertyCollectionFormEntity.setCommunityId(UserUtils.getAdminCommunityId());
        return CommonResult.ok(propertyFinanceOrderService.getCollectionFormCollection(propertyCollectionFormEntity),"查询成功");
    }
    
    /**
     *@Author: DKS
     *@Description: 获取收款报表-账单统计
     *@Param:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/8/19 11:24
     **/
    @Login
    @ApiOperation("获取收款报表-账单统计")
    @PostMapping("/getCollectionForm/order")
    public CommonResult getCollectionFormOrder(@RequestBody PropertyCollectionFormEntity propertyCollectionFormEntity) {
        try {
            if (propertyCollectionFormEntity.getYear() != null) {
                String firstYearDateOfAmount = DateCalculateUtil.getFirstYearDateOfAmount(propertyCollectionFormEntity.getYear());
                String lastYearDateOfAmount = DateCalculateUtil.getLastYearDateOfAmount(propertyCollectionFormEntity.getYear());
                propertyCollectionFormEntity.setStartTime(LocalDate.parse(firstYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyCollectionFormEntity.setEndTime(LocalDate.parse(lastYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            if (propertyCollectionFormEntity.getMonth() != null) {
                String firstMouthDateOfAmount = DateCalculateUtil.getFirstMouthDateOfAmount(propertyCollectionFormEntity.getMonth());
                String lastMouthDateOfAmount = DateCalculateUtil.getLastMouthDateOfAmount(propertyCollectionFormEntity.getMonth());
                propertyCollectionFormEntity.setStartTime(LocalDate.parse(firstMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyCollectionFormEntity.setEndTime(LocalDate.parse(lastMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ValidatorUtils.validateEntity(propertyCollectionFormEntity);
        if (propertyCollectionFormEntity.getStartTime() == null || propertyCollectionFormEntity.getEndTime() == null || propertyCollectionFormEntity.getType() == null) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少查询类型");
        }
        propertyCollectionFormEntity.setCommunityId(UserUtils.getAdminCommunityId());
    
        PropertyCollectionFormEntity propertyCollectionFormEntityList = null;
        switch (propertyCollectionFormEntity.getType()) {
            case 1:
                // 按账单生成时间
                propertyCollectionFormEntityList = propertyFinanceOrderService.getCollectionFormOrderByOrderGenerateTime(propertyCollectionFormEntity);
                break;
            case 2:
                // 按账单周期时间
                propertyCollectionFormEntityList = propertyFinanceOrderService.getCollectionFormOrderByOrderPeriodTime(propertyCollectionFormEntity);
                break;
            default:
                break;
        }
        if (propertyCollectionFormEntityList == null) {
            throw new JSYException(JSYError.NOT_FOUND.getCode(),"查询为空");
        }
        return CommonResult.ok(propertyCollectionFormEntityList,"查询成功");
    }
}
