package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.HouseValid;
import com.jsy.community.annotation.RequireRecentBrowse;
import com.jsy.community.annotation.UploadImg;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IHouseLeaseService;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.UploadBucketConst;
import com.jsy.community.constant.UploadRedisConst;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseLeaseQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.HouseVo;
import com.jsy.community.vo.lease.HouseLeaseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @author YuLF
 * @since  2021/1/13 17:33
 */
@Slf4j
@ApiJSYController
@RestController
@Api(tags = "房屋租售控制器")
@RequestMapping("/house")
public class HouseLeaseController {

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private IHouseLeaseService iHouseLeaseService;

    @Resource
    private  ShopLeaseController shopLeaseController;


    @Login
    @GetMapping("/allCommunity")
    @ApiOperation("获取用户所有小区")
    public CommonResult<List<CommunityEntity>> allCommunity(@RequestParam Long cityid){
        return CommonResult.ok(iHouseLeaseService.allCommunity( cityid, UserUtils.getUserId() ));
    }


    @Login
    @PostMapping("/wholeLease")
    @ApiOperation("整租新增")
    @HouseValid( validationInterface = HouseLeaseQO.AddWholeLeaseHouse.class)
    @UploadImg(redisKeyName = UploadRedisConst.HOUSE_IMG_ALL,attributeName = "houseImage")
    public CommonResult<Boolean> addWholeLeaseHouse(@RequestBody HouseLeaseQO houseLeaseQo) {
        return iHouseLeaseService.addWholeLeaseHouse(houseLeaseQo) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }

    @Login
    @PostMapping("/singleRoom")
    @ApiOperation("单间新增")
    @HouseValid( validationInterface = HouseLeaseQO.AddSingleRoomLeaseHouse.class)
    @UploadImg(redisKeyName = UploadRedisConst.HOUSE_IMG_ALL,attributeName = "houseImage")
    public CommonResult<Boolean> addSingleLeaseHouse(@RequestBody HouseLeaseQO houseLeaseQo) {
        return iHouseLeaseService.addSingleLeaseHouse(houseLeaseQo) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }


    @Login
    @PostMapping("/combineLease")
    @ApiOperation("合租新增")
    @HouseValid( validationInterface = HouseLeaseQO.AddCombineLeaseHouse.class)
    @UploadImg(redisKeyName = UploadRedisConst.HOUSE_IMG_ALL,attributeName = "houseImage")
    public CommonResult<Boolean> addCombineLeaseHouse(@RequestBody HouseLeaseQO houseLeaseQo) {
        return iHouseLeaseService.addCombineLeaseHouse(houseLeaseQo) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }


    @Login
    @PutMapping("/wholeLease")
    @HouseValid(validationInterface = HouseLeaseQO.UpdateWholeLeaseHouse.class)
    @ApiOperation("整租更新")
    public CommonResult<Boolean> updateWholeLease(@RequestBody HouseLeaseQO qo) {
        return iHouseLeaseService.updateWholeLease(qo) ? CommonResult.ok("更新成功!") : CommonResult.error("更新失败!");
    }


    @Login
    @PutMapping("/singleRoom")
    @HouseValid(validationInterface = HouseLeaseQO.UpdateWholeLeaseHouse.class)
    @ApiOperation("单间更新")
    public CommonResult<Boolean> updateSingleRoom(@RequestBody HouseLeaseQO qo) {
        return iHouseLeaseService.updateSingleRoom(qo) ? CommonResult.ok("更新成功!") : CommonResult.error("更新失败!");
    }

    @Login
    @PutMapping("/combineLease")
    @HouseValid(validationInterface = HouseLeaseQO.UpdateWholeLeaseHouse.class)
    @ApiOperation("合租更新")
    public CommonResult<Boolean> updateCombineLease(@RequestBody HouseLeaseQO qo) {
        return iHouseLeaseService.updateCombineLease(qo) ? CommonResult.ok("更新成功!") : CommonResult.error("更新失败!");
    }


    /**
     * 在用户点击编辑房屋时，回显数据接口
     * @param houseId       房屋id
     */
    @Login
    @GetMapping("/editDetails")
    @ApiOperation("编辑房屋详情")
    public CommonResult<HouseLeaseVO> editDetails(@RequestParam Long houseId) {
        return CommonResult.ok(iHouseLeaseService.editDetails(houseId, UserUtils.getUserId()));
    }


    @Login
    @DeleteMapping()
    @ApiOperation("删除房屋租售")
    public CommonResult<Boolean> delLeaseHouse(@RequestParam Long id) {
        return iHouseLeaseService.delLeaseHouse(id, UserUtils.getUserId()) ? CommonResult.ok() : CommonResult.error(1, "数据不存在");
    }


    @GetMapping("/latest")
    @ApiOperation("最新房屋详情")
    public CommonResult<?> leaseDetails(@RequestParam Long id, @RequestParam Boolean leaseHouse) {
        if( id == null || leaseHouse == null){
            throw new JSYException(JSYError.BAD_REQUEST);
        }
        if(leaseHouse){
            //房屋出租详情
            return houseLeaseDetails(id);
        }
        //商铺转让详情
        return shopLeaseController.getShop(id);
    }


    @Login
    @PostMapping("/page")
    @Cacheable( value = "lease:house", key = "#baseQo", unless = "#result.data == null or #result.data.size() == 0", cacheManager = "redisCacheManager")
    @ApiOperation("分页查询房屋出租数据")
    public CommonResult<List<HouseLeaseVO>> queryHouseLeaseByList(@RequestBody BaseQO<HouseLeaseQO> baseQo) {
        ValidatorUtils.validatePageParam(baseQo);
        if( baseQo.getQuery() == null ){
            baseQo.setQuery(new HouseLeaseQO());
        }
        return CommonResult.ok(iHouseLeaseService.queryHouseLeaseByList(baseQo));
    }





    @Login
    @GetMapping("/details")
    @ApiOperation("查询房屋出租数据单条详情")
    @RequireRecentBrowse
    public CommonResult<HouseLeaseVO> houseLeaseDetails(@RequestParam Long houseId) {
        return CommonResult.ok(iHouseLeaseService.queryHouseLeaseOne(houseId, UserUtils.getUserId()),"查询成功!");
    }


    /**
     *
     * @param cid   社区id
     */
    @Login
    @GetMapping("/ownerHouse")
    @ApiOperation("查询业主当前社区拥有房屋")
    public CommonResult<List<HouseVo>> ownerHouse(@RequestParam Long cid){
        return CommonResult.ok(iHouseLeaseService.ownerHouse(UserUtils.getUserId(), cid));
    }


    @Login
    @PostMapping("/ownerHouseImages")
    @ApiOperation("房屋图片批量上传接口")
    @UploadImg(bucketName = UploadBucketConst.HOUSE_BUCKET,redisKeyName = UploadRedisConst.HOUSE_IMG_PART)
    public CommonResult<String[]> ownerHouseImages(MultipartFile[] houseImages,CommonResult<String[]> commonResult){
        if( houseImages == null || houseImages.length == 0 ){
            throw new JSYException(JSYError.BAD_REQUEST);
        }
        String[] data = commonResult.getData();
        return CommonResult.ok(data);
    }


    @Login
    @PostMapping("/ownerLeaseHouse")
    @ApiOperation("查询业主已发布的房源")
    public CommonResult<List<HouseLeaseVO>> ownerLeaseHouse(@RequestBody BaseQO<HouseLeaseQO> qo){
        ValidatorUtils.validatePageParam(qo);
        if( qo.getQuery() == null ){
            qo.setQuery(new HouseLeaseQO());
        }
        qo.getQuery().setUid(UserUtils.getUserId());
        return CommonResult.ok(iHouseLeaseService.ownerLeaseHouse(qo));
    }



    /**
     * TODO : 分词查询
     */
    @Login
    @PostMapping("/search")
    @ApiOperation("按小区名或标题或地址搜索房屋")
    public CommonResult<List<HouseLeaseVO>> searchLeaseHouse(@RequestBody BaseQO<HouseLeaseQO> qo){
        ValidatorUtils.validateEntity(qo.getQuery(), HouseLeaseQO.SearchLeaseHouse.class);
        ValidatorUtils.validatePageParam(qo);
        return CommonResult.ok(iHouseLeaseService.searchLeaseHouse(qo));
    }
}
