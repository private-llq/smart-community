package com.jsy.community.controller;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICarBlackListService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.CarBlackListEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

@Api(tags = "车辆黑名单")
@RestController
@RequestMapping("/carBlackList")
@ApiJSYController
public class CarBlackListController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICarBlackListService blackListService;


    @GetMapping("carBlackListPage")
    CommonResult<PageInfo> carBlackListPage(@RequestBody BaseQO<String> baseQO){
        PageInfo<CarBlackListEntity> pageInfo = blackListService.carBlackListPage(baseQO);
        return CommonResult.ok(pageInfo);
    }

    @PostMapping("saveBlackList")
    CommonResult saveBlackList(@RequestBody CarBlackListEntity carBlackListEntity){
        blackListService.saveBlackList(carBlackListEntity);
        return CommonResult.ok();

    }

    @DeleteMapping("delBlackList")
    CommonResult delBlackList(@RequestParam("uid") String uid){
        blackListService.delBlackList(uid);
        return CommonResult.ok();
    }


}