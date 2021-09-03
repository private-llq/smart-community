package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.ICarBlackListService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarBlackListEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
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



    /**
     * 分页查询 黑名单
     * @param baseQO 车牌号
     * @return
     */
    @Login
    @PostMapping("carBlackListPage")
    public CommonResult<PageInfo> carBlackListPage(@RequestBody BaseQO<String> baseQO){
        PageInfo<CarBlackListEntity> pageInfo = blackListService.carBlackListPage(baseQO, UserUtils.getAdminCommunityId());
        return CommonResult.ok(pageInfo);
    }



    /**
     * 添加进入黑名单
     * @param carBlackListEntity
     * @return
     */
    @Login
    @PostMapping("saveBlackList")
    @businessLog(operation = "新增",content = "新增了【车辆黑名单】")
    public CommonResult saveBlackList(@RequestBody CarBlackListEntity carBlackListEntity){
        blackListService.saveBlackList(carBlackListEntity,UserUtils.getAdminCommunityId());
        return CommonResult.ok();

    }


    /**
     * 移除黑名单
     * @param uid
     * @return
     */
    @DeleteMapping("delBlackList")
    @businessLog(operation = "删除",content = "移除了【车辆黑名单】")
    public CommonResult delBlackList(@RequestParam("uid") String uid){
        blackListService.delBlackList(uid);
        return CommonResult.ok();
    }
    }
