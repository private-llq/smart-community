package com.jsy.community.controller;

import com.jsy.community.entity.proprietor.AssetLeaseRecordEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.LeaseReleasePageQO;
import com.jsy.community.service.LeaseReleaseService;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.LeaseReleaseInfoVO;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@Slf4j
// @ApiJSYController
@RequestMapping("/application/lease")
public class LeaseReleaseController {

    @Resource
    private LeaseReleaseService leaseReleaseService;


    @ApiOperation("商铺和房屋租赁信息发布列表")
    @PostMapping("/release/page")
    @Permit("community:admin:application:lease:release:page")
    public CommonResult<PageInfo<AssetLeaseRecordEntity>> queryLeaseReleasePage(@RequestBody BaseQO<LeaseReleasePageQO> baseQO) {
        if (baseQO.getQuery() == null) {
            baseQO.setQuery(new LeaseReleasePageQO());
        }
        return CommonResult.ok(leaseReleaseService.queryLeaseReleasePage(baseQO));
    }

    @ApiOperation("商铺和房屋租赁信息发布详情")
    @GetMapping("/release/info")
    @Permit("community:admin:application:lease:release:info")
    public CommonResult<LeaseReleaseInfoVO> queryLeaseHouseInfo(@RequestParam("id") Long id,
                                                                @RequestParam("type") Integer type) {
        return CommonResult.ok(leaseReleaseService.queryLeaseHouseInfo(id, type));
    }
}
