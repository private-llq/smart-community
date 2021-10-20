package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CommunityQO;
import com.jsy.community.service.ICommunityService;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.CommunityPropertyListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chq459799974
 * @description 社区控制器
 * @since 2020-11-19 16:59
 **/
@RequestMapping("community")
@Api(tags = "社区控制器")
@Login(allowAnonymous = true)
@Slf4j
@RestController
@ApiJSYController
public class CommunityController {

    @Autowired
    private ICommunityService communityService;

    /**
     * @param communityEntity:
     * @author: DKS
     * @description: 物业端添加社区
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/10/18 11:43
     **/
    @Login
    @PostMapping("/add")
    public CommonResult addCommunity(@RequestBody CommunityEntity communityEntity) {
        ValidatorUtils.validateEntity(communityEntity, CommunityEntity.ProperyuAddValidatedGroup.class);
        // 新增数据
        return CommonResult.ok(communityService.addCommunity(communityEntity) ? "添加成功!" : "添加失败");
    }

    /**
     * @param baseQO:
     * @author: DKS
     * @description: 分页查询小区列表
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/10/18 11:43
     **/
    @Login
    @PostMapping("/query")
    public CommonResult communityList(@RequestBody BaseQO<CommunityQO> baseQO) {
        PageInfo<CommunityEntity> communityEntityPage = communityService.queryCommunity(baseQO);
        return CommonResult.ok(communityEntityPage);
    }

    /**
     * @param communityEntity:
     * @author: DKS
     * @description: 更新社区信息
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/10/18 11:39
     **/
    @Login
    @PutMapping("/update")
    public CommonResult updateCommunity(@RequestBody CommunityEntity communityEntity) {
        if (communityEntity.getId() == null) {
            throw new JSYException(400, "社区ID不能为空!");
        }
        // 需要判定用户有权限的社区是否包含该社区
//		AdminInfoVo adminUserInfo = UserUtils.getAdminUserInfo();
//		if (!adminUserInfo.getCommunityIdList().contains(communityEntity.getId())) {
//			throw new JSYException(400, "你没有该社区的操作权限!");
//		}
        ValidatorUtils.validateEntity(communityEntity, CommunityEntity.ProperyuAddValidatedGroup.class);
        return communityService.updateCommunity(communityEntity) > 0 ? CommonResult.ok("更新成功") : CommonResult.error("更新失败");
    }

    /**
     * @Description: 删除社区信息
     * @Param: [id]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: DKS
     * @Date: 2021/10/18
     **/
    @Login
    @DeleteMapping("delete")
    public CommonResult delCommunity(@RequestParam("id") Long id) {
        return communityService.delCommunity(id) ? CommonResult.ok("删除成功") : CommonResult.error(JSYError.INTERNAL.getCode(), "删除失败");
    }

    /**
     * @Description: 社区列表查询
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: DKS
     * @Date: 2021/10/19
     **/
    @Login
    @GetMapping("list")
    public CommonResult queryCommunityList() {
        return CommonResult.ok(communityService.queryCommunityList());
    }

    @ApiOperation("查询小区名字和物业公司名字")
    @Login(allowAnonymous = true)
    @GetMapping("/property/list")
    public CommonResult<List<CommunityPropertyListVO>> queryCommunityAndPropertyList() {
        return CommonResult.ok(communityService.queryCommunityAndPropertyList());
    }
}
