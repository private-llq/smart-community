package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.constant.Const;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseLeaseQO;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.lease.HouseLeaseVO;
import com.jsy.community.vo.HouseVo;
import com.jsy.community.api.IHouseLeaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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


    @Login
    @PostMapping()
    @ApiOperation("新增房屋租售")
    public CommonResult<Boolean> addLeaseHouse(@RequestBody HouseLeaseQO houseLeaseQo) {
        //新增参数常规效验
        ValidatorUtils.validateEntity(houseLeaseQo, HouseLeaseQO.AddLeaseSaleHouse.class);
        houseLeaseQo.setUid(UserUtils.getUserId());
        //验证所属社区所属用户房屋是否存在
        if(iHouseLeaseService.existUserHouse(UserUtils.getUserId(), houseLeaseQo.getHouseCommunityId(), houseLeaseQo.getHouseId())){
            throw new JSYException(JSYError.BAD_REQUEST.getCode(), "您在此处未登记房产!");
        }
        //参数效验完成 新增
        return iHouseLeaseService.addLeaseSaleHouse(houseLeaseQo) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }

    @Login
    @DeleteMapping()
    @ApiOperation("删除房屋租售")
    public CommonResult<Boolean> delLeaseHouse(@RequestParam Long id) {
        return iHouseLeaseService.delLeaseHouse(id, UserUtils.getUserId()) ? CommonResult.ok() : CommonResult.error(1, "数据不存在");
    }


    @Login
    @PostMapping("/page")
    @ApiOperation("分页查询房屋出租数据")
    public CommonResult<List<HouseLeaseVO>> queryHouseLeaseByList(@RequestBody BaseQO<HouseLeaseQO> baseQo) {
        ValidatorUtils.validatePageParam(baseQo);
        if( baseQo.getQuery() == null ){
            baseQo.setQuery(new HouseLeaseQO());
        }
        return CommonResult.ok(iHouseLeaseService.queryHouseLeaseByList(baseQo));
    }


    @Login
    @PostMapping("/update")
    @ApiOperation("更新房屋出租数据")
    public CommonResult<Boolean> houseLeaseUpdate(@RequestBody HouseLeaseQO houseLeaseQO) {
        ValidatorUtils.validateEntity(houseLeaseQO, HouseLeaseQO.UpdateLeaseSaleHouse.class);
        houseLeaseQO.setUid(UserUtils.getUserId());
        return CommonResult.ok(iHouseLeaseService.updateHouseLease(houseLeaseQO),"更新成功!");
    }



    @Login
    @GetMapping("/details")
    @ApiOperation("查询房屋出租数据单条详情")
    public CommonResult<HouseLeaseVO> houseLeaseDetails(@RequestParam Long houseId) {
        return CommonResult.ok(iHouseLeaseService.queryHouseLeaseOne(houseId, UserUtils.getUserId()),"查询成功!");
    }


    @Login
    @PostMapping("/ownerHouse")
    @ApiOperation("查询业主当前社区拥有房屋")
    public CommonResult<List<HouseVo>> ownerHouse(@RequestParam Long communityId){
        return CommonResult.ok(iHouseLeaseService.ownerHouse(UserUtils.getUserId(), communityId));
    }

    @Login
    @PostMapping("/ownerHouseImages")
    @ApiOperation("房屋图片批量上传接口")
    public CommonResult<String[]> ownerHouseImages(MultipartFile[] houseImages){
        if( houseImages == null || houseImages.length == 0 ){
            throw new JSYException(JSYError.BAD_REQUEST);
        }
        return CommonResult.ok(MinioUtils.uploadForBatch(houseImages, "house-lease-img"));
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
