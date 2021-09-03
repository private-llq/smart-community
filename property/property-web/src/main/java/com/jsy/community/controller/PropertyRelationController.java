package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.Desensitization;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.IPropertyRelationService;
import com.jsy.community.aspectj.DesensitizationType;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.HouseMemberQO;
import com.jsy.community.qo.property.PropertyRelationQO;
import com.jsy.community.qo.property.RelationListQO;
import com.jsy.community.util.MembersHandler;
import com.jsy.community.utils.*;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.jsy.community.vo.property.HouseMemberVO;
import com.jsy.community.vo.property.RelationImportErrVO;
import com.jsy.community.vo.property.RelationImportQO;
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
@ApiJSYController
public class PropertyRelationController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyRelationService propertyRelationService;

    @Autowired
    private MembersHandler membersHandler;

    @ApiOperation("分页查询")
    @PostMapping("/pageList")
    @Login
    public CommonResult pageList(@RequestBody BaseQO<HouseMemberQO> baseQO){
    return CommonResult.ok(propertyRelationService.pageList(baseQO));
    }

    @ApiOperation("导出")
    @PostMapping("/export")
    @Login
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
    @Login
    public CommonResult importRelation(@RequestBody MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        String s = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (!Arrays.asList(ExcelUtil.SUPPORT_EXCEL_EXTENSION).contains(s)) {
            return CommonResult.error("错误文件！可用后缀"+ Arrays.toString(ExcelUtil.SUPPORT_EXCEL_EXTENSION));
        }
        List<RelationImportErrVO> errorVos = new LinkedList<>();
        List<RelationImportQO> list = membersHandler.importRelation(file,errorVos);


        return CommonResult.ok();
    }

    @ApiOperation("迁入")
    @GetMapping("/immigration")
    @Login
    public CommonResult immigration(@RequestParam Long id){
        propertyRelationService.immigration(id);
        return CommonResult.ok();
    }

    @ApiOperation("迁出")
    @GetMapping("/emigration")
    @Login
    public CommonResult emigration(@RequestParam Long id){
        propertyRelationService.emigration(id);
        return CommonResult.ok();
    }

    @ApiOperation("批量迁出")
    @GetMapping("/emigrations")
    @Login
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
    @Login
    @businessLog(operation = "新增",content = "新增了【物业家属】")
    public CommonResult save(@RequestBody HouseMemberEntity houseMemberEntity){
        ValidatorUtils.validateEntity(houseMemberEntity,HouseMemberEntity.SaveVerification.class);
        propertyRelationService.save(houseMemberEntity,UserUtils.getAdminUserInfo().getUid());
        return CommonResult.ok();
    }
    @ApiOperation("修改")
    @PutMapping("/update")
    @Login
    @businessLog(operation = "编辑",content = "更新了【物业家属】")
    public CommonResult update(@RequestBody HouseMemberEntity houseMemberEntity){
        ValidatorUtils.validateEntity(houseMemberEntity,HouseMemberEntity.SaveVerification.class);
        propertyRelationService.update(houseMemberEntity);
        return CommonResult.ok();
    }
    @ApiOperation("批量删除")
    @DeleteMapping("/deletes")
    @Login
    @businessLog(operation = "删除",content = "删除了【批量物业家属】")
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
    @Login
    @businessLog(operation = "删除",content = "删除了【物业家属】")
    public CommonResult delete(@RequestParam Long id){
        propertyRelationService.delete(id);
        return CommonResult.ok();
    }

    @ApiOperation("查询一条")
    @GetMapping("/findOne")
    @Login
    public CommonResult findOne(@RequestParam Long id){
        HouseMemberEntity entity = propertyRelationService.findOne(id);
        return CommonResult.ok(entity);
    }

    @ApiOperation("新增入住图")
    @PostMapping("/enterPicture")
    @Login
    public CommonResult enterPicture(@RequestParam("file") MultipartFile file) {
        if(PicUtil.checkSizeAndType(file,2048)){
            String upload = MinioUtils.upload(file, "enterimge");
            return  CommonResult.ok(upload,"上传成功");
        }
        return  CommonResult.ok("上传失败！");
    }
    @ApiOperation("新增身份证图")
    @PostMapping("/idCardImage")
    @Login
    public CommonResult idCardImage(@RequestParam("file") MultipartFile file) {
        if(PicUtil.checkSizeAndType(file,2048)){
            String upload = MinioUtils.upload(file, "idcardimage");
            return  CommonResult.ok(upload,"上传成功");
        }
        return  CommonResult.ok("上传失败！");
    }
    @ApiOperation("查询成员列表")
    @PostMapping("/list")
    @Login
    @Desensitization(type = {DesensitizationType.PHONE,DesensitizationType.ID_CARD,DesensitizationType.PHONE,DesensitizationType.ID_CARD}, field = {"mobile","idCard","ownerMobile","ownerIdCard"})
    public CommonResult list(@RequestBody BaseQO<PropertyRelationQO> baseQO){
        System.out.println(baseQO);
        Map map=propertyRelationService.list(baseQO,UserUtils.getAdminUserInfo().getCommunityId());
        return CommonResult.ok(map);
    }
    @ApiOperation("房屋下拉框")
    @PostMapping("/getHouseId")
    @Login
    public CommonResult getHouseId(@RequestBody BaseQO<RelationListQO> baseQO){
        AdminInfoVo adminInfoVo = UserUtils.getAdminUserInfo();
        List list =propertyRelationService.getHouseId(baseQO,adminInfoVo);
        return CommonResult.ok(list);
    }
    @ApiOperation("楼栋下拉框")
    @PostMapping("/getBuildingId")
    @Login
    public CommonResult getBuildingId(@RequestBody BaseQO<RelationListQO> baseQO){
        AdminInfoVo adminInfoVo = UserUtils.getAdminUserInfo();
        List list =propertyRelationService.getBuildingId(baseQO,adminInfoVo);
        return CommonResult.ok(list);
    }
    @ApiOperation("单元下拉框")
    @PostMapping("/getUnitId")
    @Login
    public CommonResult getUnitId(@RequestBody BaseQO<RelationListQO> baseQO){
        AdminInfoVo adminInfoVo = UserUtils.getAdminUserInfo();
        List list =propertyRelationService.getUnitId(baseQO,adminInfoVo);
        return CommonResult.ok(list);
    }
}
