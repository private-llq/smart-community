package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICarBlackListService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarBlackListEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.util.CarOperation;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

@Api(tags = "车辆黑名单")
@RestController
@RequestMapping("/carBlackList")
@ApiJSYController
public class CarBlackListController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarBlackListService blackListService;



    /**
     * 分页查询 黑名单
     * @param baseQO 车牌号
     * @return
     */
    @PostMapping("carBlackListPage")
    @Permit("community:property:carBlackList:carBlackListPage")
    public CommonResult<PageInfo> carBlackListPage(@RequestBody BaseQO<String> baseQO){
        PageInfo<CarBlackListEntity> pageInfo = blackListService.carBlackListPage(baseQO, UserUtils.getAdminCommunityId());
        return CommonResult.ok(pageInfo);
    }
    /**
     * 查询车牌是否是 黑名单
     * @param
     * @return
     */
    @PostMapping("carBlackListOne")
    @Permit("community:property:carBlackList:carBlackListOne")
    public CommonResult carBlackListEntity(@RequestParam("carNumber")String carNumber){
        Long adminCommunityId = UserUtils.getAdminCommunityId();

        CarBlackListEntity carBlackListEntity = blackListService.carBlackListOne(carNumber,adminCommunityId);

        return CommonResult.ok(carNumber,"查询成功");
    }


    /**
     * 添加进入黑名单
     * @param carBlackListEntity
     * @return
     */
    @PostMapping("saveBlackList")
    @CarOperation(operation = "新增了【车辆黑名单】")
    @Permit("community:property:carBlackList:saveBlackList")
    public CommonResult saveBlackList(@RequestBody CarBlackListEntity carBlackListEntity){
        blackListService.saveBlackList(carBlackListEntity,UserUtils.getAdminCommunityId());
        return CommonResult.ok();

    }


    /**
     * 移除黑名单
     * @param uid
     * @return
     */
    @LoginIgnore
    @DeleteMapping("delBlackList")
    @CarOperation(operation = "移除了【车辆黑名单】")
    public CommonResult delBlackList(@RequestParam("uid") String uid){
        blackListService.delBlackList(uid);
        return CommonResult.ok();
    }
    }
