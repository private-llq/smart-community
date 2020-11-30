package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IHouseService;
import com.jsy.community.api.IProprietorService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.ProprietorQO;
import com.jsy.community.util.ProprietorExcelCommander;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.ProprietorVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author YuLF
 * @since 2020-11-25 13:45
 */
@Api(tags = "业主信息控制器")
@RestController
@RequestMapping("/proprietor")
@ApiJSYController
@Slf4j
public class ProprietorController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IProprietorService iProprietorService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IHouseService iHouseService;

    /**
     * http://localhost:7001/api/v1/property/proprietor/downloadExcel?communityId=1
     * 下载录入业主信息excel、模板
     * @return          返回Excel模板
     */
    @GetMapping("/downloadExcel")
    @ApiOperation("下载业主信息录入Excel")
    public ResponseEntity<byte[]> downloadExcel(@RequestParam long communityId) {
        //1.设置响应格式  设置响应头
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        //1.1设置响应类型为附件类型直接下载这种
        multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode(ProprietorExcelCommander.PROPROETOR_REGISTER_EXCEL + ".xlsx", StandardCharsets.UTF_8));
        //1.2设置响应的文件mime类型为 xls类型
        multiValueMap.set("Content-type", "application/vnd.ms-excel");
        //2.生成Excel模板
        try {
            //2.1 查出数据库当前社区的所有楼栋、单元、楼层、门牌 用于excel模板录入业主信息选择
            List<HouseEntity> communityArchitecture = iHouseService.getCommunityArchitecture(communityId);
            //2.2 生成Excel 业主信息录入模板
            Workbook workbook = ProprietorExcelCommander.exportProprietorExcel(communityArchitecture);
            //2.3 把workbook转换为字节输入流
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            @Cleanup InputStream is = new ByteArrayInputStream(bos.toByteArray());
            byte[] byt = new byte[is.available()];
            //2.4 读取字节流 响应实体返回
            is.read(byt);
            return new ResponseEntity<>(byt , multiValueMap, HttpStatus.OK );
        } catch (IOException e) {
            //已接受。已经接受请求，但未处理完成
            log.error("com.jsy.community.controller.ProprietorController.downloadExcel：{}", e.getMessage());
            return new ResponseEntity<>(null , multiValueMap, HttpStatus.ACCEPTED );
        }
    }

    /**
     * 业主登记excel 导入 登记
     * @param proprietorExcel   用户上传的excel
     * @param communityId       社区id
     * @return                  返回效验或登记结果
     */
    @PostMapping("/importProprietorExcel")
    @ApiOperation("导入业主信息excel")
    public CommonResult<?> importProprietorExcel(MultipartFile proprietorExcel, Long communityId){
        //参数非空验证
        if(null == proprietorExcel || communityId == null){
            return CommonResult.error(JSYError.BAD_REQUEST);
        }
        //文件后缀验证
        boolean extension = FilenameUtils.isExtension(proprietorExcel.getOriginalFilename(), ProprietorExcelCommander.SUPPORT_EXCEL_EXTENSION);
        if (!extension) {
            return CommonResult.error(JSYError.REQUEST_PARAM.getCode(), "只支持excel文件!" );
        }
        //解析Excel
        List<UserEntity> userEntityList = ProprietorExcelCommander.importProprietorExcel(proprietorExcel);
        //做数据库写入 userEntityList 读出来的数据
        //todo 数据库信息写入
        return CommonResult.ok(userEntityList);
    }

    /**
     * TODO  Seata全局事务处理
     * 根据业主id 删除业主信息、业主关联的房屋、业主的家庭成员、业主的车辆信息
     * @return          返回删除是否成功
     */
    @DeleteMapping()
    @ApiOperation("删除业主信息")
    public CommonResult<Boolean> del(@RequestParam Long uid){
        //从JWT获取业主ID
        //Long uid = 12L;
        iProprietorService.del(uid);
        return CommonResult.ok();
    }

    /**
     * 分页查询业主信息
     * @param proprietorQOBaseQO   查询参数实体
     * @return                    返回删除是否成功
     */
    @PostMapping()
    @ApiOperation("分页查询业主信息")
    public CommonResult<List<ProprietorVO>> query(@RequestBody BaseQO<ProprietorQO> proprietorQOBaseQO){
        //1.验证分页 查询参数
        ValidatorUtils.validatePageParam(proprietorQOBaseQO);
        //2.查询信息返回
        return CommonResult.ok(iProprietorService.query(proprietorQOBaseQO));
    }

    /**
     * 修改业主信息
     * @param proprietorQO        参数实体
     * @return                    返回删除是否成功
     */
    @PutMapping()
    @ApiOperation("修改业主信息")
    public CommonResult<Boolean> update(@RequestBody ProprietorQO proprietorQO){
        //效验id
        ValidatorUtils.validateEntity(proprietorQO, ProprietorQO.propertyUpdateValid.class);
        return iProprietorService.update(proprietorQO) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }


}
