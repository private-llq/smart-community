package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IHouseFavoriteService;
import com.jsy.community.constant.Const;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseFavoriteQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.lease.HouseFavoriteVO;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author YuLF
 * @since 2020-12-29 17:06
 */
@Slf4j
// @ApiJSYController
@RestController
@RequestMapping("/favorite")
@Api(tags = "房屋租售收藏控制器")
public class HouseFavoriteController {

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private IHouseFavoriteService iHouseFavoriteService;
    
    @PostMapping()
    @ApiOperation("租房收藏接口")
    @Permit("community:lease:favorite")
    public CommonResult<Boolean> houseFavorite(@RequestBody HouseFavoriteQO qo) {
        ValidatorUtils.validateEntity(qo, HouseFavoriteQO.AddFavorite.class);
        //验证数据有效性 如果不存在
        if (!iHouseFavoriteService.hasHouseOrShop(qo)) {
            throw new JSYException("被收藏房屋不存在!");
        }
        qo.setUid(UserUtils.getUserId());
        return CommonResult.ok(iHouseFavoriteService.houseFavorite(qo), "收藏成功!");
    }
    
    @PostMapping("/shop")
    @ApiOperation("商铺收藏列表")
    @Permit("community:lease:favorite:shop")
    public CommonResult<List<HouseFavoriteVO>> shopFavorite(@RequestBody BaseQO<HouseFavoriteQO> qo) {
        ValidatorUtils.validatePageParam(qo);
        if (qo.getQuery() == null) {
            qo.setQuery(new HouseFavoriteQO());
        }
        qo.getQuery().setUid(UserUtils.getUserId());
        return CommonResult.ok(iHouseFavoriteService.shopFavorite(qo));
    }

    @PostMapping("/house")
    @ApiOperation("租房收藏列表")
    @Permit("community:lease:favorite:house")
    public CommonResult<List<HouseFavoriteVO>> leaseFavorite(@RequestBody BaseQO<HouseFavoriteQO> qo) {
        ValidatorUtils.validatePageParam(qo);
        if (qo.getQuery() == null) {
            qo.setQuery(new HouseFavoriteQO());
        }
        qo.getQuery().setUid(UserUtils.getUserId());
        return CommonResult.ok(iHouseFavoriteService.leaseFavorite(qo));
    }

    @DeleteMapping()
    @ApiOperation("删除我的收藏")
    @Permit("community:lease:favorite")
    public CommonResult<Boolean> deleteFavorite(@RequestParam Long id) {
        return iHouseFavoriteService.deleteFavorite(id, UserUtils.getUserId()) ? CommonResult.ok("删除成功!") : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }

    /**
     * @author: Pipi
     * @description: 用户取消收藏
     * @param: id:  房屋ID或商铺ID
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/7/8 16:05
     **/
    @DeleteMapping("/cancelFavorite")
    @Permit("community:lease:favorite:cancelFavorite")
    public CommonResult cancelFavorite(@RequestParam Long id) {
        HouseFavoriteQO qo = new HouseFavoriteQO();
        qo.setUid(UserUtils.getUserId());
        qo.setFavoriteId(id);
        return iHouseFavoriteService.cancelFavorite(qo) ? CommonResult.ok("取消收藏成功!") : CommonResult.error("取消收藏失败!");
    }

}
