package com.jsy.community.controller;

import com.jsy.community.api.visitor.TVisitorService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.visitor.TVisitorEntity;
import com.jsy.community.qo.visitor.TVisitorQO;
import com.jsy.community.utils.ValueAssertUtil;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


@RequestMapping("visitor")
@Api(tags = "访客控制器")
@RestController
public class VisitorController {

    @DubboReference(version = Const.version, group = Const.group)
    private TVisitorService tVisitorService;

    /**
    * @Description: 新增访客登记
     * @Param: [tVisitor]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2020/11/11
    **/
    @PostMapping
    public CommonResult save(@RequestBody TVisitorQO tVisitorQO){
        //参数验证
        ValueAssertUtil.assertStringValue(tVisitorQO.getName(),tVisitorQO.getContact());

        TVisitorEntity tVisitorEntity = new TVisitorEntity();
        BeanUtils.copyProperties(tVisitorQO,tVisitorEntity);
        boolean saveResult = tVisitorService.save(tVisitorEntity);
        if(saveResult){
            return CommonResult.ok(1);
        }
        return CommonResult.error(-1,"新增访客登记失败");
    }

    @GetMapping("")
    public CommonResult query(@RequestBody TVisitorQO tVisitorQO){
        return CommonResult.ok(tVisitorService.queryByPage(tVisitorQO).getRecords());
    }
}
