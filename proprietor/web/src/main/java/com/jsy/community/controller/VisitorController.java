package com.jsy.community.controller;

import com.jsy.community.api.visitor.ITVisitorService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.visitor.VisitorEntity;
import com.jsy.community.qo.visitor.VisitorQO;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

@RequestMapping("visitor")
@Api(tags = "访客控制器")
@RestController
public class VisitorController {

    @DubboReference(version = Const.version, group = Const.group)
    private ITVisitorService iTVisitorService;

    /**
    * @Description: 新增访客登记
     * @Param: [tVisitor]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2020/11/11
    **/
    @PostMapping("")
    public CommonResult save(@RequestBody VisitorEntity visitorEntity){
        boolean saveResult = iTVisitorService.save(visitorEntity);
        if(saveResult){
            return CommonResult.ok("");
        }
        return CommonResult.error(-1,"新增访客登记失败");
    }

    @GetMapping("")
    public CommonResult query(@RequestBody VisitorQO visitorQO){
        return CommonResult.ok(iTVisitorService.queryByPage(visitorQO).getRecords());
    }

    @PutMapping("")
    public CommonResult update(){
        return CommonResult.ok("");
    }
}
