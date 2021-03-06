package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.StrangerRecordService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.StrangerRecordEntiy;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Pipi
 * @Description: 陌生人脸记录控制器
 * @Date: 2021/8/27 11:30
 * @Version: 1.0
 **/
@RestController
// @ApiJSYController
@RequestMapping("/strangerRecord")
public class StrangerRecordController {
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private StrangerRecordService strangerRecordService;

    /**
     * @author: Pipi
     * @description: 分页查询陌生人脸记录
     * @param baseQO: 查询条件
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/8/27 11:32
     **/
    @PostMapping("/v2/pageStrangerRecord")
    @Permit("community:property:strangerRecord:v2:pageStrangerRecord")
    public CommonResult pageStrangerRecord(@RequestBody BaseQO<StrangerRecordEntiy> baseQO) {
        return CommonResult.ok(strangerRecordService.pageStrangerRecord(baseQO, UserUtils.getAdminCommunityId()));
    }
}
