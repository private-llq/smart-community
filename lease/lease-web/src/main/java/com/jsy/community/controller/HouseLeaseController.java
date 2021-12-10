package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.HouseValid;
import com.jsy.community.annotation.RequireRecentBrowse;
import com.jsy.community.annotation.UploadImg;
import com.jsy.community.api.AssetLeaseRecordService;
import com.jsy.community.api.IHouseLeaseService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.UploadBucketConst;
import com.jsy.community.constant.UploadRedisConst;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.proprietor.AssetLeaseRecordEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseLeaseQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.utils.es.Operation;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.HouseVo;
import com.jsy.community.vo.lease.HouseLeaseSimpleVO;
import com.jsy.community.vo.lease.HouseLeaseVO;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author YuLF
 * @since 2021/1/13 17:33
 */
@Slf4j
// @ApiJSYController
@RestController
@Api(tags = "房屋租售控制器")
@RequestMapping("/house")
public class HouseLeaseController {

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private IHouseLeaseService iHouseLeaseService;

    @Resource
    private ShopLeaseController shopLeaseController;

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private AssetLeaseRecordService assetLeaseRecordService;
    
    @GetMapping("/allCommunity")
    @ApiOperation("获取用户所有小区")
    // @Permit("community:lease:house:allCommunity")
    public CommonResult<List<CommunityEntity>> allCommunity(@RequestParam Long cityid) {
        return CommonResult.ok(iHouseLeaseService.allCommunity(cityid, UserUtils.getUserId()));
    }

    /**
     * @author: Pipi
     * @description: 获取用户所有小区
     * @return: com.jsy.community.vo.CommonResult<java.util.List<com.jsy.community.entity.CommunityEntity>>
     * @date: 2021/10/13 11:32
     **/
    @GetMapping("/v2/allCommunity")
    // @Permit("community:lease:house:v2:allCommunity")
    public CommonResult<List<CommunityEntity>> allCommunity() {
        return CommonResult.ok(iHouseLeaseService.allCommunity(UserUtils.getUserId()));
    }

    @PostMapping("/wholeLease")
    @ApiOperation("整租新增")
    @HouseValid(validationInterface = HouseLeaseQO.AddWholeLeaseHouse.class, operation = Operation.INSERT)
    @UploadImg(redisKeyName = UploadRedisConst.HOUSE_IMG_ALL, attributeName = "houseImage")
    // @Permit("community:lease:house:wholeLease")
    public CommonResult<Boolean> addWholeLeaseHouse(@RequestBody HouseLeaseQO houseLeaseQo) {
        return iHouseLeaseService.addWholeLeaseHouse(houseLeaseQo) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }
    
    @PostMapping("/singleRoom")
    @ApiOperation("单间新增")
    @HouseValid(validationInterface = HouseLeaseQO.AddSingleRoomLeaseHouse.class, operation = Operation.INSERT)
    @UploadImg(redisKeyName = UploadRedisConst.HOUSE_IMG_ALL, attributeName = "houseImage")
    // @Permit("community:lease:house:singleRoom")
    public CommonResult<Boolean> addSingleLeaseHouse(@RequestBody HouseLeaseQO houseLeaseQo) {
        return iHouseLeaseService.addSingleLeaseHouse(houseLeaseQo) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }
    
    @PostMapping("/combineLease")
    @ApiOperation("合租新增")
    @HouseValid(validationInterface = HouseLeaseQO.AddCombineLeaseHouse.class, operation = Operation.INSERT)
    @UploadImg(redisKeyName = UploadRedisConst.HOUSE_IMG_ALL, attributeName = "houseImage")
    // @Permit("community:lease:house:combineLease")
    public CommonResult<Boolean> addCombineLeaseHouse(@RequestBody HouseLeaseQO houseLeaseQo) {
        return iHouseLeaseService.addCombineLeaseHouse(houseLeaseQo) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }
    
    @PutMapping("/wholeLease")
    @HouseValid(validationInterface = HouseLeaseQO.UpdateWholeLeaseHouse.class, operation = Operation.UPDATE)
    @ApiOperation("整租更新")
    // @Permit("community:lease:house:wholeLease")
    public CommonResult<Boolean> updateWholeLease(@RequestBody HouseLeaseQO qo) {
        return iHouseLeaseService.updateWholeLease(qo) ? CommonResult.ok("更新成功!") : CommonResult.error("更新失败!");
    }

    @PutMapping("/singleRoom")
    @HouseValid(validationInterface = HouseLeaseQO.UpdateWholeLeaseHouse.class, operation = Operation.UPDATE)
    @ApiOperation("单间更新")
    // @Permit("community:lease:house:singleRoom")
    public CommonResult<Boolean> updateSingleRoom(@RequestBody HouseLeaseQO qo) {
        return iHouseLeaseService.updateSingleRoom(qo) ? CommonResult.ok("更新成功!") : CommonResult.error("更新失败!");
    }

    @PutMapping("/combineLease")
    @HouseValid(validationInterface = HouseLeaseQO.UpdateWholeLeaseHouse.class, operation = Operation.UPDATE)
    @ApiOperation("合租更新")
    // @Permit("community:lease:house:combineLease")
    public CommonResult<Boolean> updateCombineLease(@RequestBody HouseLeaseQO qo) {
        return iHouseLeaseService.updateCombineLease(qo) ? CommonResult.ok("更新成功!") : CommonResult.error("更新失败!");
    }


    /**
     * 在用户点击编辑房屋时，回显数据接口
     *
     * @param houseId 房屋id
     */
    @GetMapping("/editDetails")
    @ApiOperation("编辑房屋详情")
    // @Permit("community:lease:house:editDetails")
    public CommonResult<HouseLeaseVO> editDetails(@RequestParam Long houseId) {
        return CommonResult.ok(iHouseLeaseService.editDetails(houseId, UserUtils.getUserId()));
    }
    
    @DeleteMapping()
    @ApiOperation("删除房屋租售")
    // @Permit("community:lease:house")
    public CommonResult<Boolean> delLeaseHouse(@RequestParam Long id) {
        return iHouseLeaseService.delLeaseHouse(id, UserUtils.getUserId()) ? CommonResult.ok() : CommonResult.error(1, "数据不存在");
    }

    @LoginIgnore
    @GetMapping("/latest")
    @ApiOperation("最新房屋详情")
    // @Permit("community:lease:house:latest")
    public CommonResult<?> leaseDetails(@RequestParam Long id, @RequestParam Boolean leaseHouse) {
        if (id == null || leaseHouse == null) {
            throw new JSYException(JSYError.BAD_REQUEST);
        }
        if (leaseHouse) {
            //房屋出租详情
            return houseLeaseDetails(id);
        }
        //商铺转让详情
        return shopLeaseController.getShop(id);
    }
    
    @PostMapping("/page")
//    @Cacheable( value = "lease:house", key = "#baseQo", unless = "#result.data == null or #result.data.size() == 0", cacheManager = "redisCacheManager")
    @ApiOperation("分页查询房屋出租数据")
    // @Permit("community:lease:house:page")
    @LoginIgnore({"00000tourist"})
    public CommonResult<List<HouseLeaseVO>> queryHouseLeaseByList(@RequestBody BaseQO<HouseLeaseQO> baseQo) {
        ValidatorUtils.validatePageParam(baseQo);
        if (baseQo.getQuery() == null) {
            baseQo.setQuery(new HouseLeaseQO());
        }
        return CommonResult.ok(iHouseLeaseService.queryHouseLeaseByList(baseQo));
    }
    
    @GetMapping("/details")
    @ApiOperation("查询房屋出租数据单条详情")
    @RequireRecentBrowse
    // @Permit("community:lease:house:details")
    @LoginIgnore({"00000tourist"})
    public CommonResult<HouseLeaseVO> houseLeaseDetails(@RequestParam Long houseId) {
        return CommonResult.ok(iHouseLeaseService.queryHouseLeaseOne(houseId, UserUtils.getUserId()), "查询成功!");
    }


    /**
     * @param cid 社区id
     */
    @GetMapping("/ownerHouse")
    @ApiOperation("查询业主当前社区拥有房屋")
    // @Permit("community:lease:house:ownerHouse")
    public CommonResult<List<HouseVo>> ownerHouse(@RequestParam Long cid) {
        return CommonResult.ok(iHouseLeaseService.ownerHouse(UserUtils.getUserId(), cid));
    }

    @PostMapping("/ownerHouseImages")
    @ApiOperation("房屋图片批量上传接口")
    @UploadImg(bucketName = UploadBucketConst.HOUSE_BUCKET, redisKeyName = UploadRedisConst.HOUSE_IMG_PART)
    // @Permit("community:lease:house:ownerHouseImages")
    public CommonResult<String[]> ownerHouseImages(MultipartFile[] houseImages, CommonResult<String[]> commonResult) {
        if (houseImages == null || houseImages.length == 0) {
            throw new JSYException(JSYError.BAD_REQUEST);
        }
        String[] data = commonResult.getData();
        return CommonResult.ok(data);
    }

    @PostMapping("/ownerLeaseHouse")
    @ApiOperation("查询业主已发布的房源")
    // @Permit("community:lease:house:ownerLeaseHouse")
    public CommonResult<List<HouseLeaseVO>> ownerLeaseHouse(@RequestBody BaseQO<HouseLeaseQO> qo) {
        ValidatorUtils.validatePageParam(qo);
        if (qo.getQuery() == null) {
            qo.setQuery(new HouseLeaseQO());
        }
        qo.getQuery().setUid(UserUtils.getUserId());
        return CommonResult.ok(iHouseLeaseService.ownerLeaseHouse(qo));
    }


    /**
     * TODO : 分词查询
     */
    @PostMapping("/search")
    @ApiOperation("按小区名或标题或地址搜索房屋")
    // @Permit("community:lease:house:search")
    public CommonResult<List<HouseLeaseVO>> searchLeaseHouse(@RequestBody BaseQO<HouseLeaseQO> qo) {
        ValidatorUtils.validateEntity(qo.getQuery(), HouseLeaseQO.SearchLeaseHouse.class);
        ValidatorUtils.validatePageParam(qo);
        return CommonResult.ok(iHouseLeaseService.searchLeaseHouse(qo));
    }

    /**
     * @Author: Pipi
     * @Description: 查询房屋出租数据单条简略详情
     * @param: houseId: 出租房屋主键
     * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.vo.lease.HouseLeaseSimpleVO>
     * @Date: 2021/3/27 16:14
     **/
    @GetMapping("/simpleDetail")
    @ApiOperation("查询房屋出租数据单条简略详情")
    // @Permit("community:lease:house:simpleDetail")
    public CommonResult<HouseLeaseSimpleVO> houseLeaseSimpleDetails(@RequestParam Long houseId) {
        return CommonResult.ok(iHouseLeaseService.queryHouseLeaseSimpleDetail(houseId), "查询成功!");
    }

    /**
     * @param assetLeaseRecordEntity: 房屋租赁记录表实体
     * @author: Pipi
     * @description: 租赁发起签约
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/8/31 15:18
     **/
    @PostMapping("/v2/initContract")
    // @Permit("community:lease:house:v2:initContract")
    public CommonResult initContract(@RequestBody AssetLeaseRecordEntity assetLeaseRecordEntity) {
        ValidatorUtils.validateEntity(assetLeaseRecordEntity, AssetLeaseRecordEntity.InitContractValidate.class);
        assetLeaseRecordEntity.setTenantUid(UserUtils.getUserId());
        return  CommonResult.ok(assetLeaseRecordService.addLeaseRecord(assetLeaseRecordEntity), "申请成功!");
    }

    /**
     * @author: Pipi
     * @description: 对签约进行操作(租客取消申请/房东拒绝申请/租客再次申请/房东接受申请/拟定合同)
     * @param assetLeaseRecordEntity: 房屋租赁记录表实体
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/9/3 10:27
     **/
    @PostMapping("/v2/operationContract")
    // @Permit("community:lease:house:v2:operationContract")
    public CommonResult operationContract(@RequestBody AssetLeaseRecordEntity assetLeaseRecordEntity) {
        if (assetLeaseRecordEntity.getId() == null) {
            throw new JSYException(400, "签约ID不能为空");
        }
        ValidatorUtils.validateEntity(assetLeaseRecordEntity, AssetLeaseRecordEntity.OperationContractValidate.class);
        Integer integer = assetLeaseRecordService.operationContract(assetLeaseRecordEntity, UserUtils.getUserId());
        return integer > 0 ? CommonResult.ok("操作成功") : CommonResult.error("操作失败!");
    }

    /**
     * @author: Pipi
     * @description: 查询我的签约列表
     * @param assetLeaseRecordEntity: 查询条件
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/9/2 14:36
     **/
    @PostMapping("/v2/contractList")
    // @Permit("community:lease:house:v2:contractList")
    public CommonResult contractList(@RequestBody AssetLeaseRecordEntity assetLeaseRecordEntity) {
        ValidatorUtils.validateEntity(assetLeaseRecordEntity, AssetLeaseRecordEntity.ContractListValidate.class);
        if (assetLeaseRecordEntity.getIdentityType() == 1 && assetLeaseRecordEntity.getAssetType() == null) {
            throw new JSYException(400, "当身份为房东时,资产类型不能为空");
        }
        return CommonResult.ok(assetLeaseRecordService.pageContractList(assetLeaseRecordEntity, UserUtils.getUserId()));
    }

    /**
     * @author: Pipi
     * @description: 房东查看单个资产的签约列表
     * @param assetLeaseRecordEntity: 查询条件
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/9/6 14:50
     **/
    @PostMapping("/v2/landlordContractList")
    // @Permit("community:lease:house:v2:landlordContractList")
    public CommonResult landlordContractList(@RequestBody AssetLeaseRecordEntity assetLeaseRecordEntity) {
        ValidatorUtils.validateEntity(assetLeaseRecordEntity, AssetLeaseRecordEntity.LandlordContractListValidate.class);
        assetLeaseRecordEntity.setHomeOwnerUid(UserUtils.getUserId());
        return CommonResult.ok(assetLeaseRecordService.landlordContractList(assetLeaseRecordEntity));
    }

    /**
     * @author: Pipi
     * @description: 查询签约详情
     * @param assetLeaseRecordEntity: 查询条件
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/9/6 17:32
     **/
    @PostMapping("/v2/contractDetail")
    // @Permit("community:lease:house:v2:contractDetail")
    public CommonResult contractDetail(@RequestBody AssetLeaseRecordEntity assetLeaseRecordEntity) {
        if (assetLeaseRecordEntity.getId() == null) {
            throw new JSYException(400, "签约ID不能为空");
        }
        ValidatorUtils.validateEntity(assetLeaseRecordEntity, AssetLeaseRecordEntity.ContractDetailValidate.class);
        AssetLeaseRecordEntity assetLeaseRecordEntity1 = assetLeaseRecordService.contractDetail(assetLeaseRecordEntity, UserUtils.getUserId());
        return assetLeaseRecordEntity1 == null ? CommonResult.error("未查到相关签约") : CommonResult.ok(assetLeaseRecordEntity1);
    }

    /**
     * @author: Pipi
     * @description: 签章调用相关操作(区块链上链成功通知:4、完成签约:6、发起签约/重新发起:31、取消发起:32)
     * @param assetLeaseRecordEntity: 签约实体
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/9/7 10:17
     **/
    @LoginIgnore
    @PostMapping("/v2/signatureOperationContract")
    // @Permit("community:lease:house:v2:signatureOperationContract")
    public CommonResult signatureOperationContract(@RequestBody AssetLeaseRecordEntity assetLeaseRecordEntity) {
        if (assetLeaseRecordEntity.getOperationType() == null ||
                (assetLeaseRecordEntity.getOperationType() != BusinessEnum.ContractingProcessStatusEnum.LANDLORD_INITIATED_CONTRACT.getCode()
                        && assetLeaseRecordEntity.getOperationType() != BusinessEnum.ContractingProcessStatusEnum.COMPLETE_CONTRACT.getCode()
                        && assetLeaseRecordEntity.getOperationType() != BusinessEnum.ContractingProcessStatusEnum.CANCEL_LAUNCH.getCode()
                        && assetLeaseRecordEntity.getOperationType() != 4
                )
        ) {
            throw new JSYException(400, "签章操作只能是区块链上链成功通知:4、完成签约:6、发起签约/重新发起:31、取消发起:32");
        }
        if (assetLeaseRecordEntity.getOperationType() == BusinessEnum.ContractingProcessStatusEnum.LANDLORD_INITIATED_CONTRACT.getCode()) {
            if (assetLeaseRecordEntity.getId() == null) {
                throw new JSYException(400, "签约ID不能为空");
            }
            // 发起签约/重新发起
            ValidatorUtils.validateEntity(assetLeaseRecordEntity, AssetLeaseRecordEntity.LandlordInitiatedContractValidate.class);
        } else if (assetLeaseRecordEntity.getOperationType() == BusinessEnum.ContractingProcessStatusEnum.CANCEL_LAUNCH.getCode()) {
            // 房东取消发起签约
            ValidatorUtils.validateEntity(assetLeaseRecordEntity, AssetLeaseRecordEntity.CancelContractValidate.class);
        } else if (assetLeaseRecordEntity.getOperationType() == BusinessEnum.ContractingProcessStatusEnum.COMPLETE_CONTRACT.getCode()) {
            // 完成签约
            ValidatorUtils.validateEntity(assetLeaseRecordEntity, AssetLeaseRecordEntity.CompleteContractValidate.class);
        } else if (assetLeaseRecordEntity.getOperationType() == 4) {
            // 区块链上链成功通知
            ValidatorUtils.validateEntity(assetLeaseRecordEntity, AssetLeaseRecordEntity.BlockchainSuccessfulValidate.class);
        }
        return CommonResult.ok(assetLeaseRecordService.signatureOperation(assetLeaseRecordEntity));
    }

    /**
     * @author: Pipi
     * @description: 查询拟定合同需要的预填参数
     * @param assetLeaseRecordEntity:
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/10/27 16:23
     **/
    @PostMapping("/v2/queryContractPreFillInfo")
    // @Permit("community:lease:house:v2:queryContractPreFillInfo")
    public CommonResult queryContractPreFillInfo(@RequestBody AssetLeaseRecordEntity assetLeaseRecordEntity) {
        if (assetLeaseRecordEntity.getId() == null) {
            throw new JSYException(400, "签约ID不能为空");
        }
        return CommonResult.ok(assetLeaseRecordService.queryContractPreFillInfo(assetLeaseRecordEntity));
    }
}