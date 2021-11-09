package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.CommunityRfService;
import com.jsy.community.api.CommunityRfSycRecordService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CommunityRfEntity;
import com.jsy.community.entity.property.CommunityRfSycRecordEntity;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Pipi
 * @Description: 门禁卡控制器
 * @Date: 2021/11/3 16:55
 * @Version: 1.0
 **/
@RestController
@ApiJSYController
@RequestMapping("/rf")
@Login
public class CommunityRfController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private CommunityRfService rfService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private CommunityRfSycRecordService rfSycRecordService;

    /**
     * @author: Pipi
     * @description: 添加门禁卡
     * @param rfEntity:
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/11/5 14:24
     **/
    @PostMapping("/v2/addRf")
    public CommonResult addRf(@RequestBody CommunityRfEntity rfEntity) {
        ValidatorUtils.validateEntity(rfEntity, CommunityRfEntity.addEfValidateGroup.class);
        if (rfEntity.getEnableStatus() == null) {
            rfEntity.setEnableStatus(1);
        }
        rfEntity.setCommunityId(UserUtils.getAdminCommunityId());
        return rfService.addRf(rfEntity) == 1 ? CommonResult.ok() : CommonResult.error("添加失败!");
    }
}
