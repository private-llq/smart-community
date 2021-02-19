package com.jsy.community.controller;

import com.jsy.community.annotation.auth.Login;
import com.jsy.community.entity.PushInformEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.PushInformQO;
import com.jsy.community.service.ISysInformService;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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


    @Value("${jsy.sys.inform-id}")
    private Long sysInformId;


    /**
     * 注：此方法使用了Aop 类：SysAop.java
     * @param qo    新增参数
     * @return      返回结果
     */
    @Login
    @ApiOperation("系统消息新增")
    @PostMapping()
    public CommonResult<Boolean> add( @RequestBody PushInformQO qo){
        ValidatorUtils.validateEntity(qo, PushInformQO.AddPushInformValidate.class);
        return iSysInformService.add(qo) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }


    @Login
    @ApiOperation("系统消息删除")
    @DeleteMapping()
    public CommonResult<Boolean> delete(@RequestParam Long id){
        return iSysInformService.delete(id) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED.getCode(),"数据不存在!");
    }


    @Login
    @ApiOperation("批量系统消息删除")
    @DeleteMapping("/batch")
    public CommonResult<Boolean> deleteBatchByIds(@RequestBody List<Long> informIds){
        if(informIds == null || informIds.isEmpty()){
            throw new JSYException(JSYError.BAD_REQUEST.getCode(), "没有可以删除的系统消息!");
        }
        return iSysInformService.deleteBatchByIds(informIds) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED.getCode(),"数据不存在!");
    }

    @Login
    @ApiOperation("系统消息列表")
    @PostMapping("/list")
    public CommonResult<List<PushInformEntity>> query(@RequestBody BaseQO<PushInformQO> baseQO){
        ValidatorUtils.validatePageParam(baseQO);
        if(baseQO.getQuery() == null){
            baseQO.setQuery(new PushInformQO());
        }
        baseQO.getQuery().setId(sysInformId);
        return CommonResult.ok(iSysInformService.query(baseQO));
    }

}
