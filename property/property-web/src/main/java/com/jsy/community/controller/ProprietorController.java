package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.utils.FileUploadUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

    /**
     * 社区信息表常量 后期使用时放入Spring配置文件
     */
    private static final String PROPROETOR_REGISTER_EXCEL = "社区业主信息录入表";


    /**
     * 下载录入业主信息excel、模板
     * @return          返回Excel模板
     */
    @GetMapping("/downloadExcel")
    public ResponseEntity<byte[]> downloadExcel() {
        //设置响应头
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        //设置响应类型为附件类型直接下载这种
        multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode(PROPROETOR_REGISTER_EXCEL + ".xlsx", StandardCharsets.UTF_8));
        //设置响应的文件mime类型为 xls类型
        multiValueMap.set("Content-type", "application/vnd.ms-excel");
        //读取Excel模板
        try {
            String resourceFilePath = FileUploadUtils.getResourceFilePath("template"+ File.separator +PROPROETOR_REGISTER_EXCEL);
            //在当前resource/template下面没有这个excel模板
            if(resourceFilePath == null){
                return new ResponseEntity<>(null , multiValueMap, HttpStatus.NOT_FOUND );
            }
            @Cleanup InputStream fileInputStream = new FileInputStream(resourceFilePath);
            byte[] byt = new byte[fileInputStream.available()];
            fileInputStream.read(byt);
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
