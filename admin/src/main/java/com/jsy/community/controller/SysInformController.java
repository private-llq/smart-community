package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.entity.PushInformEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.PushInformQO;
import com.jsy.community.service.ISysInformService;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * @author YuLF
 * @since 2020-12-21 11:18
 */
@Slf4j
@Api(tags = "系统消息控制器")
@RestController
@RequestMapping("sys/inform")
// @ApiJSYController
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
	 * @Description: 大后台推送消息分页查询
	 * @author: DKS
	 * @since: 2021/11/17 14:59
	 * @Param: [baseQO]
	 * @return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.PushInformEntity>>
	 */
	@ApiOperation("大后台推送消息分页查询")
	@PostMapping("/query")
	@Permit("community:admin:sys:inform:query")
	public CommonResult<PageInfo<PushInformEntity>> querySysInform(@RequestBody BaseQO<PushInformQO> baseQO) {
		return CommonResult.ok(iSysInformService.querySysInform(baseQO));
	}
	
	/**
	 * @Description: 添加推送通知消息
	 * @author: DKS
	 * @since: 2021/11/17 15:00
	 * @Param: [qo]
	 * @return: com.jsy.community.vo.CommonResult<java.lang.Boolean>
	 */
    @PostMapping("/add")
    @ApiOperation("添加推送通知消息")
    @businessLog(operation = "新增",content = "新增了【推送通知消息】")
    @Permit("community:admin:sys:inform:add")
    public CommonResult<Boolean> addPushInform(@RequestBody PushInformQO qo) {
        qo.setPushTarget(0);
        // 大后台默认开启推送
	    qo.setPushTag(1);
        ValidatorUtils.validateEntity(qo);
        qo.setUid(UserUtils.getId());
        return iSysInformService.addPushInform(qo) ? CommonResult.ok("添加成功!") : CommonResult.error("添加失败!");
    }
	
	/**
	 * @Description: 删除推送通知消息
	 * @author: DKS
	 * @since: 2021/11/17 15:00
	 * @Param: [id]
	 * @return: com.jsy.community.vo.CommonResult<java.lang.Boolean>
	 */
	@DeleteMapping("/delete")
	@ApiOperation("删除推送通知消息")
	@businessLog(operation = "删除",content = "删除了【推送通知消息】")
	@Permit("community:admin:sys:inform:delete")
	public CommonResult<Boolean> deletePushInform(@RequestParam Long id) {
		return iSysInformService.deletePushInform(id, UserUtils.getId()) ? CommonResult.ok("删除成功!") : CommonResult.error(JSYError.NOT_IMPLEMENTED);
	}
	
	/**
	 * @Description: 获取单条消息详情
	 * @author: DKS
	 * @since: 2021/11/17 15:00
	 * @Param: [id]
	 * @return: com.jsy.community.vo.CommonResult<?>
	 */
	@GetMapping("/getDetatil")
	@ApiOperation("获取单条消息详情")
	@Permit("community:admin:sys:inform:getDetatil")
	public CommonResult<?> getDetatil(@RequestParam Long id) {
		return CommonResult.ok(iSysInformService.getDetail(id));
	}

}
