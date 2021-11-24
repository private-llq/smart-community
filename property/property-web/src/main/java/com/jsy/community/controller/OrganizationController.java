package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.IOrganizationService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.OrganizationEntity;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.TreeCommunityVO;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 组织机构控制器
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
	@Permit("community:property:organization:listOrganization")
	public CommonResult<TreeCommunityVO> listOrganization() {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		TreeCommunityVO treeCommunityVO = organizationService.listOrganization(communityId);
		return CommonResult.ok(treeCommunityVO);
	}
	
	@ApiOperation("新增组织机构")
	@PostMapping("/addOrganization")
	@businessLog(operation = "新增",content = "新增了【组织机构】")
	@Permit("community:property:organization:addOrganization")
	public CommonResult addOrganization(@RequestBody OrganizationEntity organizationEntity) {
		organizationEntity.setCommunityId(UserUtils.getAdminUserInfo().getCommunityId());
		organizationEntity.setId(SnowFlake.nextId());
		
		ValidatorUtils.validateEntity(organizationEntity, OrganizationEntity.addOrganizationValidate.class);
		organizationService.addOrganization(organizationEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("删除组织机构")
	@GetMapping("/deleteOrganization")
	@businessLog(operation = "删除",content = "删除了【组织机构】")
	@Permit("community:property:organization:deleteOrganization")
	public CommonResult deleteOrganization(@RequestParam Long id) {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		organizationService.deleteOrganization(id, communityId);
		return CommonResult.ok();
	}
	
	@ApiOperation("根据id查询组织机构")
	@GetMapping("/getOrganizationById")
	@Permit("community:property:organization:getOrganizationById")
	public CommonResult getOrganizationById(@RequestParam Long id) {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		OrganizationEntity organization = organizationService.getOrganizationById(id, communityId);
		return CommonResult.ok(organization);
	}
	
	@ApiOperation("修改组织机构")
	@PostMapping("/updateOrganization")
	@businessLog(operation = "编辑",content = "更新了【组织机构】")
	@Permit("community:property:organization:updateOrganization")
	public CommonResult updateOrganization(@RequestBody OrganizationEntity organization) {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		organization.setCommunityId(communityId);
		ValidatorUtils.validateEntity(organization, OrganizationEntity.updateOrganizationValidate.class);
		organizationService.updateOrganization(organization);
		return CommonResult.ok();
	}
}

