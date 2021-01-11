package com.jsy.community.controller.outer;

import com.jsy.community.annotation.ApiOutController;
import com.jsy.community.api.ICommunityService;
import com.jsy.community.api.IHouseMemberService;
import com.jsy.community.api.IHouseService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CommunityQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author chq459799974
 * @description 智慧养老调用
 * @since 2021-01-11 13:23
 **/
@Api(tags = "智慧养老")
@ApiOutController
@RestController
@RequestMapping("pension")
public class PensionOutController {
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private ICommunityService iCommunityService;
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IHouseService houseService;
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IHouseMemberService houseMemberService;
	
	/**
	 * @Description: 社区分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.CommunityEntity>>
	 * @Author: chq459799974
	 * @Date: 2020/12/29
	 **/
	@PostMapping("community/page")
	public CommonResult<PageInfo<CommunityEntity>> queryCommunity(@RequestBody BaseQO<CommunityQO> baseQO){
		return CommonResult.ok(iCommunityService.queryCommunity(baseQO));
	}
	
	/**
	* @Description: ids批量查社区
	 * @Param: [ids]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/1/11
	**/
	@ApiOperation("批量查询社区信息")
	@PostMapping("community/getByIds")
	public CommonResult queryCommunityNameByIdBatch(@RequestBody List<Long> ids){
		return CommonResult.ok(iCommunityService.queryCommunityNameByIdBatch(ids));
	}
	
	/**
	* @Description: id单查社区
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/1/11
	**/
	@ApiOperation("id单查查询社区")
	@GetMapping("community/getById")
	public CommonResult queryCommunityById(@RequestParam Long id){
		return CommonResult.ok(iCommunityService.queryCommunityById(id));
	}
	
	/**
	 * @Description: 批量查询房屋信息
	 * @Param: [ids]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/1/9
	 **/
	@ApiOperation("批量查询房屋信息")
	@PostMapping("house/getByIds")
	public CommonResult queryHouseByIdBatch(@RequestBody List<Long> ids){
		return CommonResult.ok(houseService.queryHouseByIdBatch(ids));
	}
	
	/**
	* @Description: 【房间成员】房屋id查成员
	 * @Param: [houseId]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/1/11
	**/
	@ApiOperation("【房间成员】房屋id查成员")
	@GetMapping("houseMember/queryByHouseId")
	public CommonResult queryByHouseId(@RequestParam Long houseId){
		return CommonResult.ok(houseMemberService.queryByHouseId(houseId));
	}
	
	/**
	* @Description: 【房间成员】id查成员
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/1/11
	**/
	@ApiOperation("【房间成员】id查成员")
	@GetMapping("houseMember/queryById")
	public CommonResult queryById(@RequestParam Long id){
		return CommonResult.ok(houseMemberService.queryById(id));
	}
	
	/**
	* @Description: 【房间成员】ids批量查成员
	 * @Param: [ids]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/1/11
	**/
	@ApiOperation("【房间成员】ids批量查成员")
	@PostMapping("houseMember/queryByIdBatch")
	public CommonResult queryByIdBatch(@RequestBody Set<Long> ids){
		if(ids.size() == 0){
			return CommonResult.ok();
		}
		return CommonResult.ok(houseMemberService.queryByIdBatch(ids));
	}
	
}
