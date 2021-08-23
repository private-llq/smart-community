package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IOpLogService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.OpLogEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.OpLogQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author DKS
 * @description 操作日志控制器
 * @since 2021/8/23  11:43
 **/
@Api(tags = "操作日志控制器")
@RestController
@RequestMapping("/op/log")
@ApiJSYController
public class OpLogController {
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IOpLogService opLogService;
	
	
	/**
	 * @Description: 操作日志分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.OpLogEntity>>
	 * @Author: DKS
	 * @Date: 2021/08/05
	 **/
	@Login
	@ApiOperation("操作日志分页查询")
	@PostMapping("/building/type/query")
	public CommonResult<PageInfo<OpLogEntity>> queryOpLogPage(@RequestBody BaseQO<OpLogQO> baseQO) {
		OpLogQO query = baseQO.getQuery();
		if(query == null){
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少查询类型");
		}
		query.setCommunityId(UserUtils.getAdminCommunityId());
		return CommonResult.ok(opLogService.queryOpLogPage(baseQO));
	}
}
