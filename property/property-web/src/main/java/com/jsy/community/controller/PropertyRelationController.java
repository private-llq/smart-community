package com.jsy.community.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.Desensitization;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.IHouseInfoService;
import com.jsy.community.api.IPropertyRelationService;
import com.jsy.community.aspectj.DesensitizationType;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseInfoEntity;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.HouseMemberQO;
import com.jsy.community.qo.property.PropertyRelationQO;
import com.jsy.community.qo.property.RelationListQO;
import com.jsy.community.util.MembersHandler;
import com.jsy.community.utils.*;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.jsy.community.vo.property.HouseImportErrorVO;
import com.jsy.community.vo.property.HouseMemberVO;
import com.jsy.community.vo.property.RelationImportErrVO;
import com.jsy.community.vo.property.RelationImportQO;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
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
// @ApiJSYController
public class PropertyRelationController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyRelationService propertyRelationService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IHouseInfoService houseInfoService;

    @Autowired
    private MembersHandler membersHandler;

    @ApiOperation("分页查询")
    @PostMapping("/pageList")
    @Permit("community:property:members:pageList")
    public CommonResult pageList(@RequestBody BaseQO<HouseMemberQO> baseQO){
    return CommonResult.ok(propertyRelationService.pageList(baseQO));
    }

    @ApiOperation("下载成员信息导入模板")
    @PostMapping("/downloadRelationExcelTemplate")
    @Permit("community:property:members:downloadRelationExcelTemplate")
    public ResponseEntity<byte[]> downloadHouseExcelTemplate() {
        //设置excel 响应头信息
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        //设置响应类型为附件类型直接下载这种
        multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("房屋信息.xlsx", StandardCharsets.UTF_8));
        //设置响应的文件mime类型为 xls类型
        multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
        Workbook workbook = membersHandler.exportRelationTemplate();
        //把workbook工作簿转换为字节数组 放入响应实体以附件形式输出
        try {
            return new ResponseEntity<>(ExcelUtil.readWorkbook(workbook), multiValueMap, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
        }
    }
    
    @ApiOperation("导出")
    @PostMapping("/export")
    @Permit("community:property:members:export")
    public ResponseEntity<byte[]> export(@RequestBody HouseMemberQO houseMemberQO){
        houseMemberQO.setCommunityId(UserUtils.getAdminCommunityId());
        List<HouseMemberVO> houseMemberVOS = propertyRelationService.queryExportRelationExcel(houseMemberQO);
        //设置excel 响应头信息
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        //设置响应类型为附件类型直接下载这种
        multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("成员信息表.xlsx", StandardCharsets.UTF_8));
        //设置响应的文件mime类型为 xls类型
        multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
        Workbook workbook = new XSSFWorkbook();
        workbook = membersHandler.exportRelation(houseMemberVOS);
        //把workbook工作簿转换为字节数组 放入响应实体以附件形式输出
        try {
            return new ResponseEntity<>(ExcelUtil.readWorkbook(workbook), multiValueMap, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
        }
    }
    
    @ApiOperation("导入")
    @PostMapping("/import")
    @Permit("community:property:members:import")
    public CommonResult importRelation(@RequestBody MultipartFile excel){
        String originalFilename = excel.getOriginalFilename();
        String s = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (!Arrays.asList(ExcelUtil.SUPPORT_EXCEL_EXTENSION).contains(s)) {
            return CommonResult.error("错误文件！可用后缀"+ Arrays.toString(ExcelUtil.SUPPORT_EXCEL_EXTENSION));
        }
        List<RelationImportErrVO> errorVos = new LinkedList<>();
        List<RelationImportQO> list = membersHandler.importRelation(excel,errorVos);
        //导入数据库返回其中错误信息
        List<RelationImportErrVO> errVOList = propertyRelationService.importRelation(list,UserUtils.getAdminCommunityId(),UserUtils.getUserId());
        for (RelationImportErrVO errVO : errVOList) {
            errorVos.add(errVO);
        }
        //excel导入失败的信息明细 文件下载地址
        String errorExcelAddr = null;
        //错误excel写入远程服务器 让物业人员可以直接下载
        if( CollectionUtil.isNotEmpty(errorVos) ){
            errorExcelAddr=uploadErrorExcel(errorVos);
        }

        //构造返回对象
        return CommonResult.ok(new HouseImportErrorVO(list.size()-errVOList.size(), errorVos.size(), errorExcelAddr));
    }

    /**
     *@Author: Pipi
     *@Description: 写入房屋信息导入错误信息 和 把错误信息excel文件上传至文件服务器
     *@Param: errorVos:
     *@Return: java.lang.String:  返回excel文件下载地址
     *@Date: 2021/5/21 17:38
     **/
    public String uploadErrorExcel(List<RelationImportErrVO> errorVos) {
        Workbook workbook = membersHandler.exportErrorExcel(errorVos);
        try {
            byte[] bytes = ExcelUtil.readWorkbook(workbook);
            MultipartFile multipartFile = new MockMultipartFile("file", "relationErrorExcel", "application/vnd.ms-excel", bytes);
            return MinioUtils.upload(multipartFile, "relation-error-excel");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @ApiOperation("迁入")
    @GetMapping("/immigration")
    @Permit("community:property:members:immigration")
    public CommonResult immigration(@RequestParam Long id){
        propertyRelationService.immigration(id);
        return CommonResult.ok();
    }

    @ApiOperation("迁出")
    @GetMapping("/emigration")
    @Permit("community:property:members:emigration")
    public CommonResult emigration(@RequestParam Long id){
        propertyRelationService.emigration(id);
        return CommonResult.ok();
    }
    
    @LoginIgnore
    @ApiOperation("查询推送消息类容")
    @GetMapping("/getByPushInfo")
    public CommonResult getByPushInfo(@RequestParam Long id){
        return CommonResult.ok(houseInfoService.getByPushInfo(id));
    }

    @LoginIgnore
    @ApiOperation("用户确定入驻房间")
    @PostMapping("/relationSave")
    public CommonResult relationSave(@RequestBody HouseInfoEntity houseInfoEntity){
        houseInfoService.relationSave(houseInfoEntity);
        return CommonResult.ok();
    }

    @ApiOperation("批量迁出")
    @GetMapping("/emigrations")
    @Permit("community:property:members:emigrations")
    public CommonResult emigrations(@RequestParam String ids){
        String[] split = ids.split(",");
        Long[] longAry= new Long[split.length];
        for(int i = 0, len = split.length; i < len; i++){
            longAry[i] = Long.parseLong(split[i]);
        }
        propertyRelationService.emigrations(longAry);
        return CommonResult.ok();
    }

    @ApiOperation("新增")
    @PostMapping("/save")
    @businessLog(operation = "新增",content = "新增了【物业家属】")
    @Permit("community:property:members:save")
    public CommonResult save(@RequestBody HouseMemberEntity houseMemberEntity){
        ValidatorUtils.validateEntity(houseMemberEntity,HouseMemberEntity.SaveVerification.class);
        propertyRelationService.save(houseMemberEntity,UserUtils.getAdminUserInfo().getUid());
        return CommonResult.ok();
    }
    
    @ApiOperation("修改")
    @PutMapping("/update")
    @businessLog(operation = "编辑",content = "更新了【物业家属】")
    @Permit("community:property:members:update")
    public CommonResult update(@RequestBody HouseMemberEntity houseMemberEntity){
        ValidatorUtils.validateEntity(houseMemberEntity,HouseMemberEntity.SaveVerification.class);
        propertyRelationService.update(houseMemberEntity);
        return CommonResult.ok();
    }
    
    @ApiOperation("批量删除")
    @DeleteMapping("/deletes")
    @businessLog(operation = "删除",content = "删除了【批量物业家属】")
    @Permit("community:property:members:deletes")
    public CommonResult deletes(@RequestParam String ids){
        String[] split = ids.split(",");
        Long[] longAry= new Long[split.length];
        for(int i = 0, len = split.length; i < len; i++){
            longAry[i] = Long.parseLong(split[i]);
        }
        propertyRelationService.deletes(longAry);
        return CommonResult.ok();
    }
    
    @ApiOperation("删除")
    @DeleteMapping("/delete")
    @businessLog(operation = "删除",content = "删除了【物业家属】")
    @Permit("community:property:members:delete")
    public CommonResult delete(@RequestParam Long id){
        propertyRelationService.delete(id);
        return CommonResult.ok();
    }

    @ApiOperation("查询一条")
    @GetMapping("/findOne")
    @Permit("community:property:members:findOne")
    public CommonResult findOne(@RequestParam Long id){
        HouseMemberEntity entity = propertyRelationService.findOne(id);
        return CommonResult.ok(entity);
    }

    @ApiOperation("新增入住图")
    @PostMapping("/enterPicture")
    @Permit("community:property:members:enterPicture")
    public CommonResult enterPicture(@RequestParam("file") MultipartFile file) {
        if(PicUtil.checkSizeAndType(file,2048)){
            String upload = MinioUtils.upload(file, "enterimge");
            return  CommonResult.ok(upload,"上传成功");
        }
        return  CommonResult.ok("上传失败！");
    }
    
    @ApiOperation("新增身份证图")
    @PostMapping("/idCardImage")
    @Permit("community:property:members:idCardImage")
    public CommonResult idCardImage(@RequestParam("file") MultipartFile file) {
        if(PicUtil.checkSizeAndType(file,2048)){
            String upload = MinioUtils.upload(file, "idcardimage");
            return  CommonResult.ok(upload,"上传成功");
        }
        return  CommonResult.ok("上传失败！");
    }
    
    @ApiOperation("查询成员列表")
    @PostMapping("/list")
    @Desensitization(type = {DesensitizationType.PHONE,DesensitizationType.ID_CARD,DesensitizationType.PHONE,DesensitizationType.ID_CARD}, field = {"mobile","idCard","ownerMobile","ownerIdCard"})
    @Permit("community:property:members:list")
    public CommonResult list(@RequestBody BaseQO<PropertyRelationQO> baseQO){
        System.out.println(baseQO);
        Map map=propertyRelationService.list(baseQO,UserUtils.getAdminUserInfo().getCommunityId());
        return CommonResult.ok(map);
    }
    
    @ApiOperation("房屋下拉框")
    @PostMapping("/getHouseId")
    @Permit("community:property:members:getHouseId")
    public CommonResult getHouseId(@RequestBody BaseQO<RelationListQO> baseQO){
        AdminInfoVo adminInfoVo = UserUtils.getAdminUserInfo();
        List list =propertyRelationService.getHouseId(baseQO,adminInfoVo);
        return CommonResult.ok(list);
    }
    
    @ApiOperation("楼栋下拉框")
    @PostMapping("/getBuildingId")
    @Permit("community:property:members:getBuildingId")
    public CommonResult getBuildingId(@RequestBody BaseQO<RelationListQO> baseQO){
        AdminInfoVo adminInfoVo = UserUtils.getAdminUserInfo();
        List list =propertyRelationService.getBuildingId(baseQO,adminInfoVo);
        return CommonResult.ok(list);
    }
    
    @ApiOperation("单元下拉框")
    @PostMapping("/getUnitId")
    @Permit("community:property:members:getUnitId")
    public CommonResult getUnitId(@RequestBody BaseQO<RelationListQO> baseQO){
        AdminInfoVo adminInfoVo = UserUtils.getAdminUserInfo();
        List list =propertyRelationService.getUnitId(baseQO,adminInfoVo);
        return CommonResult.ok(list);
    }
}
