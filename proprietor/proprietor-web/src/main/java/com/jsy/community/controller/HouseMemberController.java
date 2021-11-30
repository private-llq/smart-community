package com.jsy.community.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IHouseMemberService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.HouseMemberQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 房间成员表 前端控制器 (此套房主邀请加入房间(添加房间成员)的方案暂时搁置)
 * </p>
 *
 * @author qq459799974
 * @since 2020-11-23
 */
@Api(tags = "房间成员控制器")
@RestController
@RequestMapping("houseMember")
// @ApiJSYController
@Login
public class HouseMemberController {
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IHouseMemberService houseMemberService;
	
//	@ApiOperation("【房间成员】邀请")
//	@PostMapping("")
//	public CommonResult addHouseMember(@RequestBody HouseMemberEntity houseMemberEntity){
//		ValidatorUtils.validateEntity(houseMemberEntity, HouseMemberEntity.addHouseMemberValidatedGroup.class);
//		// 验证是不是房主
//		boolean b = iHouseMemberService.checkHouseHolder(JwtUtils.getUserId(), houseMemberEntity.getHouseId());
//		if(!b){
//			return CommonResult.error(JSYError.UNAUTHORIZED.getCode(),"房主认证失败");
//		}
//		houseMemberEntity.setHouseholderId(JwtUtils.getUserId());
//		houseMemberEntity.setIsConfirm(null);
//		boolean result = iHouseMemberService.addHouseMember(houseMemberEntity);
//		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"房间成员邀请失败");
//	}
	
//	@ApiOperation("【房间成员】批量删除成员/撤销邀请")
//	@DeleteMapping("")
//	public CommonResult deleteHouseMember(@RequestBody List<Long> ids){
//		if(CollectionUtils.isEmpty(ids)){
//			return CommonResult.error(JSYError.REQUEST_PARAM);
//		}
//		boolean result = iHouseMemberService.deleteHouseMember(ids);
//		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"操作失败");
//	}
	
//	@ApiOperation("【房间成员】接受邀请")
//	@PutMapping("")
//	@Transactional(rollbackFor = Exception.class)
//	public CommonResult confirmJoin(@RequestBody HouseMemberEntity houseMemberEntity){
//		ValidatorUtils.validateEntity(houseMemberEntity, HouseMemberEntity.updateHouseMemberValidatedGroup.class);
//		houseMemberEntity.setUid(JwtUtils.getUserId());
//		boolean result = iHouseMemberService.confirmJoin(houseMemberEntity);
//		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"加入失败");
//	}
	
//	@ApiOperation("【房间成员】查询成员/查询邀请")
//	@PostMapping("page")
//	public CommonResult<Page<HouseMemberEntity>> queryHouseMemberPage(@RequestBody BaseQO<HouseMemberQO> baseQO){
//		baseQO.getQuery().setUid(JwtUtils.getUserId());
//		return CommonResult.ok(iHouseMemberService.queryHouseMemberPage(baseQO));
//	}
	
	@ApiOperation("【房间成员】查询")
	@PostMapping("page")
		public CommonResult<PageInfo<HouseMemberEntity>> queryHouseMemberPage(@RequestBody BaseQO<HouseMemberQO> baseQO){
		if( baseQO.getQuery() == null ){
			baseQO.setQuery(new HouseMemberQO());
		}
		baseQO.getQuery().setHouseholderId(UserUtils.getUserId());
		return CommonResult.ok(houseMemberService.getHouseMembers(baseQO));
	}

}

