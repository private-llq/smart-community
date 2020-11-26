package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IHouseService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.util.ProprietorExcelFactory;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
    private IHouseService iHouseService;
    
    /**
     * 社区信息表常量 后期使用时放入Spring配置文件
     */
    private static final String PROPROETOR_REGISTER_EXCEL = "社区业主信息录入表";


    /**
     * 下载录入业主信息excel、模板
     * @return          返回Excel模板
     */
    @GetMapping("/downloadExcel")
    public ResponseEntity<byte[]> downloadExcel(@RequestParam long communityId) {
        //1.设置响应格式  设置响应头
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        //1.1设置响应类型为附件类型直接下载这种
        multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode(PROPROETOR_REGISTER_EXCEL + ".xlsx", StandardCharsets.UTF_8));
        //1.2设置响应的文件mime类型为 xls类型
        multiValueMap.set("Content-type", "application/vnd.ms-excel");
        //2.生成Excel模板
        try {
            //2.1 查出数据库当前社区的所有楼栋、单元、楼层、门牌 用于excel模板录入业主信息选择
            List<HouseEntity> communityArchitecture = iHouseService.getCommunityArchitecture(communityId);
            //2.2 生成Excel 业主信息录入模板
            Workbook workbook = ProprietorExcelFactory.generateExcel(communityArchitecture, PROPROETOR_REGISTER_EXCEL);
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
    public CommonResult<Boolean> importProprietorExcel(MultipartFile proprietorExcel, Long communityId){
        //参数非空验证
        if(null == proprietorExcel || communityId == null){
            return CommonResult.error(JSYError.BAD_REQUEST);
        }
        //文件后缀验证
        boolean extension = FilenameUtils.isExtension(proprietorExcel.getOriginalFilename(), "xls,xlsx");
        if (!extension) {
            return CommonResult.error(JSYError.REQUEST_PARAM.getCode(), "只支持excel文件!" );
        }
        //读取excel文件

        //WorkbookFactory.create();
        return null;
    }

    public void readProprietorExcel(InputStream inputStream){
        try {
            //把文件流转换为工作簿
            Workbook workbook = WorkbookFactory.create(inputStream);
            //从工作簿中读取工作表
            Sheet sheetAt = workbook.getSheetAt(0);
            //如果只是一个空excel文件
            if(sheetAt == null){
                throw new JSYException(JSYError.BAD_REQUEST.getCode(), "excel文件信息无效!");
            }
        } catch (IOException e) {
            log.error("com.jsy.community.controller.ProprietorController.readProprietorExcel：{}", e.getMessage());
            throw new JSYException(JSYError.NOT_IMPLEMENTED.getCode(), e.getMessage());
        }

    }

}
