package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ISmsSendRecordService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.SmsSendRecordEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.SmsSendRecordQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 短信发送记录
 * @author: DKS
 * @create: 2021-09-08 17:17
 **/
@Api(tags = "短信发送记录")
@RestController
@RequestMapping("/sms/send")
@ApiJSYController
public class SmsSendRecordController {
    
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ISmsSendRecordService smsSendRecordService;
    
    /**
     * @Description: 分页查询短信发送记录
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.SmsSendRecordEntity>>
     * @Author: DKS
     * @Date: 2021/09/08
     **/
    @ApiOperation("分页查询短信发送记录")
    @PostMapping("/query")
    @Permit("community:property:sms:send:query")
    public CommonResult<PageInfo<SmsSendRecordEntity>> queryPropertyDeposit(@RequestBody BaseQO<SmsSendRecordQO> baseQO) {
        SmsSendRecordQO query = baseQO.getQuery();
        if(query == null){
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少查询类型");
        }
        List<String> adminCommunityIdList = UserUtils.getAdminCommunityIdList();
        return CommonResult.ok(smsSendRecordService.querySmsSendRecord(baseQO, adminCommunityIdList));
    }
}
