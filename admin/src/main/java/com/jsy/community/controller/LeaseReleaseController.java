package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.LeaseReleasePageQO;
import com.jsy.community.service.LeaseReleaseService;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.lease.LeaseReleasePageVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@ApiJSYController
@RequestMapping("/application/lease")
public class LeaseReleaseController {

    @Autowired
    private LeaseReleaseService leaseReleaseService;


    @ApiOperation("商铺和房屋租赁信息发布列表")
    @PostMapping("/release/page")
    @Login(allowAnonymous = true)
    public CommonResult<PageInfo<LeaseReleasePageVO>> queryLeaseReleasePage(@RequestBody BaseQO<LeaseReleasePageQO> baseQO) {
        if (baseQO.getQuery() == null) {
            baseQO.setQuery(new LeaseReleasePageQO());
        }
        return CommonResult.ok(leaseReleaseService.queryLeaseReleasePage(baseQO));
    }
}
