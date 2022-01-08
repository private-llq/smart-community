package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.api.AssetLeaseRecordService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.HouseLeaseConstEntity;
import com.jsy.community.entity.lease.HouseLeaseEntity;
import com.jsy.community.entity.proprietor.AssetLeaseRecordEntity;
import com.jsy.community.entity.shop.ShopLeaseEntity;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.LeaseReleasePageQO;
import com.jsy.community.service.AdminException;
import com.jsy.community.service.LeaseReleaseService;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.admin.LeaseReleaseInfoVO;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.entity.RealUserDetail;
import com.zhsj.base.api.entity.UserDetail;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import com.zhsj.base.api.vo.PageVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LeaseReleaseServiceImpl implements LeaseReleaseService {

    @Resource
    private LeaseOperationRecordMapper leaseOperationRecordMapper;
    @Resource
    private AssetLeaseRecordMapper assetLeaseRecordMapper;
    @Resource
    private CommunityMapper communityMapper;
    @Resource
    private HouseLeaseMapper houseLeaseMapper;
    @Resource
    private ShopLeaseMapper shopLeaseMapper;
    @Resource
    private HouseConstMapper houseConstMapper;
    
    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private AssetLeaseRecordService assetLeaseRecordService;
    
    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
    private IBaseUserInfoRpcService userInfoRpcService;

    /**
     * 商铺和房屋租赁信息发布列表
     *
     * @param baseQO 分页条件和查询条件
     * @return
     */
    @Override
    public PageInfo<AssetLeaseRecordEntity> queryLeaseReleasePage(BaseQO<LeaseReleasePageQO> baseQO) {
        LeaseReleasePageQO query = baseQO.getQuery();
        Page<AssetLeaseRecordEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        // 查小区
        if (query.getCommunityId() != null) {
            queryWrapper.eq("community_id", query.getCommunityId());
        }
        // 查房屋类型
        if (query.getType() != null) {
            queryWrapper.eq("asset_type", query.getType());
        }
        // 查状态
        if (query.getLeaseStatus() != null) {
            List<HouseLeaseEntity> houseLeaseEntities = houseLeaseMapper.selectList(new QueryWrapper<HouseLeaseEntity>().eq("lease_status", query.getLeaseStatus()));
            List<Long> houseLeaseIds = houseLeaseEntities.stream().map(HouseLeaseEntity::getId).collect(Collectors.toList());
            List<ShopLeaseEntity> shopLeaseEntities = shopLeaseMapper.selectList(new QueryWrapper<ShopLeaseEntity>().eq("lease_status", query.getLeaseStatus()));
            List<Long> shopLeaseIds = shopLeaseEntities.stream().map(ShopLeaseEntity::getId).collect(Collectors.toList());
            houseLeaseIds.addAll(shopLeaseIds);
            if (!CollectionUtils.isEmpty(houseLeaseIds)) {
                queryWrapper.in("asset_id", houseLeaseIds);
            } else {
                queryWrapper.eq("asset_id", 0);
            }
        }
        // 查姓名、电话
        if (StringUtils.isNotBlank(query.getPhone()) || StringUtils.isNotBlank(query.getNickName())) {
            PageVO<UserDetail> pageVO = userInfoRpcService.queryUser(query.getPhone(), query.getNickName(), 0, 9999);
            Set<Long> uid = pageVO.getData().stream().map(UserDetail::getId).collect(Collectors.toSet());
            if (!CollectionUtils.isEmpty(uid)) {
                queryWrapper.in("tenant_uid", uid);
            } else {
                queryWrapper.eq("tenant_uid", 0);
            }
        }
        Page<AssetLeaseRecordEntity> pageData = assetLeaseRecordMapper.selectPage(page, queryWrapper);
        List<AssetLeaseRecordEntity> records = pageData.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return new PageInfo<>();
        }
        // 查询小区信息
        Set<Long> collect = records.stream().map(AssetLeaseRecordEntity::getCommunityId).collect(Collectors.toSet());
        List<CommunityEntity> communityList = communityMapper.selectBatchIds(collect);
        Map<Long, CommunityEntity> communityMap = communityList.stream().collect(Collectors.toMap(CommunityEntity::getId, Function.identity()));
        // 查询租赁状态map
        Set<Long> assetIds = records.stream().map(AssetLeaseRecordEntity::getAssetId).collect(Collectors.toSet());
        List<HouseLeaseEntity> houseLeaseEntityList = houseLeaseMapper.selectList(new QueryWrapper<HouseLeaseEntity>().in("id", assetIds));
        List<ShopLeaseEntity> shopLeaseEntityList = shopLeaseMapper.selectList(new QueryWrapper<ShopLeaseEntity>().in("id", assetIds));
        Map<Long, Integer> houseLeaseStatusMap = houseLeaseEntityList.stream().collect(Collectors.toMap(HouseLeaseEntity::getId, HouseLeaseEntity::getLeaseStatus));
        Map<Long, Integer> shopLeaseStatusMap = shopLeaseEntityList.stream().collect(Collectors.toMap(ShopLeaseEntity::getId, ShopLeaseEntity::getLeaseStatus));
        houseLeaseStatusMap.putAll(shopLeaseStatusMap);
        // 查询租户姓名和电话
        Set<String> uids = records.stream().map(AssetLeaseRecordEntity::getTenantUid).collect(Collectors.toSet());
        Set<Long> uidList = uids.stream().map(Long::parseLong).collect(Collectors.toSet());
        List<RealUserDetail> realUserDetailList = userInfoRpcService.getRealUserDetailsByUid(uidList);
        Map<Long, RealUserDetail> realUserDetailMap = realUserDetailList.stream().collect(Collectors.toMap(RealUserDetail::getId, Function.identity()));
    
        // 填充额外信息
        records.stream().peek(r -> {
            // 填充房源类型
            r.setTypeName(r.getAssetType() == 1 ? "商铺" : "普通住宅");
            // 填充小区信息
            if (r.getCommunityId() != null) {
                CommunityEntity communityEntity = communityMap.get(r.getCommunityId());
                if (communityEntity != null) {
                    r.setCommunityName(communityEntity.getName());
                }
            }
            r.setIdStr(String.valueOf(r.getId()));
            // 填充租赁状态
            if (!CollectionUtils.isEmpty(houseLeaseStatusMap)) {
                r.setLeaseStatusName(leaseStatus(houseLeaseStatusMap.get(r.getAssetId())));
            }
            // 填充租户姓名和电话
            r.setRealName(realUserDetailMap.get(Long.parseLong(r.getTenantUid())).getNickName());
            r.setTenantPhone(realUserDetailMap.get(Long.parseLong(r.getTenantUid())).getPhone());
        }).collect(Collectors.toList());
        
//        // 分页查询
//        Page<LeaseReleasePageVO> pageData = leaseOperationRecordMapper.queryLeaseReleasePage(query, page);
//        if (pageData.getRecords().size() == 0) {
//            return new PageInfo<>();
//        }
//        // 查询小区信息
//        List<LeaseReleasePageVO> records = pageData.getRecords();
//        Set<Long> collect = records.stream().map(LeaseReleasePageVO::getTCommunityId).collect(Collectors.toSet());
//        List<CommunityEntity> communityList = communityMapper.selectBatchIds(collect);
//        Map<Long, CommunityEntity> communityMap = communityList.stream().collect(Collectors.toMap(CommunityEntity::getId, Function.identity()));
//        // 查询合同Id
//        // 资产Id列表
//        List<Long> assetId = records.stream().map(LeaseReleasePageVO::getId).collect(Collectors.toList());
//        // 根据资产id查询对应的合同编号
//        Map<Long, List<String>> assetIdAndConIdMap = assetLeaseRecordService.queryConIdList(assetId);
//        // 填充额外信息
//        records.stream().peek(r -> {
//            // 填充小区信息
//            if (r.getTCommunityId() != null) {
//                CommunityEntity communityEntity = communityMap.get(r.getTCommunityId());
//                if (communityEntity != null) {
//                    r.setCommunity(communityEntity.getName());
//                }
//            }
//            r.setIdStr(String.valueOf(r.getId()));
//            if (!CollectionUtils.isEmpty(assetIdAndConIdMap)) {
//                r.setConId(assetIdAndConIdMap.get(r.getId()));
//            }
//            // 填充租赁状态
//            r.setLeaseStatus(leaseStatus(r.getTLeaseStatus()));
//        }).collect(Collectors.toList());
        PageInfo<AssetLeaseRecordEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageData, pageInfo);
        return pageInfo;
    }

    private static String leaseStatus(Integer leaseTypeI) {
        if (leaseTypeI == null) {
            return "";
        }
        return leaseTypeI == 0 ? "未出租" : "已出租";
    }


    @Override
    public LeaseReleaseInfoVO queryLeaseHouseInfo(Long id, Integer type) {
        String typeStr = BusinessEnum.HouseTypeEnum.getName(type);
        if (StringUtils.isEmpty(typeStr)) {
            throw new AdminException("type类型不符合");
        }
        AssetLeaseRecordEntity assetLeaseRecordEntity = assetLeaseRecordMapper.selectById(id);
        LeaseReleaseInfoVO result;
        if ("住宅".equals(typeStr)) {
            HouseLeaseEntity house = houseLeaseMapper.selectById(assetLeaseRecordEntity.getAssetId());
            if (house == null) {
                result = null;
            } else {
                result = JSONObject.parseObject(JSONObject.toJSONString(house), LeaseReleaseInfoVO.class);
            }
        } else {
            ShopLeaseEntity shop = shopLeaseMapper.selectById(assetLeaseRecordEntity.getAssetId());
            if (shop == null) {
                result = null;
            } else {
                result = shopToLeaseInfo(shop);
            }
        }
        if (result != null) {
            buildOtherProperty(result, typeStr);
        }
        return result;
    }

    /**
     * 商铺信息转返回的实体
     */
    private LeaseReleaseInfoVO shopToLeaseInfo(ShopLeaseEntity shop) {
        LeaseReleaseInfoVO info = new LeaseReleaseInfoVO();
        info.setId(shop.getId());
        info.setHouseTitle(shop.getTitle());
        info.setHouseLeasemodeId(2);
        //押付方式没有
        info.setHousePrice(shop.getMonthMoney());
        info.setHouseUnit("月");
        info.setHouseCommunityId(shop.getCommunityId());
        //户型没有
        info.setHouseFloor(shop.getFloor());
        //朝向没有
        info.setHouseSquareMeter(BigDecimal.valueOf(shop.getShopAcreage()).setScale(2, RoundingMode.HALF_UP));
        //装修情况没有
        info.setRoomFacilitiesId(shop.getShopFacility());
        //房屋优势没有
        //出租要求没有
        info.setHouseIntroduce(shop.getSummarize());
        info.setAppellation(shop.getNickname());
        info.setHouseContact(shop.getMobile());
        return info;
    }

    private void buildOtherProperty(LeaseReleaseInfoVO releaseInfo, String type) {
        // 租售方式
        releaseInfo.setLeaseType(leaseType(releaseInfo.getHouseLeasemodeId()));
        //押付方式
        releaseInfo.setHouseLeaseMode(houseLeaseMode(releaseInfo.getHouseLeasedepositId(), 1));
        //月租价格
        if (releaseInfo.getHousePrice() != null) {
            String price = releaseInfo.getHousePrice().setScale(2, RoundingMode.HALF_UP).toString();
            if ("商铺".equals(type)) {
                releaseInfo.setPriceStr(price + "/月");
            } else {
                releaseInfo.setPriceStr(price + "/" + releaseInfo.getHouseUnit());
            }
        }
        //小区地址
        if (releaseInfo.getHouseCommunityId() != null) {
            CommunityEntity communityEntity = communityMapper.selectById(releaseInfo.getHouseCommunityId());
            if (communityEntity != null) {
                releaseInfo.setCommunityAddress(communityEntity.getDetailAddress());
            }
        }
        // 房屋类型
        releaseInfo.setHouseType(type);
        //户型
        releaseInfo.setTypeCodeStr(houseType(releaseInfo.getHouseTypeCode()));
        //朝向
        releaseInfo.setDirection(direction(releaseInfo.getHouseDirectionId()));
        //装修情况
        releaseInfo.setDecorationType(decorationType(releaseInfo.getDecorationTypeId()));
        if ("商铺".equals(type)) {
            // 查商铺图片
            List<String> imgUrls = shopLeaseMapper.queryAllShopImg(releaseInfo.getId());
            releaseInfo.setImgUrl(imgUrls);
        } else {
            //房间设施
            releaseInfo.setRoomFacilities(houseConst(releaseInfo.getRoomFacilitiesId(), 24));
            //房屋亮点
            releaseInfo.setHouseAdvantage(houseConst(releaseInfo.getHouseAdvantageId(), 4));
            //出租要求
            releaseInfo.setLeaseRequire(houseConst(releaseInfo.getLeaseRequireId(), 21));
            //住宅图片
            if (releaseInfo.getHouseImageId() != null) {
                List<String> imgUrls = houseLeaseMapper.queryHouseAllImgById(releaseInfo.getHouseImageId());
                releaseInfo.setImgUrl(imgUrls);
            }
        }
    }

    /**
     * 租售方式
     */
    private static String leaseType(Integer lease) {
        if (lease == null) {
            return "";
        }
        switch (lease) {
            case 2:
                return "整租";
            case 4:
                return "合租";
            case 8:
                return "单间";
            default:
                return "不限";
        }
    }

    /**
     * 房屋亮点
     */
    private List<String> houseConst(Long houseAdvantageId, Integer type) {
        if (houseAdvantageId == null) {
            return new ArrayList<>();
        }
        List<HouseLeaseConstEntity> houseLeaseConstEntities = queryHouseConst(type, Long.parseLong(houseAdvantageId.toString()));
        return houseLeaseConstEntities.stream().map(HouseLeaseConstEntity::getHouseConstName).collect(Collectors.toList());
    }

    /**
     * 押付方式
     */
    private String houseLeaseMode(Integer leasedeposit, Integer type) {
        if (leasedeposit == null) {
            return "";
        }
        List<HouseLeaseConstEntity> houseLeaseConstEntities = queryHouseConst(type, Long.parseLong(leasedeposit.toString()));
        if (houseLeaseConstEntities.size() > 0) {
            return houseLeaseConstEntities.get(0).getHouseConstName();
        }
        return "";
    }

    /**
     * 户型
     */
    private static String houseType(String code) {
        if (StringUtils.isEmpty(code) || code.length() < 1) {
            return "";
        }
        if ("000000".equals(code)) {
            return "别墅";
        }
        Integer num1 = Integer.parseInt(code.substring(0, 2));
        int num2 = Integer.parseInt(code.substring(2, 4));
        int num3 = Integer.parseInt(code.substring(4, 6));
        return num1 + "室" + num2 + "厅" + num3 + "卫";
    }

    /**
     * 朝向
     */
    private static String direction(String houseDirectionId) {
        if (StringUtils.isEmpty(houseDirectionId)) {
            return "";
        }
        return BusinessEnum.HouseDirectionEnum.getDirectionName(Integer.parseInt(houseDirectionId));
    }

    /**
     * 房屋装修
     */
    public String decorationType(Long decorationTypeId) {
        if (decorationTypeId == null) {
            return "";
        }
        if (decorationTypeId.equals(4L)) {
            return "豪华装修";
        } else if (decorationTypeId.equals(2L)) {
            return "精装修";
        } else {
            return "简单装修";
        }
    }

    /**
     * 查询房屋的某些常量
     *
     * @param type 常量种类 1.房屋出租方式 2.房屋类型 3.房屋装修风格 4.房屋优势标签 5.租金 6.面积
     *             7.商铺类型 8.商铺行业 9.房屋来源 10.租房类型 11.租房方式 12.出租房类型 。。。
     * @param code 标识这个类型的常量标签 的唯一code，按2的倍数存储
     */
    public List<HouseLeaseConstEntity> queryHouseConst(Integer type, Long code) {
        if (code == null) {
            return new ArrayList<>();
        }
        List<Long> codes = accordingToResolution(code);
        if (codes.size() == 0) {
            return new ArrayList<>();
        }
        QueryWrapper<HouseLeaseConstEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("house_const_type", type).eq("deleted", 0).in("house_const_code", codes);
        return houseConstMapper.selectList(wrapper);
    }

    /**
     * 按位拆分长整数
     *
     * @param code
     * @return
     */
    public static List<Long> accordingToResolution(Long code) {
        if (code < 1) {
            return new ArrayList<>();
        }
        String codeStr = Long.toBinaryString(code);
        Set<Long> set = new HashSet<>();
        for (int i = 0; i < codeStr.length(); i++) {
            StringBuilder sb = new StringBuilder(codeStr.substring(i, i + 1));
            if ("0".equals(sb.toString())) {
                continue;
            }
            String lenStr = codeStr.substring(i + 1);
            sb.append("0".repeat(lenStr.length()));
            set.add(Long.parseLong(sb.toString(), 2));
        }
        return new ArrayList<>(set);
    }

}
