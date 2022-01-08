package com.jsy.community.controller;

import com.jsy.community.entity.ComplainEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.ComplainQO;
import com.jsy.community.service.AdminException;
import com.jsy.community.service.IComplainService;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @program: com.jsy.community
 * @description:  物业意见反馈
 * @author: DKS
 * @create: 2021-10-27 15:58
 **/
@Api(tags = "物业意见反馈")
@RestController
@RequestMapping("/complain")
// @ApiJSYController
public class ComplainController {

    @Resource
    private IComplainService complainService;
    
    /**
     * @Description: 意见反馈条件查询
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.ComplainEntity>>
     * @Author: DKS
     * @Date: 2021/10/27
     **/
    @ApiOperation("意见反馈条件查询")
    @PostMapping("/query")
    @Permit("community:admin:complain:query")
    public CommonResult<PageInfo<ComplainEntity>> queryComplain(@RequestBody BaseQO<ComplainQO> baseQO){
        if (baseQO.getQuery().getSource() == null) {
            throw new AdminException(JSYError.BAD_REQUEST.getCode(), "请传入来源");
        }
        return CommonResult.ok(complainService.queryComplain(baseQO));
    }
    
    /**
     * @Description: 意见反馈详情查询
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: DKS
     * @Date: 2021/10/19
     **/
    @GetMapping("/getDetail")
    @Permit("community:admin:complain:getDetail")
    public CommonResult getDetailComplain(Long id){
        return CommonResult.ok(complainService.getDetailComplain(id));
    }
    
    /**
     * @Description: 意见反馈删除
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: DKS
     * @Date: 2021/10/27
     **/
    @DeleteMapping("/delete")
    @Permit("community:admin:complain:delete")
    public CommonResult deleteComplain(Long id){
        return complainService.deleteComplain(id)? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"删除失败");
    }
}
