package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.businessLog;
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
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author chq459799974
 * @description 社区控制器
 * @since 2020-11-19 16:59
 **/
@RequestMapping("community")
@Api(tags = "社区控制器")
@Slf4j
@RestController
// @ApiJSYController
public class CommunityController {

    @Resource
    private ICommunityService communityService;

    /**
     * @param communityEntity:
     * @author: DKS
     * @description: 物业端添加社区
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/10/18 11:43
     **/
    @PostMapping("/add")
    @businessLog(operation = "新增", content = "新增了【社区】")
    @Permit("community:admin:community:add")
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
    @PostMapping("/query")
    @Permit("community:admin:community:query")
    public CommonResult<PageInfo<CommunityEntity>> communityList(@RequestBody BaseQO<CommunityQO> baseQO) {
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
    @PutMapping("/update")
    @businessLog(operation = "更新", content = "更新了【社区】")
    @Permit("community:admin:community:update")
    public CommonResult updateCommunity(@RequestBody CommunityEntity communityEntity) {
        if (communityEntity.getId() == null) {
            throw new JSYException(400, "社区ID不能为空!");
        }
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
    @DeleteMapping("delete")
    @businessLog(operation = "删除", content = "删除了【社区】")
    @Permit("community:admin:community:delete")
    public CommonResult<Boolean> delCommunity(@RequestParam("id") Long id) {
        return communityService.delCommunity(id) ? CommonResult.ok("删除成功") : CommonResult.error(JSYError.INTERNAL.getCode(), "删除失败");
    }

    /**
     * @Description: 社区列表查询
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: DKS
     * @Date: 2021/10/19
     **/
    @GetMapping("list")
    @Permit("community:admin:community:list")
    public CommonResult<List<CommunityEntity>> queryCommunityList() {
        return CommonResult.ok(communityService.queryCommunityList());
    }

    @ApiOperation("查询小区名字和物业公司名字")
    @GetMapping("/property/list")
    @Permit("community:admin:community:property:list")
    public CommonResult<List<CommunityPropertyListVO>> queryCommunityAndPropertyList() {
        return CommonResult.ok(communityService.queryCommunityAndPropertyList());
    }
}
