package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.PropertyEnum;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.mapper.HouseMapper;
import com.jsy.community.mapper.HouseMemberMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.HouseQO;
import com.jsy.community.service.IHouseService;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 房屋信息 服务实现类
 * </p>
 *
 * @author DKS
 * @since 2021-10-21
 */
@Service
public class HouseServiceImpl extends ServiceImpl<HouseMapper, HouseEntity> implements IHouseService {

    @Resource
    private HouseMapper houseMapper;
    
    @Resource
    private HouseMemberMapper houseMemberMapper;

    /**
     * @Description: 查询楼栋、单元、房屋（改）(根据物业端原型)
     * @Param: [baseQO]
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseEntity>
     * @Author: chq459799974
     * @Date: 2021/3/12
     **/
    @Override
    public PageInfo<HouseEntity> queryHouse(BaseQO<HouseQO> baseQO) {
        HouseQO query = baseQO.getQuery();
        Page<HouseEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        QueryWrapper<HouseEntity> queryWrapper = new QueryWrapper<>();
        //是否查小区
        if (query.getCommunityId() != null) {
            queryWrapper.eq("community_id", query.getCommunityId());
        }
        //是否查房号/业主
        if (query.getDoorOrOwner() != null) {
            queryWrapper.like("door",query.getDoorOrOwner());
            queryWrapper.or().in("");
        }
        //楼栋和单元均不为空 只取单元id
        if (query.getBuildingId() != null && query.getUnitId() != null) {
            queryWrapper.eq("pid", query.getUnitId());
        } else if (query.getBuildingId() != null && 0L != query.getBuildingId()) {  //楼栋不为空，单元空  取楼栋id及其所有子级单元id
            List<Long> pidList = new ArrayList<>();
            //房屋直接挂楼栋
            pidList.add(query.getBuildingId());
            //房屋挂单元，查询单元idList并添加
            List<Long> unitIdList = houseMapper.getSubIdList(Arrays.asList(query.getBuildingId()));
            if (!CollectionUtils.isEmpty(unitIdList)) {
                pidList.addAll(unitIdList);
            }
            queryWrapper.in("pid", pidList);
        } else if (query.getUnitId() != null) {  //单元不为空，楼栋空 只取单元id
            queryWrapper.eq("pid", query.getUnitId());
        }
        queryWrapper.orderByDesc("create_time");
        Page<HouseEntity> pageData = houseMapper.selectPage(page, queryWrapper);
        if (CollectionUtils.isEmpty(pageData.getRecords())) {
            return new PageInfo<>();
        }
        //查询类型为楼栋，再查出已绑定单元数
        if (BusinessConst.BUILDING_TYPE_BUILDING == query.getType()) {
            List<Long> paramList = new ArrayList<>();
            for (HouseEntity houseEntity : pageData.getRecords()) {
                paramList.add(houseEntity.getId());
            }
            Map<Long, Map<String, Long>> bindMap = houseMapper.queryBindUnitCountBatch(paramList);
            for (HouseEntity houseEntity : pageData.getRecords()) {
                Map<String, Long> countMap = bindMap.get(houseEntity.getId());
                houseEntity.setBindUnitCount(countMap != null ? countMap.get("count") : null);
            }
            //若查详情，查出已绑定单元id
            if (query.getId() != null) {
                CollectionUtils.firstElement(pageData.getRecords()).setUnitIdList(houseMapper.queryBindUnitList(query.getId()));
            }
            List<Long> param = new ArrayList<>();
            for (HouseEntity houseEntity : pageData.getRecords()) {
                param.add(houseEntity.getBuildingType());
            }
        }
        //查询类型为房屋，设置房屋类型、房产类型、装修情况、户型
        else if (BusinessConst.BUILDING_TYPE_DOOR == query.getType() || BusinessConst.BUILDING_TYPE_DOOR_BUILDING == query.getType() || BusinessConst.BUILDING_TYPE_DOOR_BUILDING_UNIT == query.getType()) {
            List<Long> paramList = new ArrayList<>();
            for (HouseEntity houseEntity : pageData.getRecords()) {
                houseEntity.setHouseTypeStr(PropertyEnum.HouseTypeEnum.HOUSE_TYPE_MAP.get(houseEntity.getHouseType()));
                // houseEntity.setPropertyTypeStr(PropertyEnum.PropertyTypeEnum.PROPERTY_TYPE_MAP.get(houseEntity.getPropertyType()));
                houseEntity.setDecorationStr(PropertyEnum.DecorationEnum.DECORATION_MAP.get(houseEntity.getDecoration()));
                if ("00000000".equals(houseEntity.getHouseTypeCode())) {
                    houseEntity.setHouseTypeCodeStr("单间配套");
                    continue;
                }
                setHouseTypeCodeStr(houseEntity);
                // 查询房屋的时候补充楼栋id
                if (houseEntity != null) {
                    HouseEntity unitEntity = houseMapper.selectOne(new QueryWrapper<HouseEntity>()
                        .eq("id", houseEntity.getPid()).eq("community_id", houseEntity.getCommunityId()));
                    if (unitEntity != null) {
                        if (unitEntity.getPid().equals(0)) {
                            houseEntity.setBuildingId(unitEntity.getId());
                            houseEntity.setBuildingIdStr(String.valueOf(unitEntity.getId()));
                        } else {
                            HouseEntity buildingEntity = houseMapper.selectOne(new QueryWrapper<HouseEntity>()
                                .eq("id", unitEntity.getPid()).eq("community_id", houseEntity.getCommunityId()));
                            if (buildingEntity != null) {
                                houseEntity.setBuildingId(buildingEntity.getId());
                                houseEntity.setBuildingIdStr(String.valueOf(buildingEntity.getId()));
                            }
                        }
                    }
                }
                paramList.add(houseEntity.getId());
            }
            //查询住户数量
            Map<Long, Map<String, Long>> bindMap = houseMapper.selectHouseNumberCount(paramList);
            for (HouseEntity houseEntity : pageData.getRecords()) {
                Map<String, Long> countMap = bindMap.get(houseEntity.getId());
                houseEntity.setHouseNumber(countMap != null ? countMap.get("count") : 0L);
                houseEntity.setStatus(houseEntity.getHouseNumber() == 0 ? "空置" : "入住");
            }
        }
        PageInfo<HouseEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageData, pageInfo);
        return pageInfo;
    }

    //设置户型
    private void setHouseTypeCodeStr(HouseEntity houseEntity) {
        String houseTypeCode = houseEntity.getHouseTypeCode();
        if (houseTypeCode == null || houseTypeCode.length() != 8) {
            houseEntity.setHouseTypeCodeStr(houseEntity.getHouseTypeCode());
            return;
        }
        String bedRoom = Integer.valueOf(houseTypeCode.substring(0, 2)) + "室";
        String livingRoom = Integer.valueOf(houseTypeCode.substring(2, 4)) + "厅";
        String kitchen = Integer.valueOf(houseTypeCode.substring(4, 6)) + "厨";
        String toilet = Integer.valueOf(houseTypeCode.substring(6, 8)) + "卫";
        houseEntity.setHouseTypeCodeStr(bedRoom.concat(livingRoom).concat(kitchen).concat(toilet));
    }
    
    /**
     * @Description: 查询楼栋、单元、房屋导出数据
     * @Param: HouseEntity
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseEntity>
     * @Author: DKS
     * @Date: 2021/8/9
     **/
    @Override
    public List<HouseEntity> queryExportHouseExcel(HouseEntity entity) {
        List<HouseEntity> houseEntities;
        QueryWrapper<HouseEntity> queryWrapper = new QueryWrapper<>();
        if (BusinessConst.BUILDING_TYPE_UNIT_BUILDING == entity.getType()) {
            queryWrapper.eq("type", BusinessConst.BUILDING_TYPE_UNIT);
        } else if (BusinessConst.BUILDING_TYPE_DOOR_BUILDING == entity.getType() || BusinessConst.BUILDING_TYPE_DOOR_UNIT == entity.getType()
            || BusinessConst.BUILDING_TYPE_DOOR_BUILDING_UNIT == entity.getType()) {
            queryWrapper.eq("type", BusinessConst.BUILDING_TYPE_DOOR);
        } else {
            queryWrapper.eq("type", entity.getType());
        }
        queryWrapper.eq("community_id", entity.getCommunityId());
        if (BusinessConst.BUILDING_TYPE_UNIT_BUILDING == entity.getType() || BusinessConst.BUILDING_TYPE_DOOR_UNIT == entity.getType()
            || BusinessConst.BUILDING_TYPE_DOOR_BUILDING_UNIT == entity.getType()) {
            //当类型为单元查楼栋，房屋查单元，房屋查楼栋单元，传入id为需要查询的pid
            queryWrapper.eq("pid", entity.getId());
        } else if (BusinessConst.BUILDING_TYPE_DOOR_BUILDING == entity.getType()) {
            //通过楼栋id查询子集所有单元id
            List<Long> unitIdList = houseMapper.getSubIdList(Arrays.asList(entity.getId()));
            if (!CollectionUtils.isEmpty(unitIdList)) {
                queryWrapper.in("pid", unitIdList);
            }
        } else {
            //是否查详情
            if (entity.getId() != null) {
                queryWrapper.eq("id", entity.getId());
            }
        }
        //是否有电梯
//        if (query.getHasElevator() != null) {
//            queryWrapper.eq("has_elevator", query.getHasElevator());
//        }
        //是否查关键字
        if (BusinessConst.BUILDING_TYPE_UNIT_BUILDING == entity.getType()) {
            queryWrapper.like("unit",entity.getName());
        } else if (BusinessConst.BUILDING_TYPE_DOOR_BUILDING == entity.getType() || BusinessConst.BUILDING_TYPE_DOOR_UNIT == entity.getType()
            || BusinessConst.BUILDING_TYPE_DOOR_BUILDING_UNIT == entity.getType()) {
            queryWrapper.like("door",entity.getName());
        } else {
            //是否查关键字
            if (entity.getName() != null) {
                if (BusinessConst.BUILDING_TYPE_BUILDING == entity.getType()) {
                    queryWrapper.like("building",entity.getName());
                } else if (BusinessConst.BUILDING_TYPE_UNIT == entity.getType()) {
                    queryWrapper.like("unit",entity.getName());
                } else if (BusinessConst.BUILDING_TYPE_DOOR == entity.getType()) {
                    queryWrapper.like("door",entity.getName());
                }
            }
        }
        //是否查楼栋层数
        if (entity.getTotalFloor() != null) {
            queryWrapper.eq("total_floor",entity.getTotalFloor());
        }
        //是否查楼宇分类
        if (entity.getBuildingType() != null) {
            queryWrapper.eq("building_type",entity.getBuildingType());
        }
        queryWrapper.orderByDesc("create_time");
        houseEntities = houseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(houseEntities)) {
            return houseEntities;
        }
        //查询类型为楼栋，再查出已绑定单元数
        if (BusinessConst.BUILDING_TYPE_BUILDING == entity.getType()) {
            List<Long> paramList = new ArrayList<>();
            for (HouseEntity houseEntity : houseEntities) {
                paramList.add(houseEntity.getId());
            }
            Map<Long, Map<String, Long>> bindMap = houseMapper.queryBindUnitCountBatch(paramList);
            for (HouseEntity houseEntity : houseEntities) {
                Map<String, Long> countMap = bindMap.get(houseEntity.getId());
                houseEntity.setBindUnitCount(countMap != null ? countMap.get("count") : null);
            }
            //若查详情，查出已绑定单元id
            if (entity.getId() != null) {
                CollectionUtils.firstElement(houseEntities).setUnitIdList(houseMapper.queryBindUnitList(entity.getId()));
            }
            List<Long> param = new ArrayList<>();
            for (HouseEntity houseEntity : houseEntities) {
                param.add(houseEntity.getBuildingType());
            }
        }
        //查询类型为房屋，设置房屋类型、房产类型、装修情况、户型
        else if (BusinessConst.BUILDING_TYPE_DOOR == entity.getType() || BusinessConst.BUILDING_TYPE_DOOR_BUILDING == entity.getType() || BusinessConst.BUILDING_TYPE_DOOR_BUILDING_UNIT == entity.getType()) {
            List<Long> paramList = new ArrayList<>();
            for (HouseEntity houseEntity : houseEntities) {
                houseEntity.setHouseTypeStr(PropertyEnum.HouseTypeEnum.HOUSE_TYPE_MAP.get(houseEntity.getHouseType()));
                // houseEntity.setPropertyTypeStr(PropertyEnum.PropertyTypeEnum.PROPERTY_TYPE_MAP.get(houseEntity.getPropertyType()));
                houseEntity.setDecorationStr(PropertyEnum.DecorationEnum.DECORATION_MAP.get(houseEntity.getDecoration()));
                if ("00000000".equals(houseEntity.getHouseTypeCode())) {
                    houseEntity.setHouseTypeCodeStr("单间配套");
                    continue;
                }
                setHouseTypeCodeStr(houseEntity);
	            // 查询房屋的时候补充楼栋id
	            if (houseEntity != null) {
		            HouseEntity unitEntity = houseMapper.selectOne(new QueryWrapper<HouseEntity>()
			            .eq("id", houseEntity.getPid()).eq("community_id", houseEntity.getCommunityId()));
		            if (unitEntity != null) {
			            if (unitEntity.getPid().equals(0)) {
				            houseEntity.setBuildingId(unitEntity.getId());
                            houseEntity.setBuildingIdStr(String.valueOf(unitEntity.getId()));
			            } else {
				            HouseEntity buildingEntity = houseMapper.selectOne(new QueryWrapper<HouseEntity>()
					            .eq("id", unitEntity.getPid()).eq("community_id", houseEntity.getCommunityId()));
				            if (buildingEntity != null) {
					            houseEntity.setBuildingId(buildingEntity.getId());
                                houseEntity.setBuildingIdStr(String.valueOf(buildingEntity.getId()));
				            }
			            }
		            }
	            }
                houseEntity.setPidStr(String.valueOf(houseEntity.getPid()));
                paramList.add(houseEntity.getId());
            }
            //查询住户数量
            Map<Long, Map<String, Long>> bindMap = houseMapper.selectHouseNumberCount(paramList);
            for (HouseEntity houseEntity : houseEntities) {
                Map<String, Long> countMap = bindMap.get(houseEntity.getId());
                houseEntity.setHouseNumber(countMap != null ? countMap.get("count") : 0L);
                houseEntity.setStatus(houseEntity.getHouseNumber() == 0 ? "空置" : "入住");
            }
        }
        return houseEntities;
    }
    
}
