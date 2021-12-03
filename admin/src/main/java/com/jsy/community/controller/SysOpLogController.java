package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.entity.SysOpLogEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.OpLogQO;
import com.jsy.community.service.ISysOpLogService;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author DKS
 * @description 大后台操作日志控制器
 * @since 2021/10/20  12:00
 **/
@Api(tags = "操作日志控制器")
@RestController
@RequestMapping("/op/log")
@ApiJSYController
public class SysOpLogController {
	
	@Resource
	private ISysOpLogService sysOpLogService;
	
	/**
	 * @Description: 操作日志分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.SysOpLogEntity>>
	 * @Author: DKS
	 * @Date: 2021/08/05
	 **/
	@ApiOperation("操作日志分页查询")
	@PostMapping("/query")
	@Permit("community:admin:op:log:query")
	public CommonResult<PageInfo<SysOpLogEntity>> queryOpLogPage(@RequestBody BaseQO<OpLogQO> baseQO) {
		return CommonResult.ok(sysOpLogService.queryOpLogPage(baseQO));
	}
}
