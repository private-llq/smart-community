package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IHouseRecentService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.lease.HouseRecentEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author YuLF
 * @since 2020-12-26 13:55
 */
@Slf4j
@ApiJSYController
@RestController
@Api(tags = "租赁最近浏览控制器")
@RequestMapping("/house/browses")
public class HouseRecentController {

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private IHouseRecentService houseRecentService;

    @GetMapping()
    @ApiOperation("房屋最近浏览")
    @Permit("community:lease:house:browses")
    public CommonResult<List<HouseRecentEntity>> recentBrowse(@RequestParam(required = false, defaultValue = "0") Integer type,
                                                              @RequestParam(required = false, defaultValue = "1") Long page,
                                                              @RequestParam(required = false, defaultValue = "10") Long size) {
        BaseQO<Object> qo = new BaseQO<>();
        qo.setPage(page);
        qo.setSize(size);
        ValidatorUtils.validatePageParam(qo);
        return CommonResult.ok(houseRecentService.recentBrowseList(type, qo, UserUtils.getUserId()));
    }

    @DeleteMapping()
    @ApiOperation("清空房屋最近浏览")
    @Permit("community:lease:house:browses")
    public CommonResult<Boolean> clearRecentBrowse(@RequestParam Integer type) {
        return houseRecentService.clearRecentBrowse(type, UserUtils.getUserId()) ? CommonResult.ok("清空成功!") : CommonResult.error("清空失败!");
    }


}
