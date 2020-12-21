package com.jsy.community.controller;

import com.jsy.community.annotation.auth.Login;
import com.jsy.community.entity.sys.SysInformEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.sys.SysInformQO;
import com.jsy.community.service.ISysInformService;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author YuLF
 * @since 2020-12-21 11:18
 */
@Slf4j
@Api(tags = "系统消息控制器")
@RestController
@RequestMapping("sys/inform")
public class SysInformController {


    private final ISysInformService iSysInformService;

    @Autowired
    public SysInformController(ISysInformService iSysInformService) {
        this.iSysInformService = iSysInformService;
    }


    @Login
    @ApiOperation("系统消息新增")
    @PostMapping()
    public CommonResult<Boolean> add(@RequestBody SysInformQO sysInformQO){
        ValidatorUtils.validateEntity(sysInformQO, SysInformQO.addSysInformValidate.class);
        return iSysInformService.add(sysInformQO) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }

    @Login
    @ApiOperation("系统消息修改")
    @PutMapping()
    public CommonResult<Boolean> update(@RequestBody SysInformQO sysInformQO, Long informId){
        return iSysInformService.update(sysInformQO, informId) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }

    @Login
    @ApiOperation("系统消息删除")
    @DeleteMapping()
    public CommonResult<Boolean> delete(@RequestParam Long informId){
        return iSysInformService.delete(informId) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED.getCode(),"数据不存在!");
    }


    @Login
    @ApiOperation("批量系统消息删除")
    @DeleteMapping("/batch")
    public CommonResult<Boolean> delete(@RequestBody List<Long> informIds){
        if(informIds == null || informIds.isEmpty()){
            throw new JSYException(JSYError.BAD_REQUEST.getCode(), "没有可以删除的系统消息!");
        }
        return iSysInformService.deleteBatchByIds(informIds) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED.getCode(),"数据不存在!");
    }

    @Login
    @ApiOperation("系统消息列表")
    @GetMapping()
    public CommonResult<List<SysInformEntity>> query(@RequestBody BaseQO<SysInformQO> baseQO){
        ValidatorUtils.validatePageParam(baseQO);
        if(baseQO.getQuery() == null){
            throw new JSYException(JSYError.BAD_REQUEST);
        }
        return CommonResult.ok(iSysInformService.query(baseQO));
    }

}
