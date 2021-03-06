package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.jsy.community.api.*;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommonConst;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.proprietor.AssetLeaseRecordEntity;
import com.jsy.community.entity.shop.ShopImgEntity;
import com.jsy.community.entity.shop.ShopLeaseEntity;
import com.jsy.community.mapper.AssetLeaseRecordMapper;
import com.jsy.community.mapper.ShopImgMapper;
import com.jsy.community.mapper.ShopLeaseMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseLeaseQO;
import com.jsy.community.qo.shop.ShopQO;
import com.jsy.community.utils.CommonUtils;
import com.jsy.community.utils.MyMathUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.es.ElasticsearchImportProvider;
import com.jsy.community.utils.es.Operation;
import com.jsy.community.utils.es.RecordFlag;
import com.jsy.community.vo.shop.IndexShopVO;
import com.jsy.community.vo.shop.ShopDetailsVO;
import com.jsy.community.vo.shop.ShopLeaseVO;
import com.jsy.community.vo.shop.UserShopLeaseVO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * ???????????????
 * </p>
 *
 * @author lihao
 * @since 2020-12-17
 */
@DubboService(version = Const.version, group = Const.group_lease)
public class ShopLeaseServiceImpl extends ServiceImpl<ShopLeaseMapper, ShopLeaseEntity> implements IShopLeaseService {
    /**
     * @Author lihao
     * @Description ??????????????????
     * @Date 2021/1/13 15:49
     **/
    private static final Integer HEAD_MAX = 3;

    /**
     * @Author lihao
     * @Description ?????????????????????
     * @Date 2021/1/13 15:50
     **/
    private static final Integer MIDDLE_MAX = 8;

    /**
     * @Author lihao
     * @Description ?????????????????????
     * @Date 2021/1/13 15:50
     **/
    private static final Integer OTHER_MAX = 8;

    /**
     * @Author lihao
     * @Description ???????????????  ?????????????????? XX???
     * @Date 2021/1/13 15:55
     **/
    private static final double NORM_MONEY = 10000.00;

    /**
     * @Author lihao
     * @Description ???????????????  ?????????????????? ??????
     * @Date 2021/1/13 15:55
     **/
    private static final double MIN_MONEY = 0.00;

    /**
     * @Author lihao
     * @Description ?????????????????????????????????
     * @Date 2021/2/7 15:55
     **/
    private static final Integer IMG_MAX = 19;

    @Resource
    private ShopLeaseMapper shopLeaseMapper;

    @Resource
    private ShopImgMapper shopImgMapper;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICommunityService communityService;

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private IHouseLeaseService iHouseLeaseService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private PropertyUserService userService;

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private IHouseConstService houseConstService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICommonConstService commonConstService;

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private ILeaseUserService leaseUserService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AssetLeaseRecordMapper assetLeaseRecordMapper;

    /**
     * @Author lihao
     * @Description ?????????????????????
     * @Date 2021/1/13 16:04
     **/
    private static final Long SHOP_TYPE = 1L;

    /**
     * @Author lihao
     * @Description ?????????????????????
     * @Date 2021/1/13 16:11
     **/
    private static final Long SHOP_BUSINESS = 9L;

    /**
     * @Author lihao
     * @Description ?????????????????????
     * @Date 2021/1/14 9:42
     **/
    private static final Short SHOP_SOURCE = 3;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addShop(ShopQO shop) {
        // ????????????????????????
        ShopLeaseEntity baseShop = new ShopLeaseEntity();
        BeanUtils.copyProperties(shop, baseShop);
        baseShop.setId(SnowFlake.nextId());

        List<Long> facilityCodes = shop.getShopFacilityList();
        // ?????????????????????Code
        long facilityCode = MyMathUtils.getTypeCode(facilityCodes);
        baseShop.setShopFacility(facilityCode);

        List<Long> peopleTypeCodes = shop.getShopPeoples();
        // ?????????????????????Code
        long peopleCode = MyMathUtils.getTypeCode(peopleTypeCodes);
        baseShop.setShopPeople(peopleCode);

        //?????????????????????
        CommunityEntity community = communityService.getCommunityNameById(baseShop.getCommunityId());
        baseShop.setLon(community.getLon());
        baseShop.setLat(community.getLat());
        shopLeaseMapper.insert(baseShop);

        // ????????????????????????
        String[] imgPath = shop.getImgPath();
        if (imgPath != null && imgPath.length != 0) {
            List<ShopImgEntity> list = new ArrayList<>();
            for (String s : imgPath) {
                ShopImgEntity shopImgEntity = new ShopImgEntity();
                shopImgEntity.setId(SnowFlake.nextId());
                shopImgEntity.setShopId(baseShop.getId());
                shopImgEntity.setImgUrl(s);
                list.add(shopImgEntity);
            }
            shopImgMapper.insertImg(list);
        }
        ElasticsearchImportProvider.elasticOperationSingle(baseShop.getId(), RecordFlag.LEASE_SHOP, Operation.INSERT, baseShop.getTitle(), CommonUtils.isEmpty(imgPath) ? null : imgPath[0]);
    }

    @Override
    public Map<String, Object> getShop(Long shopId, String uid) {
        Map<String, Object> map = new HashMap<>();


        ShopLeaseEntity shop = shopLeaseMapper.selectByShopId(shopId);

        if (shop == null) {
            return null;
        }
        ShopLeaseVO shopLeaseVo = new ShopLeaseVO();
        // ??????????????????
        BeanUtils.copyProperties(shop, shopLeaseVo);
        CommunityEntity communityNameById = communityService.getCommunityNameById(shop.getCommunityId());
        if (communityNameById != null) {
            String area = (String) redisTemplate.opsForValue().get("RegionSingle:" + shop.getAreaId());
            area = area == null ? "" : area;
            String communityName = communityNameById.getName() == null ? "" : communityNameById.getName();
            shopLeaseVo.setCommunityAddress(area + communityName);
        }
        // ???????????????????????????????????????
        QueryWrapper<AssetLeaseRecordEntity> assetLeaseRecordEntityQueryWrapper = new QueryWrapper<>();
        assetLeaseRecordEntityQueryWrapper.eq("tenant_uid", uid);
        assetLeaseRecordEntityQueryWrapper.eq("asset_id", shopId);
        assetLeaseRecordEntityQueryWrapper.eq("asset_type", BusinessEnum.HouseTypeEnum.SHOP.getCode());
        assetLeaseRecordEntityQueryWrapper.and(
                wapper -> wapper.ne("operation", BusinessEnum.ContractingProcessStatusEnum.COMPLETE_CONTRACT.getCode())
                        .or(newwapper ->
                                newwapper.eq("operation", BusinessEnum.ContractingProcessStatusEnum.COMPLETE_CONTRACT.getCode())
                                        .gt("end_date", new Date())
                        )
        );
        AssetLeaseRecordEntity assetLeaseRecordEntity = assetLeaseRecordMapper.selectOne(assetLeaseRecordEntityQueryWrapper);
        if (assetLeaseRecordEntity == null) {
            shopLeaseVo.setOperation(0);
        } else {
            shopLeaseVo.setOperation(assetLeaseRecordEntity.getOperation());
            shopLeaseVo.setContractId(assetLeaseRecordEntity.getIdStr());
        }


        QueryWrapper<ShopImgEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("shop_id", shopId);
        List<ShopImgEntity> imgs = shopImgMapper.selectList(queryWrapper);
        List<String> strings = new ArrayList<>();
        for (ShopImgEntity img : imgs) {
            strings.add(img.getImgUrl());
        }
        //???list??????????????????????????????????????????????????????????????????????????????list????????????????????????
        //Object[] objects = strings.toArray();
        //???????????????Object[] ?????????String[]?????????????????????????????????????????????????????????
        // java????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        String[] imgPath = strings.toArray(new String[strings.size()]);
        // ??????????????????
        shopLeaseVo.setImgPath(imgPath);

        // ??????????????????
        Long shopFacility = shop.getShopFacility();
        List<Long> facilityCode = MyMathUtils.analysisTypeCode(shopFacility);
        List<String> constByTypeCodeForString = houseConstService.getConstByTypeCodeForString(facilityCode, 16L);
        if (!CollectionUtils.isEmpty(constByTypeCodeForString)) {
            shopLeaseVo.setShopFacilityStrings(constByTypeCodeForString);
        }

        // ????????????
        Long shopTypeId = shop.getShopTypeId();
        CommonConst type = commonConstService.getConstById(shopTypeId);
        if (type != null) {
            shopLeaseVo.setShopTypeString(type.getConstName());
        }

        // ????????????
        Long shopBusinessId = shop.getShopBusinessId();
        CommonConst business = commonConstService.getConstById(shopBusinessId);
        if (business != null) {
            shopLeaseVo.setShopBusinessString(business.getConstName());
        }

        // ??????????????????
        Long shopPeople = shop.getShopPeople();
        List<Long> peopleCode = MyMathUtils.analysisTypeCode(shopPeople);
        List<String> constByPeopleCodeForString = houseConstService.getConstByTypeCodeForString(peopleCode, 17L);
        if (!CollectionUtils.isEmpty(constByPeopleCodeForString)) {
            shopLeaseVo.setShopPeopleStrings(constByPeopleCodeForString);
        }

        map.put("shop", shopLeaseVo);

        // ?????????????????????????????? ??????????????????
        List<String> facilityStrings = shopLeaseVo.getShopFacilityStrings();
        List<String> peopleStrings = shopLeaseVo.getShopPeopleStrings();
        ArrayList<String> list = new ArrayList<>();
        // ????????????????????????????????????
        if (facilityStrings != null) {
            list.addAll(peopleStrings);
            list.addAll(facilityStrings);
            shopLeaseVo.setTags(list);
        }

        // ????????????
        Integer source = shop.getSource();
        Map<Integer, String> kv = BusinessEnum.SourceEnum.getKv();
        String s = kv.get(source);
        shopLeaseVo.setSourceString(s);

        // ????????????
        Integer status = shop.getStatus();
        if (status == 0) {
            shopLeaseVo.setStatusString("?????????");
        } else {
            shopLeaseVo.setStatusString("?????????");
        }

        // ??????????????????   // TODO: 2021/2/19 ??????????????????????????????    ???????????????
        Long areaId = shop.getAreaId();
        String area = redisTemplate.opsForValue().get("RegionSingle" + ":" + areaId);

        Long communityId = shop.getCommunityId();
        CommunityEntity community = communityService.getCommunityNameById(communityId);
        if (area == null) {
            area = "";
        }
        shopLeaseVo.setShopAddress(area + "  " + community.getName());


        // ???????????????????????????????????????
        UserEntity one = userService.selectOne(shop.getUid());
        // ????????????????????????????????????????????????????????????????????????
        // ??????????????????????????????????????????????????????????????????
        one.setRealName(shop.getNickname());
        one.setMobile(shop.getMobile());
        //?????????????????????id(im_id)
        String imId = leaseUserService.queryIMIdByUid(shop.getUid());
        one.setImId(imId);
        map.put("user", one);
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateShop(ShopQO shop) {
        ShopLeaseEntity shopLeaseEntity = new ShopLeaseEntity();
        shopLeaseEntity.setId(shop.getShopId());
        List<Long> shopFacilityList = shop.getShopFacilityList();
        List<Long> shopPeoples = shop.getShopPeoples();

        BeanUtils.copyProperties(shop, shopLeaseEntity);

        // ?????????????????????Code
        long facilityCode = MyMathUtils.getTypeCode(shopFacilityList);
        long peopleCode = MyMathUtils.getTypeCode(shopPeoples);
        shopLeaseEntity.setShopFacility(facilityCode);
        shopLeaseEntity.setShopPeople(peopleCode);
        // ??????????????????
//		shopLeaseEntity.setUpdateTime(null);
        shopLeaseMapper.updateById(shopLeaseEntity);

        QueryWrapper<ShopImgEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("shop_id", shop.getShopId());
        List<ShopImgEntity> shopImgList = shopImgMapper.selectList(wrapper);
        if (!CollectionUtils.isEmpty(shopImgList)) {
            List<Long> longs = new ArrayList<>();
            for (ShopImgEntity shopImgEntity : shopImgList) {
                longs.add(shopImgEntity.getId());
            }
            // ??????????????????
            shopImgMapper.deleteBatchIds(longs);
        }

        String[] imgPath = shop.getImgPath();
        if (imgPath != null && imgPath.length > 0) {
            List<ShopImgEntity> imgList = new ArrayList<>();
            for (String s : imgPath) {
                ShopImgEntity entity = new ShopImgEntity();
                entity.setId(SnowFlake.nextId());
                entity.setImgUrl(s);
                entity.setShopId(shop.getShopId());
                imgList.add(entity);
            }
            // ??????????????????
            shopImgMapper.insertImg(imgList);
        }
        ElasticsearchImportProvider.elasticOperationSingle(shop.getShopId(), RecordFlag.LEASE_SHOP, Operation.UPDATE, shop.getTitle(), CommonUtils.isEmpty(imgPath) ? null : imgPath[0]);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelShop(String userId, Long shopId) {
        // ??????????????????
        shopLeaseMapper.deleteById(shopId);

        // ????????????????????????
        QueryWrapper<ShopImgEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("shop_id", shopId);
        List<ShopImgEntity> shopImgList = shopImgMapper.selectList(wrapper);
        if (!CollectionUtils.isEmpty(shopImgList)) {
            List<Long> longs = new ArrayList<>();
            for (ShopImgEntity shopImgEntity : shopImgList) {
                longs.add(shopImgEntity.getId());
            }
            // ??????????????????
            shopImgMapper.deleteBatchIds(longs);
        }
        ElasticsearchImportProvider.elasticOperationSingle(shopId, RecordFlag.LEASE_SHOP, Operation.DELETE, null, null);
    }


    @Override
    public List<Map<String, Object>> listShop(String userId) {
        QueryWrapper<ShopLeaseEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", userId).orderByDesc("create_time");
        List<Map<String, Object>> maps = new ArrayList<>();
        List<ShopLeaseEntity> list = shopLeaseMapper.selectList(wrapper);
        for (ShopLeaseEntity shopLeaseEntity : list) {
            HashMap<String, Object> map = new HashMap<>();
            Integer status = shopLeaseEntity.getStatus();
            if (0 == (status)) {
                shopLeaseEntity.setStatusString("?????????");
            }
            if (1 == (status)) {
                shopLeaseEntity.setStatusString("?????????");
            }
            map.put("shopLease", shopLeaseEntity);

            QueryWrapper<ShopImgEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("shop_id", shopLeaseEntity.getId());
            List<ShopImgEntity> shopImgEntities = shopImgMapper.selectList(queryWrapper);
            if (!CollectionUtils.isEmpty(shopImgEntities)) {
                map.put("shopImg", shopImgEntities.get(0).getImgUrl());
            }

            maps.add(map);
        }
        return maps;
    }

    /**
     * @return com.jsy.community.utils.PageInfo<com.jsy.community.vo.shop.IndexShopVO>
     * @Author lihao
     * @Description ???shopLeaseEntity?????????IndexShopVO
     * @Date 2021/1/5 23:47
     * @Param [page, shopVOS]
     **/
    private PageInfo<IndexShopVO> commonCode(Page<ShopLeaseEntity> page, List<IndexShopVO> shopVOS) {
        List<ShopLeaseEntity> records = page.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return null;
        }
        // ??????????????????IndexShopVO
        for (ShopLeaseEntity record : records) {
            IndexShopVO indexShopVO = new IndexShopVO();
            BeanUtils.copyProperties(record, indexShopVO);

            // ??????id
            Long id = record.getId();

            // ????????????
            QueryWrapper<ShopImgEntity> imgWrapper = new QueryWrapper<>();
            imgWrapper.eq("shop_id", id);
            List<ShopImgEntity> shopImgEntities = shopImgMapper.selectList(imgWrapper);
            if (!CollectionUtils.isEmpty(shopImgEntities)) {
                for (ShopImgEntity imgEntity : shopImgEntities) {
                    String imgUrl = imgEntity.getImgUrl();
                    if (imgUrl.contains("shop-head")) {
                        indexShopVO.setImgPath(imgUrl);
                    }
                    break;
                }
            }

            // ??????????????????
            // ??????????????????
            Long shopFacility = record.getShopFacility();
            // ??????????????????
            Long shopPeople = record.getShopPeople();

            // ????????????????????????????????????????????????
            List<Long> facilityList = MyMathUtils.analysisTypeCode(shopFacility);
            // ????????????????????????Code
            List<Long> facilityCodes = new ArrayList<>();
            if (!CollectionUtils.isEmpty(facilityList)) {
                facilityCodes.addAll(facilityList);
            }
            List<String> facilitys = houseConstService.getConstByTypeCodeForString(facilityCodes, 16L);

            // ????????????????????????????????????????????????
            List<Long> peopleList = MyMathUtils.analysisTypeCode(shopPeople);
            // ????????????????????????Code
            ArrayList<Long> peopleCodes = new ArrayList<>();
            if (!CollectionUtils.isEmpty(peopleList)) {
                peopleCodes.addAll(peopleList);
            }
            List<String> peoples = houseConstService.getConstByTypeCodeForString(peopleCodes, 17L);

            // ???2??????????????????1?????????
            ArrayList<String> list = new ArrayList<>();
            // ????????????????????????????????????
            if (facilitys != null) {
                list.addAll(peoples);
                list.addAll(facilitys);
                indexShopVO.setTags(list);
            }
            shopVOS.add(indexShopVO);


            // ????????????
            // ????????????
            Long areaId = record.getAreaId();
            String area = redisTemplate.opsForValue().get("RegionSingle" + ":" + areaId);

            Long communityId = record.getCommunityId();
            CommunityEntity community = communityService.getCommunityNameById(communityId);

            indexShopVO.setAddress(area + "  " + community.getName());
        }
        PageInfo<IndexShopVO> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(page, pageInfo);
        pageInfo.setRecords(shopVOS);
        return pageInfo;
    }

    @Override
    public PageInfo<IndexShopVO> getShopBySearch(BaseQO<ShopLeaseEntity> baseQO, String query, Integer areaId) {
        Page<ShopLeaseEntity> page = new Page<>(baseQO.getPage(), baseQO.getSize());
        QueryWrapper<ShopLeaseEntity> queryWrapper = new QueryWrapper<>();

        List<Long> longs = new ArrayList<>();
        List<IndexShopVO> shopVOS = new ArrayList<>();
        // ???????????????
        if (!StringUtils.isEmpty(query)) {

            List<CommunityEntity> list = communityService.listCommunityByName(query, areaId);
            for (CommunityEntity communityEntity : list) {
                longs.add(communityEntity.getId());
            }
            if (!CollectionUtils.isEmpty(longs)) {
                queryWrapper.in("community_id", longs);
                shopLeaseMapper.selectPage(page, queryWrapper);
            }

            List<ShopLeaseEntity> records = page.getRecords();
            for (ShopLeaseEntity record : records) {
                IndexShopVO indexShopVO = new IndexShopVO();
                Long id = record.getId();

                // ????????????
                QueryWrapper<ShopImgEntity> wrapper = new QueryWrapper<>();
                wrapper.eq("shop_id", id).last("limit 1");
                ShopImgEntity shopImgEntity = shopImgMapper.selectOne(wrapper);
                indexShopVO.setImgPath(shopImgEntity.getImgUrl());

                // ??????????????????
                Long[] tags = shopLeaseMapper.selectTags(id);
                List<String> constNameByConstId = houseConstService.getConstNameByConstId(tags);
                indexShopVO.setTags(constNameByConstId);

                BeanUtils.copyProperties(record, indexShopVO);
                shopVOS.add(indexShopVO);
            }

            PageInfo<IndexShopVO> pageInfo = new PageInfo<>();
            BeanUtils.copyProperties(page, pageInfo);
            pageInfo.setRecords(shopVOS);
            return pageInfo;
        }

        // ???????????????
        // ??????????????????????????????
        List<CommunityEntity> list = communityService.listCommunityByAreaId(areaId.longValue());
        for (CommunityEntity communityEntity : list) {
            longs.add(communityEntity.getId());
        }
        queryWrapper.in("community_id", longs);
        shopLeaseMapper.selectPage(page, queryWrapper);
        // ?????????????????????????????????
        List<ShopLeaseEntity> records = page.getRecords();

        // ??????????????????
        for (ShopLeaseEntity record : records) {
            IndexShopVO indexShopVO = new IndexShopVO();
            BeanUtils.copyProperties(record, indexShopVO);

            Long id = record.getId();

            // ????????????
            QueryWrapper<ShopImgEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("shop_id", id).last("limit 1");
            ShopImgEntity shopImgEntity = shopImgMapper.selectOne(wrapper);
            if (shopImgEntity != null) {
                indexShopVO.setImgPath(shopImgEntity.getImgUrl());
            }

            // ??????????????????
            Long[] tags = shopLeaseMapper.selectTags(id);
            if (tags != null && tags.length > 0) {
                List<String> constNameByConstId = houseConstService.getConstNameByConstId(tags);
                indexShopVO.setTags(constNameByConstId);
            }
            shopVOS.add(indexShopVO);
        }

        PageInfo<IndexShopVO> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(page, pageInfo);
        pageInfo.setRecords(shopVOS);
        return pageInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public void testTransaction() {

        // 1. ??????????????????
        communityService.addCommunityEntity();
        int b = 1 / 0;
        // 2. ??????????????????
        ShopLeaseEntity shopLeaseEntity = new ShopLeaseEntity();
        shopLeaseEntity.setId(140L);
        shopLeaseEntity.setTitle("?????????????????????");
        shopLeaseMapper.insert(shopLeaseEntity);

        System.out.println(11);


    }

    @Override
    public Map<String, Object> moreOption() {
        // 1. ????????????????????????
        List<CommonConst> typeList = commonConstService.getShopType();

        // 2. ????????????????????????
        List<CommonConst> businessList = commonConstService.getBusiness();

        HashMap<String, Object> map = new HashMap<>();
        map.put("type", typeList);
        map.put("business", businessList);

        // 3. ??????   ?????????????????????????????????
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", 1);
        hashMap.put("type", "??????");

        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("id", 2);
        hashMap2.put("type", "??????");

        HashMap<String, Object> hashMap3 = new HashMap<>();
        hashMap3.put("id", 3);
        hashMap3.put("type", "??????");

        List<Map<String, Object>> maps = new ArrayList<>();
        maps.add(hashMap);
        maps.add(hashMap2);
        maps.add(hashMap3);
        map.put("source", maps);


        return map;
    }

    @Override
    public Map<String, Object> getPublishTags() {
        // 1. ????????????????????????
        List<CommonConst> typeList = commonConstService.getShopType();
        // ?????????????????????[????????????????????????????????????]
        List<CommonConst> types = new ArrayList<>();
        for (CommonConst commonConst : typeList) {
            if (("??????").equals(commonConst.getConstName())) {
                continue;
            }
            types.add(commonConst);
        }

        // 2. ????????????????????????
        List<CommonConst> businessList = commonConstService.getBusiness();
        // ?????????????????????[????????????????????????????????????]
        List<CommonConst> business = new ArrayList<>();
        for (CommonConst commonConst : businessList) {
            if (("??????").equals(commonConst.getConstName())) {
                continue;
            }
            business.add(commonConst);
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("type", types);
        map.put("business", business);

        return map;
    }

    @Override
    public List<CommunityEntity> getCommunity(Long areaId) {
        return communityService.listCommunityByAreaId(areaId);
    }

    @Override
    public ShopDetailsVO getShopForUpdate(Long shopId) {
        ShopLeaseEntity entity = shopLeaseMapper.selectById(shopId);
        if (entity == null) {
            throw new LeaseException("???????????????????????????");
        }
        ShopDetailsVO detailsVO = new ShopDetailsVO();
        BeanUtils.copyProperties(entity, detailsVO);

        // ??????id????????????????????????
        Long communityId = entity.getCommunityId();
        CommunityEntity communityNameById = communityService.getCommunityNameById(communityId);
        detailsVO.setCommunity(communityNameById.getName());
        // ????????????
        Long shopTypeId = entity.getShopTypeId();
        CommonConst constType = commonConstService.getConstById(shopTypeId);
        detailsVO.setShopType(constType.getConstName());
        // ????????????
        Long shopBusinessId = entity.getShopBusinessId();
        CommonConst constBusiness = commonConstService.getConstById(shopBusinessId);
        if (constBusiness != null) {
            detailsVO.setShopBusiness(constBusiness.getConstName());
        }
        // ????????????
        QueryWrapper<ShopImgEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("shop_id", entity.getId()).select("img_url");
        List<ShopImgEntity> shopImgEntities = shopImgMapper.selectList(wrapper);
        if (!CollectionUtils.isEmpty(shopImgEntities)) {
            List<String> head = new ArrayList<>();
            List<String> middle = new ArrayList<>();
            List<String> other = new ArrayList<>();
            for (ShopImgEntity shopImgEntity : shopImgEntities) {
                String imgUrl = shopImgEntity.getImgUrl();
                if (imgUrl.contains("shop-head-img")) {
                    head.add(imgUrl);
                } else if (imgUrl.contains("shop-middle-img")) {
                    middle.add(imgUrl);
                } else {
                    other.add(imgUrl);
                }
            }
            detailsVO.setHeadImg(head);
            detailsVO.setMiddleImg(middle);
            detailsVO.setOtherImg(other);
        }

        // ????????????Code
        Long shopFacility = entity.getShopFacility();
        List<Long> facilityList = MyMathUtils.analysisTypeCode(shopFacility);
        detailsVO.setShopFacilityList(facilityList);
        // ????????????Code
        Long shopPeople = entity.getShopPeople();
        List<Long> peopleList = MyMathUtils.analysisTypeCode(shopPeople);
        detailsVO.setShopPeoples(peopleList);
        return detailsVO;
    }

    @Override
    public List<UserShopLeaseVO> listUserShop(String userId) {
        QueryWrapper<ShopLeaseEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", userId).orderByDesc("create_time");
        List<ShopLeaseEntity> list = shopLeaseMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<UserShopLeaseVO>();
        }

        ArrayList<UserShopLeaseVO> userShopLeaseVOS = new ArrayList<>();
        for (ShopLeaseEntity shopLeaseEntity : list) {
            UserShopLeaseVO leaseVO = new UserShopLeaseVO();
            BeanUtils.copyProperties(shopLeaseEntity, leaseVO);

            Integer status = shopLeaseEntity.getStatus();
            if (0 == (status)) {
                leaseVO.setStatusString("?????????");
            }
            if (1 == (status)) {
                leaseVO.setStatusString("?????????");
            }


            // ????????????
            Long shopTypeId = shopLeaseEntity.getShopTypeId();
            CommonConst constById = commonConstService.getConstById(shopTypeId);
            if (constById != null) {
                leaseVO.setShopType(constById.getConstName());
            }

            // ????????????
            String city = shopLeaseEntity.getCity();
            String area = shopLeaseEntity.getArea();
            Long communityId = shopLeaseEntity.getCommunityId();
            CommunityEntity communityNameById = communityService.getCommunityNameById(communityId);
            String community = "";
            if (communityNameById != null) {
                community = communityNameById.getName();

            }
            leaseVO.setAddress(city + area + community);

            // ??????
            QueryWrapper<ShopImgEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("shop_id", shopLeaseEntity.getId());
            List<ShopImgEntity> shopImgEntities = shopImgMapper.selectList(queryWrapper);
            if (!CollectionUtils.isEmpty(shopImgEntities)) {
                leaseVO.setShopShowImg(shopImgEntities.get(0).getImgUrl());
            }

            userShopLeaseVOS.add(leaseVO);
        }
        return userShopLeaseVOS;
    }


    @Override
    public PageInfo<IndexShopVO> getShopByCondition(BaseQO<HouseLeaseQO> baseQO) {
        Long page = baseQO.getPage();
        Long size = baseQO.getSize();

        Page<ShopLeaseEntity> info = new Page<>(page, size);
        List<ShopLeaseEntity> shopList = shopLeaseMapper.getShopByCondition(baseQO, info);
        List<Long> communityIdList = shopList.stream().map(ShopLeaseEntity::getCommunityId).collect(Collectors.toList());
        List<CommunityEntity> communityEntityList = communityService.queryCommunityBatch(communityIdList);
        ArrayList<IndexShopVO> shopVOS = new ArrayList<>();
        for (ShopLeaseEntity shopLeaseEntity : shopList) {
            IndexShopVO indexShopVO = new IndexShopVO();
            BeanUtils.copyProperties(shopLeaseEntity, indexShopVO);
            for (CommunityEntity communityEntity : communityEntityList) {
                if (communityEntity.getId() == shopLeaseEntity.getCommunityId()) {
                    String area = redisTemplate.opsForValue().get("RegionSingle:" + shopLeaseEntity.getAreaId());
                    area = area == null ? "" : area;
                    indexShopVO.setCommunityAddress(area + communityEntity.getName());
                }
            }
            // ????????????
            QueryWrapper<ShopImgEntity> imgWrapper = new QueryWrapper<>();
            imgWrapper.eq("shop_id", shopLeaseEntity.getId());
            imgWrapper.orderByDesc("create_time");
            List<ShopImgEntity> shopImgEntities = shopImgMapper.selectList(imgWrapper);
            if (!CollectionUtils.isEmpty(shopImgEntities)) {
                for (ShopImgEntity imgEntity : shopImgEntities) {
                    String imgUrl = imgEntity.getImgUrl();
                    if (imgUrl.contains("shop-head")) {
                        indexShopVO.setImgPath(imgUrl);
                    }
                    break;
                }
            }

            // ????????????
            // ????????????
            Long areaId = shopLeaseEntity.getAreaId();
            String area = redisTemplate.opsForValue().get("RegionSingle" + ":" + areaId);
            if (StringUtils.isEmpty(area)) {
                area = "";
            }
            Long communityId = shopLeaseEntity.getCommunityId();
            CommunityEntity community = communityService.getCommunityNameById(communityId);
            indexShopVO.setAddress(area + "  " + community.getName());

            // ????????????
            if (shopLeaseEntity.getMonthMoney().doubleValue() > NORM_MONEY) {
                String s = String.format("%.2f", shopLeaseEntity.getMonthMoney().doubleValue() / NORM_MONEY) + "???";
                indexShopVO.setMonthMoneyString(s);
            } else if (shopLeaseEntity.getMonthMoney().compareTo(BigDecimal.valueOf(MIN_MONEY)) == 0) {
                String s = "??????";
                indexShopVO.setMonthMoneyString(s);
            } else {
                String s = "" + shopLeaseEntity.getMonthMoney();
                int i = s.lastIndexOf(".");
                String substring = s.substring(0, i) + "???";
                indexShopVO.setMonthMoneyString(substring);
            }


            // ??????????????????
            // ??????????????????
            Long shopFacility = shopLeaseEntity.getShopFacility();
            // ??????????????????
            Long shopPeople = shopLeaseEntity.getShopPeople();


            // ????????????????????????????????????????????????
            List<Long> facilityList = MyMathUtils.analysisTypeCode(shopFacility);
            // ????????????????????????Code
            List<Long> facilityCodes = new ArrayList<>();
            if (!CollectionUtils.isEmpty(facilityList)) {
                facilityCodes.addAll(facilityList);
            }
            List<String> facilitys = houseConstService.getConstByTypeCodeForString(facilityCodes, 16L);

            // ????????????????????????????????????????????????
            List<Long> peopleList = MyMathUtils.analysisTypeCode(shopPeople);
            // ????????????????????????Code
            ArrayList<Long> peopleCodes = new ArrayList<>();
            if (!CollectionUtils.isEmpty(peopleList)) {
                peopleCodes.addAll(peopleList);
            }
            List<String> peoples = houseConstService.getConstByTypeCodeForString(peopleCodes, 17L);

            // ???2??????????????????1?????????
            ArrayList<String> list = new ArrayList<>();
            // ????????????????????????????????????
            if (facilitys != null) {
                list.addAll(peoples);
                list.addAll(facilitys);
                indexShopVO.setTags(list);
            }

            shopVOS.add(indexShopVO);
        }

        PageInfo<IndexShopVO> objectPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(info, objectPageInfo);
        objectPageInfo.setRecords(shopVOS);
        return objectPageInfo;
    }
    
    /**
     * @Description: ??????communityIds?????????????????????
     * @author: DKS
     * @since: 2021/9/3 9:31
     * @Param: communityIdList
     * @return: Integer
     */
    @Override
    public Integer selectAllShopByCommunityIds(List<String> communityIdList) {
        return shopLeaseMapper.selectAllShopByCommunityIds(communityIdList);
    }
}
