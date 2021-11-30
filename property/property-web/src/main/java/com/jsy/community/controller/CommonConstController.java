package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICommonConstService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 公共常量表 前端控制器
 * </p>
 *
 * @author lihao
 * @since 2020-12-25
 */
@Api(tags = "公共常量控制器")
// @ApiJSYController
@Slf4j
@RestController
@RequestMapping("/const")
public class CommonConstController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private ICommonConstService commonConstService;

	@LoginIgnore
	@ApiOperation("字典资源查询")
	@GetMapping("typeSources")
	@Permit("community:property:const:typeSources")
	public CommonResult typeSources(@RequestParam String typeKey){
		HashMap<String, Object> map = new HashMap<>();
		String[] split = typeKey.split(",");
		if (split!=null){
			for (String s : split) {
				List<Map<String, Object>> maps = BusinessEnum.sourceMap.get(s);
				if(!CollectionUtils.isEmpty(maps)){
					map.put(s,BusinessEnum.sourceMap.get(s));
				}
			}
		}
		return CommonResult.ok(map);
	}
}

