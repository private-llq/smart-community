package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IHouseFavoriteService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseFavoriteQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.shop.ShopLeaseVO;
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
@ApiJSYController
@RestController
@RequestMapping("/favorite")
@Api(tags = "房屋租售收藏控制器")
public class HouseFavoriteController {

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private IHouseFavoriteService iHouseFavoriteService;


    @Login
    @PostMapping()
    @ApiOperation("租房收藏接口")
    public CommonResult<Boolean> houseFavorite(@RequestBody HouseFavoriteQO qo) {
        ValidatorUtils.validateEntity(qo, HouseFavoriteQO.addFavorite.class);
        qo.setUid(UserUtils.getUserId());
        return CommonResult.ok(iHouseFavoriteService.houseFavorite(qo),"收藏成功!");
    }


    @Login
    @PostMapping("/shop")
    @ApiOperation("商铺收藏列表")
    public CommonResult<List<ShopLeaseVO>> shopFavorite(@RequestBody BaseQO<HouseFavoriteQO> qo) {
        ValidatorUtils.validatePageParam(qo);
        if( qo.getQuery() == null ){
            qo.setQuery(new HouseFavoriteQO());
        }
        qo.getQuery().setUid(UserUtils.getUserId());
        return CommonResult.ok(iHouseFavoriteService.shopFavorite(qo));
    }


    @Login
    @PostMapping("/lease")
    @ApiOperation("租房收藏列表")
    public CommonResult<List<ShopLeaseVO>> leaseFavorite(@RequestBody BaseQO<HouseFavoriteQO> qo) {
        ValidatorUtils.validatePageParam(qo);
        if( qo.getQuery() == null ){
            qo.setQuery(new HouseFavoriteQO());
        }
        qo.getQuery().setUid(UserUtils.getUserId());
        return CommonResult.ok(iHouseFavoriteService.leaseFavorite(qo));
    }


    @Login
    @DeleteMapping()
    @ApiOperation("删除我的收藏")
    public CommonResult<Boolean> deleteFavorite(@RequestParam Long id) {
        return CommonResult.ok(iHouseFavoriteService.deleteFavorite(id, UserUtils.getUserId()), "删除成功!");
    }

}
