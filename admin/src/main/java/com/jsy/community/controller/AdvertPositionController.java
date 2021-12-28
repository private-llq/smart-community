package com.jsy.community.controller;

import com.jsy.community.qo.admin.AddAdvertPositionQO;
import com.jsy.community.service.AdvertPositionService;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author xrq
 * @version 1.0
 * @Description: 广告位置控制层
 * @date 2021/12/25 16:03
 */

@RestController
@RequestMapping("/advert/position")
public class AdvertPositionController {

    @Autowired
    private AdvertPositionService positionService;

    @PostMapping("/insert")
    @Permit("community:admin:advert:position:insert")
    public CommonResult insert(@RequestBody @Valid AddAdvertPositionQO qo) {
        return CommonResult.ok(positionService.insertPosition(qo));
    }

    @PostMapping("/getList")
    @Permit("community:admin:advert:position:getList")
    public CommonResult getList() {
        return CommonResult.ok(positionService.list());
    }
}
