package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IPropertyFinanceStatementService;
import com.jsy.community.constant.Const;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.StatementQO;
import com.jsy.community.util.excel.impl.FinanceExcelImpl;
import com.jsy.community.utils.ExcelUtil;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.StatementVO;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 物业财务-结算单控制器
 * @Date: 2021/4/23 16:24
 * @Version: 1.0
 **/
@RestController
@Api("物业财务-结算单控制器")
@RequestMapping("/statement")
// @ApiJSYController
public class PropertyFinanceStatementController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceStatementService statementService;

    @Autowired
    private FinanceExcelImpl financeExcel;

    /**
     *@Author: Pipi
     *@Description: 物业财务-结算单列表
     *@Param: statementQO:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/4/23 16:40
     **/
    @ApiOperation("物业财务-结算单列表")
    @PostMapping("/statementList")
    @Permit("community:property:statement:statementList")
    public CommonResult statementList(@RequestBody BaseQO<StatementQO> statementQO) {
        statementQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
        ValidatorUtils.validatePageParam(statementQO);
        if (statementQO.getQuery() == null) {
            return CommonResult.error(JSYError.BAD_REQUEST);
        }
        return CommonResult.ok(statementService.getStatementList(statementQO));
    }

    /**
     *@Author: Pipi
     *@Description: 物业财务-导出结算单
     *@Param: statementQO:
     *@Return: org.springframework.http.ResponseEntity<byte[]>
     *@Date: 2021/4/24 15:21
     **/
    @ApiOperation("物业财务-导出结算单")
    @PostMapping("/downloadStatementList")
    @Permit("community:property:statement:downloadStatementList")
    public ResponseEntity<byte[]> downloadStatementList(@RequestBody StatementQO statementQO) {
        ValidatorUtils.validateEntity(statementQO, StatementQO.ExportValiadate.class);
        statementQO.setCommunityId(UserUtils.getAdminCommunityId());
        List<StatementVO> downloadStatementList = statementService.getDownloadStatementList(statementQO);
        //设置excel 响应头信息
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        //设置响应类型为附件类型直接下载这种
        multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode("结算单表.xlsx", StandardCharsets.UTF_8));
        //设置响应的文件mime类型为 xls类型
        multiValueMap.set("Content-type", "application/vnd.ms-excel;charset=utf-8");
        Workbook workbook = new XSSFWorkbook();
        if (statementQO.getExportType() == 1) {
            workbook = financeExcel.exportMaterStatement(downloadStatementList);
        } else {
            workbook = financeExcel.exportMasterSlaveStatement(downloadStatementList);
        }
        //把workbook工作簿转换为字节数组 放入响应实体以附件形式输出
        try {
            return new ResponseEntity<>(ExcelUtil.readWorkbook(workbook), multiValueMap, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
        }
    }
}
