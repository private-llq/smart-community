package com.jsy.community.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.jsy.community.annotation.PropertyFinanceLog;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.property.*;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.FinanceOrderOperationQO;
import com.jsy.community.qo.property.FinanceOrderQO;
import com.jsy.community.qo.property.StatementNumQO;
import com.jsy.community.util.excel.impl.FinanceExcelImpl;
import com.jsy.community.utils.*;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.jsy.community.vo.property.FinanceImportErrorVO;
import com.jsy.community.vo.property.FinanceOrderAndCarOrHouseInfoVO;
import com.jsy.community.vo.property.PropertyFinanceOrderVO;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jodd.util.StringUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @program: com.jsy.community
 * @description: ??????????????????
 * @author: Hu
 * @create: 2021-04-20 16:35
 **/
@Api(tags = "??????????????????")
@RestController
@RequestMapping("/financeOrder")
// @ApiJSYController
public class PropertyFinanceOrderController {
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceOrderService propertyFinanceOrderService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IHouseService houseService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IProprietorService proprietorService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarPositionService carPositionService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFeeRuleService propertyFeeRuleService;

    @Autowired
    private FinanceExcelImpl financeExcel;

    @ApiOperation("??????????????????????????????")
    @PostMapping("/list")
    @Permit("community:property:financeOrder:list")
    public CommonResult list(@RequestBody BaseQO<FinanceOrderQO> baseQO) {
        FinanceOrderQO query = baseQO.getQuery();
        query.setCommunityId(UserUtils.getAdminCommunityId());
        baseQO.setQuery(query);
        AdminInfoVo userInfo = UserUtils.getAdminInfo();
        Map<String, Object> map = propertyFinanceOrderService.findList(userInfo, baseQO);
        return CommonResult.ok(map);
    }

    @ApiOperation("??????id????????????")
    @PostMapping("/getIds")
    @Permit("community:property:financeOrder:getIds")
    public CommonResult getIds(@RequestParam String ids) {
        List<PropertyFinanceOrderEntity> list = propertyFinanceOrderService.getIds(ids, UserUtils.getAdminCommunityId());
        return CommonResult.ok(list);
    }

    @ApiOperation("??????????????????????????????")
    @GetMapping("/carList")
    @Permit("community:property:financeOrder:carList")
    public CommonResult carList() {
        List<CarPositionEntity> list = propertyFinanceOrderService.carList(UserUtils.getAdminCommunityId());
        return CommonResult.ok(list);
    }

    @ApiOperation("????????????????????????")
    @PutMapping("/updateOrder")
    @businessLog(operation = "??????", content = "?????????????????????????????????")
    @Permit("community:property:financeOrder:updateOrder")
    public CommonResult updateOrder(@RequestParam("id") Long id, @RequestParam("coupon") BigDecimal coupon) {
        AdminInfoVo userInfo = UserUtils.getAdminInfo();
        if (coupon.compareTo(BigDecimal.ZERO) < 0) {
            throw new PropertyException(JSYError.REQUEST_PARAM.getCode(), "??????????????????????????????");
        }
        propertyFinanceOrderService.updateOrder(id, coupon);
        return CommonResult.ok();
    }

    @ApiOperation("??????????????????")
    @DeleteMapping("/delete")
    @businessLog(operation = "??????", content = "???????????????????????????????????????")
    @Permit("community:property:financeOrder:delete")
    public CommonResult delete(@RequestParam("id") Long id) {
        AdminInfoVo userInfo = UserUtils.getAdminInfo();
        propertyFinanceOrderService.delete(id);
        return CommonResult.ok();
    }

    @ApiOperation("??????????????????")
    @DeleteMapping("/deleteIds")
    @businessLog(operation = "??????", content = "???????????????????????????????????????")
    @Permit("community:property:financeOrder:deleteIds")
    public CommonResult deleteIds(@RequestParam("ids") String ids) {
        AdminInfoVo userInfo = UserUtils.getAdminInfo();
        propertyFinanceOrderService.deleteIds(ids);
        return CommonResult.ok();
    }

    @ApiOperation("??????????????????????????????")
    @GetMapping("/getFeeList")
    @businessLog(operation = "??????", content = "?????????????????????????????????")
    @Permit("community:property:financeOrder:getFeeList")
    public CommonResult getFeeList() {
        List<PropertyFeeRuleEntity> list = propertyFinanceOrderService.getFeeList(UserUtils.getAdminCommunityId());
        return CommonResult.ok(list);
    }

    @ApiOperation("????????????????????????")
    @DeleteMapping("/deletes")
    @businessLog(operation = "??????", content = "???????????????????????????????????????")
    @Permit("community:property:financeOrder:deletes")
    public CommonResult deletes(@RequestBody FinanceOrderOperationQO financeOrderOperationQO) {
        AdminInfoVo userInfo = UserUtils.getAdminInfo();
        propertyFinanceOrderService.deletes(financeOrderOperationQO);
        return CommonResult.ok();
    }

    @ApiOperation("????????????????????????")
    @PutMapping("/update")
    @businessLog(operation = "??????", content = "?????????????????????????????????????????????")
    @Permit("community:property:financeOrder:update")
    public CommonResult update(@RequestParam("id") Long id) {
        AdminInfoVo userInfo = UserUtils.getAdminInfo();
        propertyFinanceOrderService.update(id);
        return CommonResult.ok();
    }

    @ApiOperation("??????????????????????????????")
    @PutMapping("/updateStatusIds")
    @businessLog(operation = "??????", content = "????????????????????????????????????????????????")
    @Permit("community:property:financeOrder:updateStatusIds")
    public CommonResult updateStatusIds(@RequestParam("ids") String ids, Integer hide) {
        AdminInfoVo userInfo = UserUtils.getAdminInfo();
        propertyFinanceOrderService.updateStatusIds(ids, hide);
        return CommonResult.ok();
    }

    @ApiOperation("??????????????????????????????")
    @PutMapping("/updates")
    @businessLog(operation = "??????", content = "?????????????????????????????????????????????")
    @Permit("community:property:financeOrder:updates")
    public CommonResult updates(@RequestBody FinanceOrderOperationQO financeOrderOperationQO) {
        AdminInfoVo userInfo = UserUtils.getAdminInfo();
        propertyFinanceOrderService.updates(financeOrderOperationQO);
        return CommonResult.ok();
    }

    @ApiOperation("??????????????????????????????")
    @GetMapping("/getOrderNum")
    @Permit("community:property:financeOrder:getOrderNum")
    public CommonResult getOrderNum(@RequestParam("id") Long id) {
        AdminInfoVo userInfo = UserUtils.getAdminInfo();
        PropertyFinanceOrderVO propertyFinanceOrderVO = propertyFinanceOrderService.getOrderNum(userInfo, id);
        return CommonResult.ok(propertyFinanceOrderVO);
    }

    /**
     * @Description: ????????????????????? (????????????)
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/4/24
     **/
    @ApiOperation("?????????????????????")
    @PostMapping("paid")
    @Permit("community:property:financeOrder:paid")
    public CommonResult queryPaid(@RequestBody BaseQO<PropertyFinanceOrderEntity> baseQO) {
        if (baseQO.getQuery() == null) {
            baseQO.setQuery(new PropertyFinanceOrderEntity());
        }
        baseQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
        return CommonResult.ok(propertyFinanceOrderService.queryPaid(baseQO), "????????????");
    }

    /**
     * @Description: ???????????? (????????????)
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/4/23
     **/
    @ApiOperation("????????????")
    @PostMapping("page")
    @Permit("community:property:financeOrder:page")
    public CommonResult queryUnionPage(@RequestBody BaseQO<PropertyFinanceOrderEntity> baseQO) {
        if (baseQO.getQuery() == null) {
            baseQO.setQuery(new PropertyFinanceOrderEntity());
        }
        baseQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
        return CommonResult.ok(propertyFinanceOrderService.queryPage(baseQO), "????????????");
    }

    /**
     * @Author: Pipi
     * @Description: ????????????????????????????????????
     * @Param: baseQO:
     * @Return: com.jsy.community.vo.CommonResult
     * @Date: 2021/4/24 11:42
     **/
    @ApiOperation("????????????????????????????????????")
    @PostMapping("/getPageByStatemenNum")
    @Permit("community:property:financeOrder:getPageByStatemenNum")
    public CommonResult getPageByStatemenNum(@RequestBody BaseQO<StatementNumQO> baseQO) {
        ValidatorUtils.validatePageParam(baseQO);
        if (baseQO.getQuery() == null) {
            return CommonResult.error(JSYError.BAD_REQUEST);
        }
        ValidatorUtils.validateEntity(baseQO.getQuery());
        return CommonResult.ok(propertyFinanceOrderService.queryPageByStatemenNum(baseQO), "????????????");
    }

    /**
     * @Author: Pipi
     * @Description: ????????????-????????????
     * @Param: :
     * @Return: org.springframework.http.ResponseEntity<byte [ ]>
     * @Date: 2021/4/25 15:49
     **/
    @ApiOperation("????????????-????????????")
    @PostMapping("/downloadOrderList")
    @Permit("community:property:financeOrder:downloadOrderList")
    public ResponseEntity<byte[]> downloadOrderList(@RequestBody PropertyFinanceOrderEntity propertyFinanceOrderEntity) {
        propertyFinanceOrderEntity.setCommunityId(UserUtils.getAdminCommunityId());
        List<PropertyFinanceOrderEntity> orderEntities = propertyFinanceOrderService.queryExportExcelList(propertyFinanceOrderEntity);
        //??????excel ???????????????
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        //???????????????????????????????????????????????????
        multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("?????????.xlsx", StandardCharsets.UTF_8));
        //?????????????????????mime????????? xls??????
        multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
        Workbook workbook = new XSSFWorkbook();
        workbook = financeExcel.exportMaterOrder(orderEntities);
        //???workbook?????????????????????????????? ???????????????????????????????????????
        try {
            return new ResponseEntity<>(ExcelUtil.readWorkbook(workbook), multiValueMap, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
        }
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????-????????????
     * @Param:
     * @Return: com.jsy.community.vo.CommonResult
     * @Date: 2021/8/17 16:00
     **/
    @ApiOperation("??????????????????-????????????")
    @PostMapping("/getFinanceForm/community/income")
    @Permit("community:property:financeOrder:getFinanceForm:community:income")
    public CommonResult getFinanceFormCommunityIncome(@RequestBody PropertyFinanceFormEntity propertyFinanceFormEntity) {
        List<String> communityIdList = UserUtils.getAdminCommunityIdList();
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
//            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"??????????????????");
//        }
//        propertyFinanceFormEntity.setCommunityId(UserUtils.getAdminCommunityId());
        return CommonResult.ok(propertyFinanceOrderService.getFinanceFormCommunityIncome(propertyFinanceFormEntity, communityIdList), "????????????");
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????-??????????????????
     * @Param:
     * @Return: com.jsy.community.vo.CommonResult
     * @Date: 2021/8/18 11:08
     **/
    @ApiOperation("??????????????????-??????????????????")
    @PostMapping("/getFinanceForm/community/charge")
    @Permit("community:property:financeOrder:getFinanceForm:community:charge")
    public CommonResult getFinanceFormCommunityCharge(@RequestBody PropertyFinanceFormChargeEntity propertyFinanceFormChargeEntity) {
        List<String> communityIdList = UserUtils.getAdminCommunityIdList();
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
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "??????????????????");
        }
//        propertyFinanceFormChargeEntity.setCommunityId(UserUtils.getAdminCommunityId());

        List<PropertyFinanceFormChargeEntity> propertyFinanceFormChargeEntityList = null;
        switch (propertyFinanceFormChargeEntity.getType()) {
            case 1:
                // ?????????????????????
                propertyFinanceFormChargeEntityList = propertyFinanceOrderService.getFinanceFormCommunityChargeByOrderGenerateTime(propertyFinanceFormChargeEntity, communityIdList);
                break;
            case 2:
                // ?????????????????????
                propertyFinanceFormChargeEntityList = propertyFinanceOrderService.getFinanceFormCommunityChargeByOrderPeriodTime(propertyFinanceFormChargeEntity, communityIdList);
                break;
            default:
                break;
        }
        if (propertyFinanceFormChargeEntityList == null) {
            throw new JSYException(JSYError.NOT_FOUND.getCode(), "????????????");
        }
        return CommonResult.ok(propertyFinanceFormChargeEntityList, "????????????");
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????-????????????
     * @Param:
     * @Return: com.jsy.community.vo.CommonResult
     * @Date: 2021/8/19 9:31
     **/
    @ApiOperation("??????????????????-????????????")
    @PostMapping("/getCollectionForm/collection")
    @Permit("community:property:financeOrder:getCollectionForm:collection")
    public CommonResult getCollectionFormCollection(@RequestBody PropertyCollectionFormEntity propertyCollectionFormEntity) {
        List<String> communityIdList = UserUtils.getAdminCommunityIdList();
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
//            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"??????????????????");
//        }
//        propertyCollectionFormEntity.setCommunityId(UserUtils.getAdminCommunityId());
        return CommonResult.ok(propertyFinanceOrderService.getCollectionFormCollection(propertyCollectionFormEntity, communityIdList), "????????????");
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????-????????????
     * @Param:
     * @Return: com.jsy.community.vo.CommonResult
     * @Date: 2021/8/19 9:31
     **/
    @ApiOperation("??????????????????-????????????")
    @PostMapping("/v2/getCollectionForm/collection")
    @Permit("community:property:financeOrder:v2:getCollectionForm:collection")
    public CommonResult getCollectionFormCollectionV2(@RequestBody PropertyCollectionFormEntity propertyCollectionFormEntity) {
        List<String> communityIdList = UserUtils.getAdminCommunityIdList();
        try {
            if (propertyCollectionFormEntity.getYear() != null) {
                String firstYearDateOfAmount = DateCalculateUtil.getFirstYearDateOfAmount(propertyCollectionFormEntity.getYear());
                String lastYearDateOfAmount = DateCalculateUtil.getLastYearDateOfAmount(propertyCollectionFormEntity.getYear());
                propertyCollectionFormEntity.setStartTime(LocalDate.parse(firstYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyCollectionFormEntity.setEndTime(LocalDate.parse(lastYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            if (StringUtil.isNotBlank(propertyCollectionFormEntity.getMonthStr())) {
                String firstMouthDateOfAmount = DateCalculateUtil.getFirstMouthDateOfAmount(propertyCollectionFormEntity.getMonthStr() + "-01");
                String lastMouthDateOfAmount = DateCalculateUtil.getLastMouthDateOfAmount(propertyCollectionFormEntity.getMonthStr() + "-01");
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
//            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"??????????????????");
//        }
//        propertyCollectionFormEntity.setCommunityId(UserUtils.getAdminCommunityId());
        return CommonResult.ok(propertyFinanceOrderService.getCollectionFormCollection(propertyCollectionFormEntity, communityIdList), "????????????");
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????-????????????
     * @Param:
     * @Return: com.jsy.community.vo.CommonResult
     * @Date: 2021/8/19 11:24
     **/
    @ApiOperation("??????????????????-????????????")
    @PostMapping("/getCollectionForm/order")
    @Permit("community:property:financeOrder:getCollectionForm:order")
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
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "??????????????????");
        }
//        propertyCollectionFormEntity.setCommunityId(UserUtils.getAdminCommunityId());

        List<PropertyCollectionFormEntity> propertyCollectionFormEntities = new ArrayList<>();
        PropertyCollectionFormEntity propertyCollectionFormEntityList = null;
        switch (propertyCollectionFormEntity.getType()) {
            case 1:
                // ?????????????????????
                propertyCollectionFormEntityList = propertyFinanceOrderService.getCollectionFormOrderByOrderGenerateTime(propertyCollectionFormEntity);
                break;
            case 2:
                // ?????????????????????
                propertyCollectionFormEntityList = propertyFinanceOrderService.getCollectionFormOrderByOrderPeriodTime(propertyCollectionFormEntity);
                break;
            default:
                break;
        }
        propertyCollectionFormEntities.add(propertyCollectionFormEntityList);
        return CommonResult.ok(propertyCollectionFormEntities, "????????????");
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????-????????????
     * @Param: :
     * @Return: org.springframework.http.ResponseEntity<byte [ ]>
     * @Date: 2021/8/19 15:49
     **/
    @ApiOperation("??????????????????-????????????")
    @PostMapping("/downloadFinanceFormList")
    @Permit("community:property:financeOrder:downloadFinanceFormList")
    public ResponseEntity<byte[]> downloadFinanceFormList(@RequestBody PropertyFinanceFormEntity propertyFinanceFormEntity) {
        List<String> communityIdList = UserUtils.getAdminCommunityIdList();
//        propertyFinanceFormEntity.setCommunityId(UserUtils.getAdminCommunityId());
        List<PropertyFinanceFormEntity> orderEntities = propertyFinanceOrderService.queryExportExcelFinanceFormList(propertyFinanceFormEntity, communityIdList);
        //??????excel ???????????????
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        //???????????????????????????????????????????????????
        multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("????????????-????????????.xlsx", StandardCharsets.UTF_8));
        //?????????????????????mime????????? xls??????
        multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
        Workbook workbook = new XSSFWorkbook();
        workbook = financeExcel.exportFinanceForm(orderEntities);
        //???workbook?????????????????????????????? ???????????????????????????????????????
        try {
            return new ResponseEntity<>(ExcelUtil.readWorkbook(workbook), multiValueMap, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
        }
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????-??????????????????
     * @Param: :
     * @Return: org.springframework.http.ResponseEntity<byte [ ]>
     * @Date: 2021/8/19 16:09
     **/
    @ApiOperation("??????????????????-??????????????????")
    @PostMapping("/downloadChargeList")
    @Permit("community:property:financeOrder:downloadChargeList")
    public ResponseEntity<byte[]> downloadChargeList(@RequestBody PropertyFinanceFormChargeEntity propertyFinanceFormChargeEntity) {
        List<String> communityIdList = UserUtils.getAdminCommunityIdList();
//        propertyFinanceFormChargeEntity.setCommunityId(UserUtils.getAdminCommunityId());
        List<PropertyFinanceFormChargeEntity> orderEntities = propertyFinanceOrderService.queryExportExcelChargeList(propertyFinanceFormChargeEntity, communityIdList);
        //??????excel ???????????????
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        //???????????????????????????????????????????????????
        multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("????????????-??????????????????.xlsx", StandardCharsets.UTF_8));
        //?????????????????????mime????????? xls??????
        multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
        Workbook workbook = new XSSFWorkbook();
        workbook = financeExcel.exportCharge(orderEntities);
        //???workbook?????????????????????????????? ???????????????????????????????????????
        try {
            return new ResponseEntity<>(ExcelUtil.readWorkbook(workbook), multiValueMap, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
        }
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????-????????????
     * @Param: :
     * @Return: org.springframework.http.ResponseEntity<byte [ ]>
     * @Date: 2021/8/19 16:11
     **/
    @ApiOperation("??????????????????-????????????")
    @PostMapping("/downloadCollectionFormList")
    @Permit("community:property:financeOrder:downloadCollectionFormList")
    public ResponseEntity<byte[]> downloadCollectionFormList(@RequestBody PropertyCollectionFormEntity propertyCollectionFormEntity) {
        List<String> communityIdList = UserUtils.getAdminCommunityIdList();
//        propertyCollectionFormEntity.setCommunityId(UserUtils.getAdminCommunityId());
        List<PropertyCollectionFormEntity> orderEntities = propertyFinanceOrderService.queryExportExcelCollectionFormList(propertyCollectionFormEntity, communityIdList);
        //??????excel ???????????????
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        //???????????????????????????????????????????????????
        multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("????????????-????????????.xlsx", StandardCharsets.UTF_8));
        //?????????????????????mime????????? xls??????
        multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
        Workbook workbook = new XSSFWorkbook();
        workbook = financeExcel.exportCollectionForm(orderEntities);
        //???workbook?????????????????????????????? ???????????????????????????????????????
        try {
            return new ResponseEntity<>(ExcelUtil.readWorkbook(workbook), multiValueMap, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
        }
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????-????????????
     * @Param: :
     * @Return: org.springframework.http.ResponseEntity<byte [ ]>
     * @Date: 2021/8/19 15:49
     **/
    @ApiOperation("??????????????????-????????????")
    @PostMapping("/downloadCollectionFormOrderList")
    @Permit("community:property:financeOrder:downloadCollectionFormOrderList")
    public ResponseEntity<byte[]> downloadCollectionFormOrderList(@RequestBody PropertyCollectionFormEntity propertyCollectionFormEntity) {
//        propertyCollectionFormEntity.setCommunityId(UserUtils.getAdminCommunityId());
        List<PropertyCollectionFormEntity> orderEntities = propertyFinanceOrderService.queryExportExcelCollectionFormOrderList(propertyCollectionFormEntity);
        //??????excel ???????????????
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        //???????????????????????????????????????????????????
        multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("????????????-????????????.xlsx", StandardCharsets.UTF_8));
        //?????????????????????mime????????? xls??????
        multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
        Workbook workbook = new XSSFWorkbook();
        workbook = financeExcel.exportCollectionFormOrder(orderEntities);
        //???workbook?????????????????????????????? ???????????????????????????????????????
        try {
            return new ResponseEntity<>(ExcelUtil.readWorkbook(workbook), multiValueMap, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
        }
    }

    /**
     * @Description: ??????????????????????????????
     * @Param: [propertyFinanceOrderEntity]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: DKS
     * @Date: 2021/08/26 09:35
     **/
    @ApiOperation("??????????????????????????????")
    @PostMapping("/temporary/charges")
    @businessLog(operation = "??????", content = "???????????????????????????????????????")
    @PropertyFinanceLog(operation = "????????????", type = 2)
    @Permit("community:property:financeOrder:temporary:charges")
    public CommonResult addTemporaryCharges(@RequestBody PropertyFinanceOrderEntity propertyFinanceOrderEntity) {
        if (propertyFinanceOrderEntity.getAssociatedType() == null || propertyFinanceOrderEntity.getTargetId() == null || propertyFinanceOrderEntity.getFeeRuleName() == null
                || propertyFinanceOrderEntity.getPropertyFee() == null || propertyFinanceOrderEntity.getBeginTime() == null || propertyFinanceOrderEntity.getOverTime() == null) {
            throw new PropertyException(JSYError.REQUEST_PARAM.getCode(), "??????????????????");
        }
        ValidatorUtils.validateEntity(propertyFinanceOrderEntity);
        AdminInfoVo loginUser = UserUtils.getAdminInfo();
        propertyFinanceOrderEntity.setCommunityId(loginUser.getCommunityId());
        PropertyFinanceOrderEntity entity = propertyFinanceOrderService.addTemporaryCharges(propertyFinanceOrderEntity);
        return entity != null ? CommonResult.ok(entity) : CommonResult.error(JSYError.INTERNAL.getCode(), "????????????????????????????????????");
    }

    /**
     * @Description: ?????????????????????
     * @Param: [propertyFinanceOrderEntity]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: DKS
     * @Date: 2021/09/06 10:16
     **/
    @ApiOperation("?????????????????????")
    @PostMapping("/collection")
    @Permit("community:property:financeOrder:collection")
    public CommonResult collection(@RequestParam(value = "ids") List<Long> ids, @RequestParam(value = "payType") Integer payType) {
        if (ids == null || ids.size() <= 0 || payType == null) {
            throw new PropertyException(JSYError.REQUEST_PARAM.getCode(), "????????????");
        }
        Long communityId = UserUtils.getAdminCommunityId();
        return propertyFinanceOrderService.collection(ids, communityId, payType)
                ? CommonResult.ok("????????????") : CommonResult.error(JSYError.INTERNAL.getCode(), "????????????");
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????????????????
     * @Date: 2021/9/7 9:30
     **/
    @ApiOperation("??????????????????????????????")
    @PostMapping("/downloadFinanceExcelTemplate")
    @Permit("community:property:financeOrder:downloadFinanceExcelTemplate")
    public ResponseEntity<byte[]> downloadFinanceExcelTemplate() {
        //??????excel ???????????????
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        //???????????????????????????????????????????????????
        multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("????????????.xlsx", StandardCharsets.UTF_8));
        //?????????????????????mime????????? xls??????
        multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
        Workbook workbook = financeExcel.exportFinanceTemplate();
        //???workbook?????????????????????????????? ???????????????????????????????????????
        try {
            return new ResponseEntity<>(ExcelUtil.readWorkbook(workbook), multiValueMap, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
        }
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????
     * @Param: excel:
     * @Return: com.jsy.community.vo.CommonResult
     * @Date: 2021/9/7 11:25
     **/
    @ApiOperation("??????????????????")
    @PostMapping("/importFinanceExcel")
    @Permit("community:property:financeOrder:importFinanceExcel")
    public CommonResult importFinanceExcel(MultipartFile excel, @RequestParam("orderStatus") Integer orderStatus) {
        //????????????
        validFileSuffix(excel);
        Long adminCommunityId = UserUtils.getAdminCommunityId();
        String userId = UserUtils.getUserId();
        ArrayList<FinanceImportErrorVO> errorVos = new ArrayList<>(32);
        List<PropertyFinanceOrderEntity> propertyFinanceOrderEntities = financeExcel.importFinanceExcel(excel, errorVos);
        List<HouseEntity> allHouse = houseService.getAllHouse(adminCommunityId);
        List<CarPositionEntity> allCarPosition = carPositionService.getAll(adminCommunityId);
        // ??????????????????????????? ??? ????????????????????????????????????????????????
        Iterator<PropertyFinanceOrderEntity> iterator = propertyFinanceOrderEntities.iterator();
        while (iterator.hasNext()) {
            PropertyFinanceOrderEntity propertyFinanceOrderEntity = iterator.next();
            // ?????????????????????
            if (propertyFinanceOrderEntity.getAssociatedType() == 1) {
                // ??????????????????
                for (HouseEntity houseEntity : allHouse) {
                    if ((houseEntity.getBuilding() + houseEntity.getUnit() + houseEntity.getDoor()).equals(propertyFinanceOrderEntity.getFinanceTarget())) {
                        // ??????????????????id
                        propertyFinanceOrderEntity.setTargetId(houseEntity.getId());
                    }
                }
                List<Long> houseIdLists = new ArrayList<>();
                // ??????????????????????????????id
                List<Long> houseIdList = proprietorService.queryBindHouseByMobile(propertyFinanceOrderEntity.getMobile(), adminCommunityId);
                for (Long houseId : houseIdList) {
                    if (houseId.equals(propertyFinanceOrderEntity.getTargetId())) {
                        houseIdLists.add(houseId);
                    }
                }
                if (houseIdLists.size() <= 0) {
                    iterator.remove();
                    FinanceImportErrorVO errorVO = new FinanceImportErrorVO();
                    errorVO.setRealName(propertyFinanceOrderEntity.getRealName());
                    errorVO.setMobile(propertyFinanceOrderEntity.getMobile());
                    errorVO.setTargetType("??????");
                    errorVO.setFinanceTarget(propertyFinanceOrderEntity.getFinanceTarget());
                    errorVO.setFeeRuleName(propertyFinanceOrderEntity.getFeeRuleName());
                    errorVO.setBeginTime(propertyFinanceOrderEntity.getBeginTime());
                    errorVO.setOverTime(propertyFinanceOrderEntity.getOverTime());
                    errorVO.setPropertyFee(propertyFinanceOrderEntity.getPropertyFee());
                    errorVO.setRemark("????????????????????????????????????????????????!");
                    errorVos.add(errorVO);
                    // ?????????????????????
                }
            } else if (propertyFinanceOrderEntity.getAssociatedType() == 2) {
                // ??????????????????
                for (CarPositionEntity carPositionEntity : allCarPosition) {
                    if (carPositionEntity.getCarPosition().equals(propertyFinanceOrderEntity.getFinanceTarget())) {
                        // ??????????????????id
                        propertyFinanceOrderEntity.setTargetId(carPositionEntity.getId());
                    }
                }
                List<Long> carPositionIdLists = new ArrayList<>();
                // ??????????????????????????????id
                List<Long> carPositionList = carPositionService.queryBindCarPositionByMobile(propertyFinanceOrderEntity.getMobile(), adminCommunityId);
                for (Long carPositionId : carPositionList) {
                    if (carPositionId.equals(propertyFinanceOrderEntity.getTargetId())) {
                        carPositionIdLists.add(carPositionId);
                    }
                }
                if (carPositionIdLists.size() <= 0) {
                    iterator.remove();
                    FinanceImportErrorVO errorVO = new FinanceImportErrorVO();
                    errorVO.setRealName(propertyFinanceOrderEntity.getRealName());
                    errorVO.setMobile(propertyFinanceOrderEntity.getMobile());
                    errorVO.setTargetType("??????");
                    errorVO.setFinanceTarget(propertyFinanceOrderEntity.getFinanceTarget());
                    errorVO.setFeeRuleName(propertyFinanceOrderEntity.getFeeRuleName());
                    errorVO.setBeginTime(propertyFinanceOrderEntity.getBeginTime());
                    errorVO.setOverTime(propertyFinanceOrderEntity.getOverTime());
                    errorVO.setPropertyFee(propertyFinanceOrderEntity.getPropertyFee());
                    errorVO.setRemark("????????????????????????????????????????????????!");
                    errorVos.add(errorVO);
                }
            }
            // ??????????????????id
            propertyFinanceOrderEntity.setFeeRuleId(propertyFeeRuleService.selectFeeRuleIdByFeeRuleName(propertyFinanceOrderEntity.getFeeRuleName(), adminCommunityId));
            // ??????uid
            propertyFinanceOrderEntity.setUid(userId);
            // ??????????????????
            propertyFinanceOrderEntity.setOrderStatus(orderStatus);
        }
        Integer row = 0;
        if (CollectionUtil.isNotEmpty(propertyFinanceOrderEntities)) {
            //????????????????????? ??????????????????????????????????????????
            row = propertyFinanceOrderService.saveFinanceOrder(propertyFinanceOrderEntities, adminCommunityId, userId);
        }
        //excel??????????????????????????? ??????????????????
        String errorExcelAddr = null;
        //??????excel????????????????????? ?????????????????????????????????
        if (CollectionUtil.isNotEmpty(errorVos)) {
            errorExcelAddr = uploadFinanceOrderErrorExcel(errorVos);
        }

        //??????????????????
        return CommonResult.ok(new FinanceImportErrorVO(row, errorVos.size(), errorExcelAddr));
    }

    /**
     * excel ????????????????????? ????????????
     *
     * @param file excel??????
     */
    private void validFileSuffix(MultipartFile file) {
        //??????????????????
        if (null == file) {
            throw new JSYException(JSYError.BAD_REQUEST);
        }
        //??????????????????
        boolean extension = FilenameUtils.isExtension(file.getOriginalFilename(), ExcelUtil.SUPPORT_EXCEL_EXTENSION);
        if (!extension) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "?????????excel??????!");
        }
    }

    /**
     * @Author: DKS
     * @Description: ???????????????????????????????????? ??? ???????????????excel??????????????????????????????
     * @Param: errorVos:
     * @Return: java.lang.String:  ??????excel??????????????????
     * @Date: 2021/9/7 16:07
     **/
    public String uploadFinanceOrderErrorExcel(List<FinanceImportErrorVO> errorVos) {
        Workbook workbook = financeExcel.exportFinanceOrderErrorExcel(errorVos);
        try {
            byte[] bytes = ExcelUtil.readWorkbook(workbook);
            MultipartFile multipartFile = new MockMultipartFile("file", "houseErrorExcel", "application/vnd.ms-excel", bytes);
            return MinioUtils.upload(multipartFile, "house-error-excel");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????
     * @Param: excel:
     * @Return: com.jsy.community.vo.CommonResult
     * @Date: 2021/9/8 10:30
     **/
    @ApiOperation("??????????????????")
    @PostMapping("/downloadFinanceList")
    @Permit("community:property:financeOrder:downloadFinanceList")
    public ResponseEntity<byte[]> downloadFinanceList(@RequestBody PropertyFinanceOrderEntity qo) {
        qo.setCommunityId(UserUtils.getAdminCommunityId());
        List<PropertyFinanceOrderEntity> propertyFinanceOrderEntities = propertyFinanceOrderService.queryExportFinanceExcel(qo);
        //??????excel ???????????????
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        //???????????????????????????????????????????????????
        multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("???????????????.xlsx", StandardCharsets.UTF_8));
        //?????????????????????mime????????? xls??????
        multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
        Workbook workbook = new XSSFWorkbook();
        workbook = financeExcel.exportFinance(propertyFinanceOrderEntities);
        //???workbook?????????????????????????????? ???????????????????????????????????????
        try {
            return new ResponseEntity<>(ExcelUtil.readWorkbook(workbook), multiValueMap, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
        }
    }

    @ApiOperation("????????????ID???????????????????????????/????????????")
    @GetMapping("/queryTemplateAndFinanceOrder")
    @Permit("community:property:financeOrder:queryTemplateAndFinanceOrder")
    public CommonResult<FinanceOrderAndCarOrHouseInfoVO> queryTemplateAndFinanceOrder(@RequestParam("id") Long id) {
        FinanceOrderAndCarOrHouseInfoVO result = propertyFinanceOrderService.queryTemplateAndFinanceOrder(id);
        return CommonResult.ok(result);
    }

    @PostMapping("/v2/completePropertyOrder")
    public CommonResult completePropertyOrder() {
        return CommonResult.ok();
    }
}
