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
     * ???????????????????????????????????????
     *
     * @param baseQO ???????????????????????????
     * @return
     */
    @Override
    public PageInfo<AssetLeaseRecordEntity> queryLeaseReleasePage(BaseQO<LeaseReleasePageQO> baseQO) {
        LeaseReleasePageQO query = baseQO.getQuery();
        Page<AssetLeaseRecordEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        // ?????????
        if (query.getCommunityId() != null) {
            queryWrapper.eq("community_id", query.getCommunityId());
        }
        // ???????????????
        if (query.getType() != null) {
            queryWrapper.eq("asset_type", query.getType());
        }
        // ?????????
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
        // ??????????????????
        if (StringUtils.isNotBlank(query.getPhone()) || StringUtils.isNotBlank(query.getNickName())) {
            PageVO<UserDetail> pageVO = userInfoRpcService.queryUser(query.getPhone(), query.getNickName(), 0, 9999);
            Set<String> uid = pageVO.getData().stream().map(UserDetail::getAccount).collect(Collectors.toSet());
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
        // ??????????????????
        Set<Long> collect = records.stream().map(AssetLeaseRecordEntity::getCommunityId).collect(Collectors.toSet());
        List<CommunityEntity> communityList = communityMapper.selectBatchIds(collect);
        Map<Long, CommunityEntity> communityMap = communityList.stream().collect(Collectors.toMap(CommunityEntity::getId, Function.identity()));
        // ??????????????????map
        Set<Long> assetIds = records.stream().map(AssetLeaseRecordEntity::getAssetId).collect(Collectors.toSet());
        List<HouseLeaseEntity> houseLeaseEntityList = houseLeaseMapper.selectList(new QueryWrapper<HouseLeaseEntity>().in("id", assetIds));
        List<ShopLeaseEntity> shopLeaseEntityList = shopLeaseMapper.selectList(new QueryWrapper<ShopLeaseEntity>().in("id", assetIds));
        Map<Long, Integer> houseLeaseStatusMap = houseLeaseEntityList.stream().collect(Collectors.toMap(HouseLeaseEntity::getId, HouseLeaseEntity::getLeaseStatus));
        Map<Long, Integer> shopLeaseStatusMap = shopLeaseEntityList.stream().collect(Collectors.toMap(ShopLeaseEntity::getId, ShopLeaseEntity::getLeaseStatus));
        houseLeaseStatusMap.putAll(shopLeaseStatusMap);
        // ???????????????????????????
        Set<String> uids = records.stream().map(AssetLeaseRecordEntity::getTenantUid).collect(Collectors.toSet());
//        Set<Long> uidList = uids.stream().map(Long::parseLong).collect(Collectors.toSet());
        List<RealUserDetail> realUserDetailList = userInfoRpcService.getRealUserDetails(uids);
        Map<String, RealUserDetail> realUserDetailMap = realUserDetailList.stream().collect(Collectors.toMap(RealUserDetail::getAccount, Function.identity()));
    
        // ??????????????????
        records.stream().peek(r -> {
            // ??????????????????
            r.setTypeName(r.getAssetType() == 1 ? "??????" : "????????????");
            // ??????????????????
            if (r.getCommunityId() != null) {
                CommunityEntity communityEntity = communityMap.get(r.getCommunityId());
                if (communityEntity != null) {
                    r.setCommunityName(communityEntity.getName());
                }
            }
            r.setIdStr(String.valueOf(r.getId()));
            // ??????????????????
            if (!CollectionUtils.isEmpty(houseLeaseStatusMap)) {
                r.setLeaseStatusName(leaseStatus(houseLeaseStatusMap.get(r.getAssetId())));
            }
            // ???????????????????????????
            r.setRealName(realUserDetailMap.get(r.getTenantUid()).getNickName());
            r.setTenantPhone(realUserDetailMap.get(r.getTenantUid()).getPhone());
        }).collect(Collectors.toList());
        
//        // ????????????
//        Page<LeaseReleasePageVO> pageData = leaseOperationRecordMapper.queryLeaseReleasePage(query, page);
//        if (pageData.getRecords().size() == 0) {
//            return new PageInfo<>();
//        }
//        // ??????????????????
//        List<LeaseReleasePageVO> records = pageData.getRecords();
//        Set<Long> collect = records.stream().map(LeaseReleasePageVO::getTCommunityId).collect(Collectors.toSet());
//        List<CommunityEntity> communityList = communityMapper.selectBatchIds(collect);
//        Map<Long, CommunityEntity> communityMap = communityList.stream().collect(Collectors.toMap(CommunityEntity::getUserId, Function.identity()));
//        // ????????????Id
//        // ??????Id??????
//        List<Long> assetId = records.stream().map(LeaseReleasePageVO::getUserId).collect(Collectors.toList());
//        // ????????????id???????????????????????????
//        Map<Long, List<String>> assetIdAndConIdMap = assetLeaseRecordService.queryConIdList(assetId);
//        // ??????????????????
//        records.stream().peek(r -> {
//            // ??????????????????
//            if (r.getTCommunityId() != null) {
//                CommunityEntity communityEntity = communityMap.get(r.getTCommunityId());
//                if (communityEntity != null) {
//                    r.setCommunity(communityEntity.getName());
//                }
//            }
//            r.setIdStr(String.valueOf(r.getUserId()));
//            if (!CollectionUtils.isEmpty(assetIdAndConIdMap)) {
//                r.setConId(assetIdAndConIdMap.get(r.getUserId()));
//            }
//            // ??????????????????
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
        return leaseTypeI == 0 ? "?????????" : "?????????";
    }


    @Override
    public LeaseReleaseInfoVO queryLeaseHouseInfo(Long id, Integer type) {
        String typeStr = BusinessEnum.HouseTypeEnum.getName(type);
        if (StringUtils.isEmpty(typeStr)) {
            throw new AdminException("type???????????????");
        }
        AssetLeaseRecordEntity assetLeaseRecordEntity = assetLeaseRecordMapper.selectById(id);
        LeaseReleaseInfoVO result;
        if ("??????".equals(typeStr)) {
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
     * ??????????????????????????????
     */
    private LeaseReleaseInfoVO shopToLeaseInfo(ShopLeaseEntity shop) {
        LeaseReleaseInfoVO info = new LeaseReleaseInfoVO();
        info.setId(shop.getId());
        info.setHouseTitle(shop.getTitle());
        info.setHouseLeasemodeId(2);
        //??????????????????
        info.setHousePrice(shop.getMonthMoney());
        info.setHouseUnit("???");
        info.setHouseCommunityId(shop.getCommunityId());
        //????????????
        info.setHouseFloor(shop.getFloor());
        //????????????
        info.setHouseSquareMeter(BigDecimal.valueOf(shop.getShopAcreage()).setScale(2, RoundingMode.HALF_UP));
        //??????????????????
        info.setRoomFacilitiesId(shop.getShopFacility());
        //??????????????????
        //??????????????????
        info.setHouseIntroduce(shop.getSummarize());
        info.setAppellation(shop.getNickname());
        info.setHouseContact(shop.getMobile());
        return info;
    }

    private void buildOtherProperty(LeaseReleaseInfoVO releaseInfo, String type) {
        // ????????????
        releaseInfo.setLeaseType(leaseType(releaseInfo.getHouseLeasemodeId()));
        //????????????
        releaseInfo.setHouseLeaseMode(houseLeaseMode(releaseInfo.getHouseLeasedepositId(), 1));
        //????????????
        if (releaseInfo.getHousePrice() != null) {
            String price = releaseInfo.getHousePrice().setScale(2, RoundingMode.HALF_UP).toString();
            if ("??????".equals(type)) {
                releaseInfo.setPriceStr(price + "/???");
            } else {
                releaseInfo.setPriceStr(price + "/" + releaseInfo.getHouseUnit());
            }
        }
        //????????????
        if (releaseInfo.getHouseCommunityId() != null) {
            CommunityEntity communityEntity = communityMapper.selectById(releaseInfo.getHouseCommunityId());
            if (communityEntity != null) {
                releaseInfo.setCommunityAddress(communityEntity.getDetailAddress());
            }
        }
        // ????????????
        releaseInfo.setHouseType(type);
        //??????
        releaseInfo.setTypeCodeStr(houseType(releaseInfo.getHouseTypeCode()));
        //??????
        releaseInfo.setDirection(direction(releaseInfo.getHouseDirectionId()));
        //????????????
        releaseInfo.setDecorationType(decorationType(releaseInfo.getDecorationTypeId()));
        if ("??????".equals(type)) {
            // ???????????????
            List<String> imgUrls = shopLeaseMapper.queryAllShopImg(releaseInfo.getId());
            releaseInfo.setImgUrl(imgUrls);
        } else {
            //????????????
            releaseInfo.setRoomFacilities(houseConst(releaseInfo.getRoomFacilitiesId(), 24));
            //????????????
            releaseInfo.setHouseAdvantage(houseConst(releaseInfo.getHouseAdvantageId(), 4));
            //????????????
            releaseInfo.setLeaseRequire(houseConst(releaseInfo.getLeaseRequireId(), 21));
            //????????????
            if (releaseInfo.getHouseImageId() != null) {
                List<String> imgUrls = houseLeaseMapper.queryHouseAllImgById(releaseInfo.getHouseImageId());
                releaseInfo.setImgUrl(imgUrls);
            }
        }
    }

    /**
     * ????????????
     */
    private static String leaseType(Integer lease) {
        if (lease == null) {
            return "";
        }
        switch (lease) {
            case 2:
                return "??????";
            case 4:
                return "??????";
            case 8:
                return "??????";
            default:
                return "??????";
        }
    }

    /**
     * ????????????
     */
    private List<String> houseConst(Long houseAdvantageId, Integer type) {
        if (houseAdvantageId == null) {
            return new ArrayList<>();
        }
        List<HouseLeaseConstEntity> houseLeaseConstEntities = queryHouseConst(type, Long.parseLong(houseAdvantageId.toString()));
        return houseLeaseConstEntities.stream().map(HouseLeaseConstEntity::getHouseConstName).collect(Collectors.toList());
    }

    /**
     * ????????????
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
     * ??????
     */
    private static String houseType(String code) {
        if (StringUtils.isEmpty(code) || code.length() < 1) {
            return "";
        }
        if ("000000".equals(code)) {
            return "??????";
        }
        Integer num1 = Integer.parseInt(code.substring(0, 2));
        int num2 = Integer.parseInt(code.substring(2, 4));
        int num3 = Integer.parseInt(code.substring(4, 6));
        return num1 + "???" + num2 + "???" + num3 + "???";
    }

    /**
     * ??????
     */
    private static String direction(String houseDirectionId) {
        if (StringUtils.isEmpty(houseDirectionId)) {
            return "";
        }
        return BusinessEnum.HouseDirectionEnum.getDirectionName(Integer.parseInt(houseDirectionId));
    }

    /**
     * ????????????
     */
    public String decorationType(Long decorationTypeId) {
        if (decorationTypeId == null) {
            return "";
        }
        if (decorationTypeId.equals(4L)) {
            return "????????????";
        } else if (decorationTypeId.equals(2L)) {
            return "?????????";
        } else {
            return "????????????";
        }
    }

    /**
     * ???????????????????????????
     *
     * @param type ???????????? 1.?????????????????? 2.???????????? 3.?????????????????? 4.?????????????????? 5.?????? 6.??????
     *             7.???????????? 8.???????????? 9.???????????? 10.???????????? 11.???????????? 12.??????????????? ?????????
     * @param code ????????????????????????????????? ?????????code??????2???????????????
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
     * ?????????????????????
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
