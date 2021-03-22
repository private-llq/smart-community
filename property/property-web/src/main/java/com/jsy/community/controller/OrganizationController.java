package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IOrganizationService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.OrganizationEntity;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.TreeCommunityVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lihao
 * @since 2021-03-15
 */
@Api(tags = "组织机构控制器")
@RestController
@ApiJSYController
@RequestMapping("/organization")
public class OrganizationController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IOrganizationService organizationService;
	
	@ApiOperation("树形查询所有组织")
	@GetMapping("/listOrganization")
	public CommonResult<TreeCommunityVO> listOrganization(@ApiParam("社区id") @RequestParam Long communityId) {
		TreeCommunityVO treeCommunityVO = organizationService.listOrganization(communityId);
		return CommonResult.ok(treeCommunityVO);
	}
	
	@ApiOperation("新增组织机构")
	@PostMapping("/addOrganization")
	public CommonResult addOrganization(@RequestBody OrganizationEntity organizationEntity) {
		ValidatorUtils.validateEntity(organizationEntity, OrganizationEntity.addOrganizationValidate.class);
		organizationService.addOrganization(organizationEntity);
		return CommonResult.ok();
	}
	
	// TODO: 2021/3/22 组织机构没有对其下有成员时做判定限制
	@ApiOperation("删除组织机构")
	@GetMapping("/deleteOrganization")
	public CommonResult deleteOrganization(@RequestParam Long id, @RequestParam Long communityId) {
		organizationService.deleteOrganization(id, communityId);
		return CommonResult.ok();
	}
	
	@ApiOperation("根据id查询组织机构")
	@GetMapping("/getOrganizationById")
	public CommonResult getOrganizationById(@RequestParam Long id, @RequestParam Long communityId) {
		OrganizationEntity organization = organizationService.getOrganizationById(id, communityId);
		return CommonResult.ok(organization);
	}
	
	
	@ApiOperation("修改组织机构")
	@PostMapping("/updateOrganization")
	public CommonResult updateOrganization(@RequestBody OrganizationEntity organization) {
		ValidatorUtils.validateEntity(organization, OrganizationEntity.updateOrganizationValidate.class);
		organizationService.updateOrganization(organization);
		return CommonResult.ok();
	}
	
	
}

