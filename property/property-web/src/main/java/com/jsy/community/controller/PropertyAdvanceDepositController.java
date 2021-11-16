package com.jsy.community.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.IHouseService;
import com.jsy.community.api.IPropertyAdvanceDepositService;
import com.jsy.community.api.IProprietorService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.property.PropertyAdvanceDepositEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyAdvanceDepositQO;
import com.jsy.community.util.excel.impl.AdvanceDepositExcelHandlerImpl;
import com.jsy.community.utils.*;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.jsy.community.vo.property.AdvanceDepositImportErrorVO;
import com.jsy.community.vo.property.BuildingImportErrorVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.poi.ss.usermodel.Workbook;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    
    @Autowired
    private AdvanceDepositExcelHandlerImpl advanceDepositExcelHandler;
    
    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IProprietorService proprietorService;
    
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IHouseService houseService;
    
    /**
     * @program: com.jsy.community
     * @description: 预存款充值余额
     * @author: DKS
     * @create: 2021-08-11 16:23
     **/
    @Login
    @ApiOperation("新增预存款充值余额")
    @PostMapping("/add/recharge")
    @businessLog(operation = "新增",content = "新增了【预存款充值余额】")
    public CommonResult addRechargePropertyAdvanceDeposit(@RequestBody PropertyAdvanceDepositEntity propertyAdvanceDepositEntity){
        if(propertyAdvanceDepositEntity.getHouseId() == null){
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少类型参数");
        }
        if (propertyAdvanceDepositEntity.getBalance() == null && propertyAdvanceDepositEntity.getBalance().compareTo(BigDecimal.ZERO) == 0) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"请输入正确的金额");
        }
        ValidatorUtils.validateEntity(propertyAdvanceDepositEntity);
        AdminInfoVo loginUser = UserUtils.getAdminUserInfo();
        propertyAdvanceDepositEntity.setCommunityId(loginUser.getCommunityId());
        propertyAdvanceDepositEntity.setCreateBy(loginUser.getUid());
        boolean result = propertyAdvanceDepositService.addRechargePropertyAdvanceDeposit(propertyAdvanceDepositEntity);
        return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"新增预存款充值余额失败");
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
    @businessLog(operation = "编辑",content = "更新了【预存款充值余额】")
    public CommonResult updateRechargePropertyAdvanceDeposit(@RequestBody PropertyAdvanceDepositEntity propertyAdvanceDepositEntity){
        if (propertyAdvanceDepositEntity.getBalance() == null && propertyAdvanceDepositEntity.getBalance().compareTo(BigDecimal.ZERO) == 0) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"请输入正确的金额");
        }
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
    @ApiOperation("分页查询预存款余额")
    @PostMapping("/query")
    public CommonResult<PageInfo<PropertyAdvanceDepositEntity>> queryPropertyDeposit(@RequestBody BaseQO<PropertyAdvanceDepositQO> baseQO) {
        PropertyAdvanceDepositQO query = baseQO.getQuery();
        if(query == null){
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少查询类型");
        }
        query.setCommunityId(UserUtils.getAdminCommunityId());
        return CommonResult.ok(propertyAdvanceDepositService.queryPropertyAdvanceDeposit(baseQO));
    }
    
    /**
     *@Author: DKS
     *@Description: 下载充值余额导入模板
     *@Date: 2021/8/10 9:10
     **/
    @Login
    @ApiOperation("下载充值余额导入模板")
    @PostMapping("/downloadAdvanceDepositExcelTemplate")
    public ResponseEntity<byte[]> downloadHouseExcelTemplate() {
        //设置excel 响应头信息
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        //设置响应类型为附件类型直接下载这种
        multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("预存款充值.xlsx", StandardCharsets.UTF_8));
        //设置响应的文件mime类型为 xls类型
        multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
        Workbook workbook = advanceDepositExcelHandler.exportAdvanceDepositTemplate();
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
     *@Description: 导入充值余额
     *@Param: excel:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/8/13 16:58
     **/
    @Login
    @ApiOperation("导入充值余额")
    @PostMapping("/importAdvanceDepositExcel")
    public CommonResult importAdvanceDepositExcel(MultipartFile excel) {
        //参数验证
        validFileSuffix(excel);
        Long adminCommunityId = UserUtils.getAdminCommunityId();
        String userId = UserUtils.getUserId();
        ArrayList<AdvanceDepositImportErrorVO> errorVos = new ArrayList<>(32);
        List<PropertyAdvanceDepositEntity> propertyAdvanceDepositEntities = advanceDepositExcelHandler.importAdvanceDepositExcel(excel, errorVos);
        List<HouseEntity> allHouse = houseService.getAllHouse(adminCommunityId);
        // 通过物业提交的数据 和 数据库该社区已存在的数据进行效验
        Iterator<PropertyAdvanceDepositEntity> iterator = propertyAdvanceDepositEntities.iterator();
        while (iterator.hasNext()) {
            PropertyAdvanceDepositEntity propertyAdvanceDepositEntity = iterator.next();
            // 查询全部房屋
            for (HouseEntity houseEntity : allHouse) {
                if ((houseEntity.getBuilding() + houseEntity.getUnit() + houseEntity.getDoor()).equals(propertyAdvanceDepositEntity.getAddress())) {
                    // 设置对应房屋id
                    propertyAdvanceDepositEntity.setHouseId(houseEntity.getId());
                }
            }
            List<Long> houseIdLists = new ArrayList<>();
            // 查询手机号绑定房屋的id
            List<Long> houseIdList = proprietorService.queryBindHouseByMobile(propertyAdvanceDepositEntity.getMobile(), adminCommunityId);
            for (Long houseId : houseIdList) {
                if (houseId.equals(propertyAdvanceDepositEntity.getHouseId())) {
                    houseIdLists.add(houseId);
                }
            }
            List<PropertyAdvanceDepositEntity> entities = new ArrayList<>();
            // 根据houseId查询预存款余额
            PropertyAdvanceDepositEntity entity1 = propertyAdvanceDepositService.queryAdvanceDepositByHouseId(propertyAdvanceDepositEntity.getHouseId(), adminCommunityId);
            if (entity1 != null) {
                if (entity1.getBalance().add(propertyAdvanceDepositEntity.getReceivedAmount()).compareTo(BigDecimal.ZERO) == -1) {
                    entities.add(entity1);
                }
            }
            if (houseIdLists.size() <= 0) {
                iterator.remove();
                AdvanceDepositImportErrorVO errorVO = new AdvanceDepositImportErrorVO();
                errorVO.setName(propertyAdvanceDepositEntity.getRealName());
                errorVO.setMobile(propertyAdvanceDepositEntity.getMobile());
                errorVO.setHouseAddress(propertyAdvanceDepositEntity.getAddress());
                errorVO.setDoor(propertyAdvanceDepositEntity.getDoor());
                errorVO.setPayAmount(propertyAdvanceDepositEntity.getPayAmount());
                errorVO.setReceivedAmount(propertyAdvanceDepositEntity.getReceivedAmount());
                errorVO.setRemark("该手机号和该房屋地址不是绑定关系!");
                errorVos.add(errorVO);
            } else if (entities.size() > 0) {
                iterator.remove();
                AdvanceDepositImportErrorVO errorVO = new AdvanceDepositImportErrorVO();
                errorVO.setName(propertyAdvanceDepositEntity.getRealName());
                errorVO.setMobile(propertyAdvanceDepositEntity.getMobile());
                errorVO.setHouseAddress(propertyAdvanceDepositEntity.getAddress());
                errorVO.setDoor(propertyAdvanceDepositEntity.getDoor());
                errorVO.setPayAmount(propertyAdvanceDepositEntity.getPayAmount());
                errorVO.setReceivedAmount(propertyAdvanceDepositEntity.getReceivedAmount());
                errorVO.setRemark("预存款余额不足!");
                errorVos.add(errorVO);
            }
        }
        Integer row = 0;
        if (CollectionUtil.isNotEmpty(propertyAdvanceDepositEntities)) {
            //获取管理员姓名 用于标识每条业主数据的创建人
            row = propertyAdvanceDepositService.saveAdvanceDeposit(propertyAdvanceDepositEntities, adminCommunityId, userId);
        }
        //excel导入失败的信息明细 文件下载地址
        String errorExcelAddr = null;
        //错误excel写入远程服务器 让物业人员可以直接下载
        if( CollectionUtil.isNotEmpty(errorVos) ){
            errorExcelAddr = uploadAdvanceDepositErrorExcel(errorVos);
        }
        //构造返回对象
        return CommonResult.ok(new BuildingImportErrorVO(row, errorVos.size(), errorExcelAddr));
    }
    
    /**
     *@Author: DKS
     *@Description: 写入充值余额导入错误信息 和 把错误信息excel文件上传至文件服务器
     *@Param: errorVos:
     *@Return: java.lang.String:  返回excel文件下载地址
     *@Date: 2021/8/16 13:50
     **/
    public String uploadAdvanceDepositErrorExcel(List<AdvanceDepositImportErrorVO> errorVos) {
        Workbook workbook = advanceDepositExcelHandler.exportAdvanceDepositErrorExcel(errorVos);
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
     * excel 文件上传上来后 验证方法
     * @param file        excel文件
     */
    private void validFileSuffix(MultipartFile file) {
        //参数非空验证
        if (null == file) {
            throw new JSYException(JSYError.BAD_REQUEST);
        }
        //文件后缀验证
        boolean extension = FilenameUtils.isExtension(file.getOriginalFilename(), ExcelUtil.SUPPORT_EXCEL_EXTENSION);
        if (!extension) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "只支持excel文件!");
        }
    }
}
