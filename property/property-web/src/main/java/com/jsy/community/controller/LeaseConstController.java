package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ILeaseConstService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseLeaseConstEntity;
import com.jsy.community.qo.property.HouseLeaseConstQO;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author YuLF
 * @since 2020-12-19 17:33
 */
@Api(tags = "物业端房屋租赁常量接口")
@RestController
@RequestMapping("lease/const")
@ApiJSYController
public class LeaseConstController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ILeaseConstService iLeaseConstService;


    @ApiOperation("常量分类查询接口")
    @GetMapping()
    public CommonResult<List<HouseLeaseConstEntity>> listConst(){
        return null;
    }

    @ApiOperation("常量修改接口")
    @PutMapping()
    public CommonResult<Boolean> update(@RequestBody HouseLeaseConstQO houseLeaseConstQO){
        return null;
    }

    @ApiOperation("常量新增接口")
    @PostMapping()
    public CommonResult<Boolean> save(@RequestBody HouseLeaseConstQO houseLeaseConstQO){
        return null;
    }
}
