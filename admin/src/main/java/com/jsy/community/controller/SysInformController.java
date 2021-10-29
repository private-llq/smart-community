package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.entity.PushInformEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.OldPushInformQO;
import com.jsy.community.qo.proprietor.PushInformQO;
import com.jsy.community.service.ISysInformService;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author YuLF
 * @since 2020-12-21 11:18
 */
@Slf4j
@Api(tags = "系统消息控制器")
@RestController
@RequestMapping("sys/inform")
@ApiJSYController
public class SysInformController {
    
    @Resource
    private ISysInformService iSysInformService;

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
    @businessLog(operation = "新增",content = "新增了【系统消息】")
    public CommonResult<Boolean> add( @RequestBody OldPushInformQO qo){
        ValidatorUtils.validateEntity(qo, OldPushInformQO.AddPushInformValidate.class);
        return iSysInformService.add(qo) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }


    @Login
    @ApiOperation("系统消息删除")
    @DeleteMapping()
    @businessLog(operation = "删除",content = "删除了【系统消息】")
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
    public CommonResult<List<PushInformEntity>> query(@RequestBody BaseQO<OldPushInformQO> qo){
        ValidatorUtils.validatePageParam(qo);
        if(qo.getQuery() == null){
            qo.setQuery(new OldPushInformQO());
        }
        qo.getQuery().setId(sysInformId);
        return CommonResult.ok(iSysInformService.query(qo));
    }
    
    /**
     * 新增推送通知消息
     * @param qo 新增推送消息
     * @return 返回是否新增成功
     */
    @Login
    @PostMapping("/add")
    @ApiOperation("添加推送通知消息")
    @businessLog(operation = "新增",content = "新增了【推送通知消息】")
    public CommonResult<Boolean> addPushInform(@RequestBody PushInformQO qo) {
        qo.setPushTarget(1);
        if (qo.getPushTag() == null) {
            // 默认关闭推送
            qo.setPushTag(0);
        }
        ValidatorUtils.validateEntity(qo);
        qo.setUid(UserUtils.getId());
        return iSysInformService.addPushInform(qo) ? CommonResult.ok("添加成功!") : CommonResult.error("添加失败!");
    }
	
	/**
	 * (物业端)删除通知消息 [管理员]
	 * @param id 消息id
	 * @return 返回修改成功值
	 */
	@Login
	@DeleteMapping("/delete")
	@ApiOperation("删除推送通知消息")
	@businessLog(operation = "删除",content = "删除了【推送通知消息】")
	public CommonResult<Boolean> deletePushInform(@RequestParam Long id) {
		return iSysInformService.deletePushInform(id, UserUtils.getId()) ? CommonResult.ok("删除成功!") : CommonResult.error(JSYError.NOT_IMPLEMENTED);
	}
	
	/**
	 *@Author: DKS
	 *@Description:  获取单条消息详情
	 *@Param: id: 消息ID
	 *@Return: com.jsy.community.vo.CommonResult<?>
	 *@Date: 2021/10/27 14:30
	 **/
	@Login
	@GetMapping("/getDetatil")
	@ApiOperation("获取单条消息详情")
	public CommonResult<?> getDetatil(@RequestParam Long id) {
		return CommonResult.ok(iSysInformService.getDetail(id));
	}

}
