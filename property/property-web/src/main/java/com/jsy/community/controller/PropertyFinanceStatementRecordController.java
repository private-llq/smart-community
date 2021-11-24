package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IPropertyFinanceStatementRecordService;
import com.jsy.community.constant.Const;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Pipi
 * @Description: 物业财务-账单结算操作记录控制器
 * @Date: 2021/4/24 10:36
 * @Version: 1.0
 **/
@RestController
@Api("物业财务-结算单操作记录控制器")
@RequestMapping("/statementRecord")
@ApiJSYController
public class PropertyFinanceStatementRecordController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceStatementRecordService statementRecordService;

    /**
     *@Author: Pipi
     *@Description: 物业结算-结算进程
     *@Param: statementNum:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/4/24 10:54
     **/
    @ApiOperation("物业结算-结算进程")
    @GetMapping("/getStatementRecord")
    @Permit("community:property:statementRecord:getStatementRecord")
    public CommonResult getStatementRecord(@RequestParam String statementNum) {
        return CommonResult.ok(statementRecordService.statementRecordList(statementNum));
    }
}
