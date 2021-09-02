package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.PropertyFinanceLog;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.IPropertyFinanceOrderService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.*;
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
    @businessLog(operation = "编辑",content = "更新了【订单优惠金额】")
    public CommonResult updateOrder(@RequestParam("id") Long id, @RequestParam("coupon")BigDecimal coupon){
        AdminInfoVo userInfo = UserUtils.getAdminUserInfo();
        propertyFinanceOrderService.updateOrder(id,coupon);
        return CommonResult.ok();
    }
    @ApiOperation("删除一条账单")
    @DeleteMapping("/delete")
    @Login
    @businessLog(operation = "删除",content = "删除了【一条物业房间账单】")
    public CommonResult delete(@RequestParam("id") Long id){
        AdminInfoVo userInfo = UserUtils.getAdminUserInfo();
        propertyFinanceOrderService.delete(id);
        return CommonResult.ok();
    }
    @ApiOperation("删除多条账单")
    @DeleteMapping("/deleteIds")
    @Login
    @businessLog(operation = "删除",content = "删除了【多条物业房间账单】")
    public CommonResult deleteIds(@RequestParam("ids") String ids){
        AdminInfoVo userInfo = UserUtils.getAdminUserInfo();
        propertyFinanceOrderService.deleteIds(ids);
        return CommonResult.ok();
    }
    @ApiOperation("查询当前小区缴费项目")
    @GetMapping("/getFeeList")
    @Login
    @businessLog(operation = "查询",content = "查询了【物业缴费项目】")
    public CommonResult getFeeList(){
        List<PropertyFeeRuleEntity> list = propertyFinanceOrderService.getFeeList(UserUtils.getAdminCommunityId());
        return CommonResult.ok(list);
    }
    @ApiOperation("条件删除多条账单")
    @DeleteMapping("/deletes")
    @Login
    @businessLog(operation = "删除",content = "删除了【多条物业房间账单】")
    public CommonResult deletes(@RequestBody FinanceOrderOperationQO financeOrderOperationQO){
        AdminInfoVo userInfo = UserUtils.getAdminUserInfo();
        propertyFinanceOrderService.deletes(financeOrderOperationQO);
        return CommonResult.ok();
    }
    @ApiOperation("修改一条订单状态")
    @PutMapping("/update")
    @Login
    @businessLog(operation = "编辑",content = "更新了【一条物业房间账单状态】")
    public CommonResult update(@RequestParam("id") Long id){
        AdminInfoVo userInfo = UserUtils.getAdminUserInfo();
        propertyFinanceOrderService.update(id);
        return CommonResult.ok();
    }
    @ApiOperation("修改多条物业账单状态")
    @PutMapping("/updateStatusIds")
    @Login
    @businessLog(operation = "编辑",content = "更新了【多条条物业房间账单状态】")
    public CommonResult updateStatusIds(@RequestParam("ids") String ids,Integer hide){
        AdminInfoVo userInfo = UserUtils.getAdminUserInfo();
        propertyFinanceOrderService.updateStatusIds(ids,hide);
        return CommonResult.ok();
    }
    @ApiOperation("条件修改物业订单状态")
    @PutMapping("/updates")
    @Login
    @businessLog(operation = "编辑",content = "更新了【多条物业房间账单状态】")
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
        List<Long> communityIdList = UserUtils.getAdminCommunityIdList();
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
//        if (propertyFinanceFormEntity.getStartTime() == null && propertyFinanceFormEntity.getEndTime() == null) {
//            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少查询类型");
//        }
//        propertyFinanceFormEntity.setCommunityId(UserUtils.getAdminCommunityId());
        return CommonResult.ok(propertyFinanceOrderService.getFinanceFormCommunityIncome(propertyFinanceFormEntity, communityIdList),"查询成功");
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
        List<Long> communityIdList = UserUtils.getAdminCommunityIdList();
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
        if (propertyFinanceFormChargeEntity.getType() == null) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少查询类型");
        }
//        propertyFinanceFormChargeEntity.setCommunityId(UserUtils.getAdminCommunityId());
        
        List<PropertyFinanceFormChargeEntity> propertyFinanceFormChargeEntityList = null;
        switch (propertyFinanceFormChargeEntity.getType()) {
            case 1:
                // 按账单生成时间
                propertyFinanceFormChargeEntityList = propertyFinanceOrderService.getFinanceFormCommunityChargeByOrderGenerateTime(propertyFinanceFormChargeEntity, communityIdList);
                break;
            case 2:
                // 按账单周期时间
                propertyFinanceFormChargeEntityList = propertyFinanceOrderService.getFinanceFormCommunityChargeByOrderPeriodTime(propertyFinanceFormChargeEntity, communityIdList);
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
        List<Long> communityIdList = UserUtils.getAdminCommunityIdList();
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
            if (propertyCollectionFormEntity.getDateTime() != null) {
                ZoneId zone = ZoneId.systemDefault();
                Instant instant = propertyCollectionFormEntity.getDateTime().atStartOfDay().atZone(zone).toInstant();
                String firstDate = DateCalculateUtil.getFirstDate(Date.from(instant));
                String lastDate = DateCalculateUtil.getLastDate(Date.from(instant));
                propertyCollectionFormEntity.setStartTime(LocalDate.parse(firstDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyCollectionFormEntity.setEndTime(LocalDate.parse(lastDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ValidatorUtils.validateEntity(propertyCollectionFormEntity);
//        if (propertyCollectionFormEntity.getStartTime() == null && propertyCollectionFormEntity.getEndTime() == null) {
//            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少查询类型");
//        }
//        propertyCollectionFormEntity.setCommunityId(UserUtils.getAdminCommunityId());
        return CommonResult.ok(propertyFinanceOrderService.getCollectionFormCollection(propertyCollectionFormEntity, communityIdList),"查询成功");
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
        if (propertyCollectionFormEntity.getType() == null) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少查询类型");
        }
//        propertyCollectionFormEntity.setCommunityId(UserUtils.getAdminCommunityId());
    
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
    
    /**
     *@Author: DKS
     *@Description: 导出财务报表-小区收入
     *@Param: :
     *@Return: org.springframework.http.ResponseEntity<byte[]>
     *@Date: 2021/8/19 15:49
     **/
    @Login
    @ApiOperation("导出财务报表-小区收入")
    @PostMapping("/downloadFinanceFormList")
    public ResponseEntity<byte[]> downloadFinanceFormList(@RequestBody PropertyFinanceFormEntity propertyFinanceFormEntity) {
        List<Long> communityIdList = UserUtils.getAdminCommunityIdList();
//        propertyFinanceFormEntity.setCommunityId(UserUtils.getAdminCommunityId());
        List<PropertyFinanceFormEntity> orderEntities = propertyFinanceOrderService.queryExportExcelFinanceFormList(propertyFinanceFormEntity, communityIdList);
        //设置excel 响应头信息
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        //设置响应类型为附件类型直接下载这种
        multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("财务报表-小区收入.xlsx", StandardCharsets.UTF_8));
        //设置响应的文件mime类型为 xls类型
        multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
        Workbook workbook = new XSSFWorkbook();
        workbook = financeExcel.exportFinanceForm(orderEntities);
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
     *@Description: 导出财务报表-小区收费报表
     *@Param: :
     *@Return: org.springframework.http.ResponseEntity<byte[]>
     *@Date: 2021/8/19 16:09
     **/
    @Login
    @ApiOperation("导出财务报表-小区收费报表")
    @PostMapping("/downloadChargeList")
    public ResponseEntity<byte[]> downloadChargeList(@RequestBody PropertyFinanceFormChargeEntity propertyFinanceFormChargeEntity) {
        List<Long> communityIdList = UserUtils.getAdminCommunityIdList();
//        propertyFinanceFormChargeEntity.setCommunityId(UserUtils.getAdminCommunityId());
        List<PropertyFinanceFormChargeEntity> orderEntities = propertyFinanceOrderService.queryExportExcelChargeList(propertyFinanceFormChargeEntity, communityIdList);
        //设置excel 响应头信息
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        //设置响应类型为附件类型直接下载这种
        multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("财务报表-小区收费报表.xlsx", StandardCharsets.UTF_8));
        //设置响应的文件mime类型为 xls类型
        multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
        Workbook workbook = new XSSFWorkbook();
        workbook = financeExcel.exportCharge(orderEntities);
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
     *@Description: 导出收款报表-收款报表
     *@Param: :
     *@Return: org.springframework.http.ResponseEntity<byte[]>
     *@Date: 2021/8/19 16:11
     **/
    @Login
    @ApiOperation("导出收款报表-收款报表")
    @PostMapping("/downloadCollectionFormList")
    public ResponseEntity<byte[]> downloadCollectionFormList(@RequestBody PropertyCollectionFormEntity propertyCollectionFormEntity) {
        List<Long> communityIdList = UserUtils.getAdminCommunityIdList();
//        propertyCollectionFormEntity.setCommunityId(UserUtils.getAdminCommunityId());
        List<PropertyCollectionFormEntity> orderEntities = propertyFinanceOrderService.queryExportExcelCollectionFormList(propertyCollectionFormEntity, communityIdList);
        //设置excel 响应头信息
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        //设置响应类型为附件类型直接下载这种
        multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("收款报表-收款报表.xlsx", StandardCharsets.UTF_8));
        //设置响应的文件mime类型为 xls类型
        multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
        Workbook workbook = new XSSFWorkbook();
        workbook = financeExcel.exportCollectionForm(orderEntities);
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
     *@Description: 导出收款报表-账单统计
     *@Param: :
     *@Return: org.springframework.http.ResponseEntity<byte[]>
     *@Date: 2021/8/19 15:49
     **/
    @Login
    @ApiOperation("导出收款报表-账单统计")
    @PostMapping("/downloadCollectionFormOrderList")
    public ResponseEntity<byte[]> downloadCollectionFormOrderList(@RequestBody PropertyCollectionFormEntity propertyCollectionFormEntity) {
//        propertyCollectionFormEntity.setCommunityId(UserUtils.getAdminCommunityId());
        List<PropertyCollectionFormEntity> orderEntities = propertyFinanceOrderService.queryExportExcelCollectionFormOrderList(propertyCollectionFormEntity);
        //设置excel 响应头信息
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        //设置响应类型为附件类型直接下载这种
        multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("收款报表-账单统计.xlsx", StandardCharsets.UTF_8));
        //设置响应的文件mime类型为 xls类型
        multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
        Workbook workbook = new XSSFWorkbook();
        workbook = financeExcel.exportCollectionFormOrder(orderEntities);
        //把workbook工作簿转换为字节数组 放入响应实体以附件形式输出
        try {
            return new ResponseEntity<>(ExcelUtil.readWorkbook(workbook), multiValueMap, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
        }
    }
    
    /**
     * @Description: 新增物业账单临时收费
     * @Param: [propertyFinanceOrderEntity]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: DKS
     * @Date: 2021/08/26 09:35
     **/
    @Login
    @ApiOperation("新增物业账单临时收费")
    @PostMapping("/temporary/charges")
    @businessLog(operation = "新增",content = "新增了【物业账单临时收费】")
    @PropertyFinanceLog(operation = "创建了一个临时账单")
    public CommonResult addTemporaryCharges(@RequestBody PropertyFinanceOrderEntity propertyFinanceOrderEntity){
        if(propertyFinanceOrderEntity.getAssociatedType() == null || propertyFinanceOrderEntity.getTargetId() == null || propertyFinanceOrderEntity.getFeeRuleId() == null
            || propertyFinanceOrderEntity.getPropertyFee() == null || propertyFinanceOrderEntity.getBeginTime() == null || propertyFinanceOrderEntity.getOverTime() == null){
            throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"缺少类型参数");
        }
        ValidatorUtils.validateEntity(propertyFinanceOrderEntity);
        AdminInfoVo loginUser = UserUtils.getAdminUserInfo();
        propertyFinanceOrderEntity.setCommunityId(loginUser.getCommunityId());
        return propertyFinanceOrderService.addTemporaryCharges(propertyFinanceOrderEntity)
            ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"新增物业账单临时收费失败");
    }
}
