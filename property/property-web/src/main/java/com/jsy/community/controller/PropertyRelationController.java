package com.jsy.community.controller;

import cn.hutool.core.collection.CollectionUtil;
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
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.entity.UserDetail;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import com.zhsj.base.api.vo.PageVO;
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
import org.springframework.util.CollectionUtils;
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
import java.util.stream.Collectors;

/**
 * @program: com.jsy.community
 * @description: ????????????????????????
 * @author: Hu
 * @create: 2021-03-05 11:18
 **/
@Api(tags = "??????????????????")
@RestController
@RequestMapping("/members")
// @ApiJSYController
public class PropertyRelationController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyRelationService propertyRelationService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IHouseInfoService houseInfoService;
    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check=false)
    private IBaseUserInfoRpcService baseUserInfoRpcService;

    @Autowired
    private MembersHandler membersHandler;

    @ApiOperation("????????????")
    @PostMapping("/pageList")
    @Permit("community:property:members:pageList")
    public CommonResult pageList(@RequestBody BaseQO<HouseMemberQO> baseQO){
        return CommonResult.ok(propertyRelationService.pageList(baseQO));
    }

    @ApiOperation("??????????????????????????????")
    @PostMapping("/downloadRelationExcelTemplate")
    @Permit("community:property:members:downloadRelationExcelTemplate")
    public ResponseEntity<byte[]> downloadHouseExcelTemplate() {
        //??????excel ???????????????
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        //???????????????????????????????????????????????????
        multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("????????????.xlsx", StandardCharsets.UTF_8));
        //?????????????????????mime????????? xls??????
        multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
        Workbook workbook = membersHandler.exportRelationTemplate();
        //???workbook?????????????????????????????? ???????????????????????????????????????
        try {
            return new ResponseEntity<>(ExcelUtil.readWorkbook(workbook), multiValueMap, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
        }
    }
    
    @ApiOperation("??????")
    @PostMapping("/export")
    @Permit("community:property:members:export")
    public ResponseEntity<byte[]> export(@RequestBody HouseMemberQO houseMemberQO){
        houseMemberQO.setCommunityId(UserUtils.getAdminCommunityId());
        PageVO<UserDetail> pageVO = baseUserInfoRpcService.queryUser("", "", 0, 999999999);
        Map<String, String> nickNameMap = pageVO.getData().stream().collect(Collectors.toMap(UserDetail::getAccount, UserDetail::getNickName));
        List<HouseMemberVO> houseMemberVOS = propertyRelationService.queryExportRelationExcel(houseMemberQO);
        houseMemberVOS.stream().peek(h -> {
            // ??????????????????
            if (!CollectionUtils.isEmpty(nickNameMap)) {
                h.setAppName(nickNameMap.get(h.getUid()));
            }
        }).collect(Collectors.toList());
        
        //??????excel ???????????????
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        //???????????????????????????????????????????????????
        multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("???????????????.xlsx", StandardCharsets.UTF_8));
        //?????????????????????mime????????? xls??????
        multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
        Workbook workbook = new XSSFWorkbook();
        workbook = membersHandler.exportRelation(houseMemberVOS);
        //???workbook?????????????????????????????? ???????????????????????????????????????
        try {
            return new ResponseEntity<>(ExcelUtil.readWorkbook(workbook), multiValueMap, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
        }
    }
    
    @ApiOperation("??????")
    @PostMapping("/import")
    @Permit("community:property:members:import")
    public CommonResult importRelation(@RequestBody MultipartFile excel){
        String originalFilename = excel.getOriginalFilename();
        String s = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (!Arrays.asList(ExcelUtil.SUPPORT_EXCEL_EXTENSION).contains(s)) {
            return CommonResult.error("???????????????????????????"+ Arrays.toString(ExcelUtil.SUPPORT_EXCEL_EXTENSION));
        }
        List<RelationImportErrVO> errorVos = new LinkedList<>();
        List<RelationImportQO> list = membersHandler.importRelation(excel,errorVos);
        //???????????????????????????????????????
        List<RelationImportErrVO> errVOList = propertyRelationService.importRelation(list,UserUtils.getAdminCommunityId(),UserUtils.getUserId());
        for (RelationImportErrVO errVO : errVOList) {
            errorVos.add(errVO);
        }
        //excel??????????????????????????? ??????????????????
        String errorExcelAddr = null;
        //??????excel????????????????????? ?????????????????????????????????
        if( CollectionUtil.isNotEmpty(errorVos) ){
            errorExcelAddr=uploadErrorExcel(errorVos);
        }

        //??????????????????
        return CommonResult.ok(new HouseImportErrorVO(list.size()-errVOList.size(), errorVos.size(), errorExcelAddr));
    }

    /**
     *@Author: Pipi
     *@Description: ???????????????????????????????????? ??? ???????????????excel??????????????????????????????
     *@Param: errorVos:
     *@Return: java.lang.String:  ??????excel??????????????????
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

    @ApiOperation("??????")
    @GetMapping("/immigration")
    @Permit("community:property:members:immigration")
    public CommonResult immigration(@RequestParam Long id){
        propertyRelationService.immigration(id);
        return CommonResult.ok();
    }

    @ApiOperation("??????")
    @GetMapping("/emigration")
    @Permit("community:property:members:emigration")
    public CommonResult emigration(@RequestParam Long id){
        propertyRelationService.emigration(id);
        return CommonResult.ok();
    }
    
    @LoginIgnore
    @ApiOperation("????????????????????????")
    @GetMapping("/getByPushInfo")
    public CommonResult getByPushInfo(@RequestParam Long id){
        return CommonResult.ok(houseInfoService.getByPushInfo(id));
    }

    @LoginIgnore
    @ApiOperation("????????????????????????")
    @PostMapping("/relationSave")
    public CommonResult relationSave(@RequestBody HouseInfoEntity houseInfoEntity){
        houseInfoService.relationSave(houseInfoEntity);
        return CommonResult.ok();
    }

    @ApiOperation("????????????")
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

    @ApiOperation("??????")
    @PostMapping("/save")
    @businessLog(operation = "??????",content = "???????????????????????????")
    @Permit("community:property:members:save")
    public CommonResult save(@RequestBody HouseMemberEntity houseMemberEntity){
        ValidatorUtils.validateEntity(houseMemberEntity,HouseMemberEntity.SaveVerification.class);
        propertyRelationService.save(houseMemberEntity,UserUtils.getAdminInfo().getUid());
        return CommonResult.ok();
    }
    
    @ApiOperation("??????")
    @PutMapping("/update")
    @businessLog(operation = "??????",content = "???????????????????????????")
    @Permit("community:property:members:update")
    public CommonResult update(@RequestBody HouseMemberEntity houseMemberEntity){
        ValidatorUtils.validateEntity(houseMemberEntity,HouseMemberEntity.SaveVerification.class);
        propertyRelationService.update(houseMemberEntity);
        return CommonResult.ok();
    }
    
    @ApiOperation("????????????")
    @DeleteMapping("/deletes")
    @businessLog(operation = "??????",content = "?????????????????????????????????")
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
    
    @ApiOperation("??????")
    @DeleteMapping("/delete")
    @businessLog(operation = "??????",content = "???????????????????????????")
    @Permit("community:property:members:delete")
    public CommonResult delete(@RequestParam Long id){
        propertyRelationService.delete(id);
        return CommonResult.ok();
    }

    @ApiOperation("????????????")
    @GetMapping("/findOne")
    @Permit("community:property:members:findOne")
    public CommonResult findOne(@RequestParam Long id){
        HouseMemberEntity entity = propertyRelationService.findOne(id);
        return CommonResult.ok(entity);
    }

    @ApiOperation("???????????????")
    @PostMapping("/enterPicture")
    @Permit("community:property:members:enterPicture")
    public CommonResult enterPicture(@RequestParam("file") MultipartFile file) {
        if(PicUtil.checkSizeAndType(file,2048)){
            String upload = MinioUtils.upload(file, "enterimge");
            return  CommonResult.ok(upload,"????????????");
        }
        return  CommonResult.ok("???????????????");
    }
    
    @ApiOperation("??????????????????")
    @PostMapping("/idCardImage")
    @Permit("community:property:members:idCardImage")
    public CommonResult idCardImage(@RequestParam("file") MultipartFile file) {
        if(PicUtil.checkSizeAndType(file,2048)){
            String upload = MinioUtils.upload(file, "idcardimage");
            return  CommonResult.ok(upload,"????????????");
        }
        return  CommonResult.ok("???????????????");
    }
    
    @ApiOperation("??????????????????")
    @PostMapping("/list")
    @Desensitization(type = {DesensitizationType.PHONE,DesensitizationType.ID_CARD,DesensitizationType.PHONE,DesensitizationType.ID_CARD}, field = {"mobile","idCard","ownerMobile","ownerIdCard"})
    @Permit("community:property:members:list")
    public CommonResult list(@RequestBody BaseQO<PropertyRelationQO> baseQO){
        System.out.println(baseQO);
        Map map=propertyRelationService.list(baseQO,UserUtils.getAdminInfo().getCommunityId());
        return CommonResult.ok(map);
    }
    
    @ApiOperation("???????????????")
    @PostMapping("/getHouseId")
    @Permit("community:property:members:getHouseId")
    public CommonResult getHouseId(@RequestBody BaseQO<RelationListQO> baseQO){
        AdminInfoVo adminInfoVo = UserUtils.getAdminInfo();
        List list =propertyRelationService.getHouseId(baseQO,adminInfoVo);
        return CommonResult.ok(list);
    }
    
    @ApiOperation("???????????????")
    @PostMapping("/getBuildingId")
    @Permit("community:property:members:getBuildingId")
    public CommonResult getBuildingId(@RequestBody BaseQO<RelationListQO> baseQO){
        AdminInfoVo adminInfoVo = UserUtils.getAdminInfo();
        List list =propertyRelationService.getBuildingId(baseQO,adminInfoVo);
        return CommonResult.ok(list);
    }
    
    @ApiOperation("???????????????")
    @PostMapping("/getUnitId")
    @Permit("community:property:members:getUnitId")
    public CommonResult getUnitId(@RequestBody BaseQO<RelationListQO> baseQO){
        AdminInfoVo adminInfoVo = UserUtils.getAdminInfo();
        List list =propertyRelationService.getUnitId(baseQO,adminInfoVo);
        return CommonResult.ok(list);
    }
}
