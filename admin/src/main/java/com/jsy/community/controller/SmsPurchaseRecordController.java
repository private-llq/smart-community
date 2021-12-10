package com.jsy.community.controller;

import com.jsy.community.entity.SmsPurchaseRecordEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.sys.SmsPurchaseRecordQO;
import com.jsy.community.service.ISmsPurchaseRecordService;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 短信购买记录
 * @author: DKS
 * @create: 2021-12-09 15:30
 **/
@Api(tags = "短信购买记录")
@RestController
@RequestMapping("/sms/purchase")
public class SmsPurchaseRecordController {
    
    @Resource
    private ISmsPurchaseRecordService smsPurchaseRecordService;
    
    /**
     * @Description: 查询短信购买记录
     * @Param: [smsPurchaseRecordQO]
     * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.SmsPurchaseRecordEntity>>
     * @Author: DKS
     * @Date: 2021/12/9
     **/
    @PostMapping("/query")
    @Permit("community:admin:sms:purchase:query")
    public CommonResult<PageInfo<SmsPurchaseRecordEntity>> queryPropertyDeposit(@RequestBody BaseQO<SmsPurchaseRecordQO> smsPurchaseRecordQO) {
        return CommonResult.ok(smsPurchaseRecordService.querySmsPurchaseRecord(smsPurchaseRecordQO));
    }
    
    /**
     * @Description: 批量删除短信购买记录
     * @author: DKS
     * @since: 2021/12/9 17:05
     * @Param: [ids]
     * @return: com.jsy.community.vo.CommonResult
     */
    @DeleteMapping("/deleteIds")
    @Permit("community:admin:sms:purchase:deleteIds")
    public CommonResult deleteIds(@RequestParam("ids") List<Long> ids) {
        return CommonResult.ok(smsPurchaseRecordService.deleteIds(ids) ? "删除成功" : "删除失败");
    }
}
