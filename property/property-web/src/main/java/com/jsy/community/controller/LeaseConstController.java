package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.ILeaseConstService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseLeaseConstEntity;
import com.jsy.community.qo.property.HouseLeaseConstQO;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.LoginIgnore;
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

    @LoginIgnore
    @ApiOperation("常量分类查询接口")
    @GetMapping()
    public CommonResult<List<HouseLeaseConstEntity>> listConst(){
        return null;
    }

    @LoginIgnore
    @ApiOperation("常量修改接口")
    @PutMapping()
    @businessLog(operation = "编辑",content = "更新了【房屋租赁常量】")
    public CommonResult<Boolean> update(@RequestBody HouseLeaseConstQO qo){
        return null;
    }

    @LoginIgnore
    @ApiOperation("常量新增接口")
    @PostMapping()
    @businessLog(operation = "新增",content = "新增了【房屋租赁常量】")
    public CommonResult<Boolean> save(@RequestBody HouseLeaseConstQO qo){
        return null;
    }
}
