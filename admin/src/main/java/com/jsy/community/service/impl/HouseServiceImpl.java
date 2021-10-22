package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.PropertyCompanyEntity;
import com.jsy.community.mapper.CommunityMapper;
import com.jsy.community.mapper.HouseMapper;
import com.jsy.community.mapper.HouseMemberMapper;
import com.jsy.community.mapper.PropertyCompanyMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.HouseQO;
import com.jsy.community.service.IHouseService;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
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
    
    @Resource
    private PropertyCompanyMapper propertyCompanyMapper;
    
    @Resource
    private CommunityMapper communityMapper;

    /**
     * @Description: 【楼宇房屋】条件查询
     * @Param: [baseQO]
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseEntity>
     * @Author: DKS
     * @Date: 2021/10/22
     **/
    @Override
    public PageInfo<HouseEntity> queryHouse(BaseQO<HouseQO> baseQO) {
        HouseQO query = baseQO.getQuery();
        Page<HouseEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        QueryWrapper<HouseEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", BusinessConst.BUILDING_TYPE_DOOR);
        //是否查小区
        if (query.getCommunityId() != null) {
            queryWrapper.eq("community_id", query.getCommunityId());
        }
        //是否查房号/业主
        if (StringUtils.isNotBlank(query.getDoorOrOwner())) {
            List<Long> houseIdList = houseMemberMapper.getAllHouseIdByOwnerName(query.getDoorOrOwner());
            queryWrapper.like("door",query.getDoorOrOwner());
            if (houseIdList.size() > 0) {
                queryWrapper.or().in("id", houseIdList);
            }
        }
        queryWrapper.orderByDesc("create_time");
        Page<HouseEntity> pageData = houseMapper.selectPage(page, queryWrapper);
        if (CollectionUtils.isEmpty(pageData.getRecords())) {
            return new PageInfo<>();
        }
        List<Long> paramList = new ArrayList<>();
        for (HouseEntity houseEntity : pageData.getRecords()) {
            // 补充物业公司名称
            CommunityEntity communityEntity = communityMapper.selectById(houseEntity.getCommunityId());
            PropertyCompanyEntity companyEntity = propertyCompanyMapper.selectById(communityEntity.getPropertyId());
            houseEntity.setCompanyName(companyEntity.getName());
            // 补充社区名称
            houseEntity.setCommunityName(communityEntity.getName());
            // 补充业主名字
            houseEntity.setOwner(houseMemberMapper.getOwnerNameByHouseId(houseEntity.getId()));
            paramList.add(houseEntity.getId());
        }
        // 补充住户量
        Map<Long, Map<String, Long>> bindMap = houseMapper.selectHouseNumberCount(paramList);
        for (HouseEntity houseEntity : pageData.getRecords()) {
            Map<String, Long> countMap = bindMap.get(BigInteger.valueOf(houseEntity.getId()));
            houseEntity.setHouseNumber(countMap != null ? countMap.get("count") : 0L);
            // 补充状态
            houseEntity.setStatus(houseEntity.getHouseNumber() == 0 ? "空置" : "已绑定");
            // 查询该房间是否有租户
            Integer row = houseMemberMapper.getTenantByHouseId(houseEntity.getId());
            if (row > 0) {
                houseEntity.setStatus("已租用");
            }
        }
        PageInfo<HouseEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageData, pageInfo);
        return pageInfo;
    }
    
    /**
     * @Description: 查询楼栋、单元、房屋导出数据
     * @Param: HouseEntity
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseEntity>
     * @Author: DKS
     * @Date: 2021/10/22
     **/
    @Override
    public List<HouseEntity> queryExportHouseExcel(HouseQO houseQO) {
        List<HouseEntity> houseEntities;
        QueryWrapper<HouseEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", BusinessConst.BUILDING_TYPE_DOOR);
        //是否查小区
        if (houseQO.getCommunityId() != null) {
            queryWrapper.eq("community_id", houseQO.getCommunityId());
        }
        //是否查房号/业主
        if (houseQO.getDoorOrOwner() != null) {
            List<Long> houseIdList = houseMemberMapper.getAllHouseIdByOwnerName(houseQO.getDoorOrOwner());
            queryWrapper.like("door",houseQO.getDoorOrOwner());
            if (houseIdList.size() > 0) {
                queryWrapper.or().in("id", houseIdList);
            }
        }
        queryWrapper.orderByDesc("create_time");
        houseEntities = houseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(houseEntities)) {
            return new ArrayList<>();
        }
        List<Long> paramList = new ArrayList<>();
        for (HouseEntity houseEntity : houseEntities) {
            // 补充物业公司名称
            CommunityEntity communityEntity = communityMapper.selectById(houseEntity.getCommunityId());
            PropertyCompanyEntity companyEntity = propertyCompanyMapper.selectById(communityEntity.getPropertyId());
            houseEntity.setCompanyName(companyEntity.getName());
            // 补充社区名称
            houseEntity.setCommunityName(communityEntity.getName());
            // 补充业主名字
            houseEntity.setOwner(houseMemberMapper.getOwnerNameByHouseId(houseEntity.getId()));
            paramList.add(houseEntity.getId());
        }
        // 补充住户量
        Map<Long, Map<String, Long>> bindMap = houseMapper.selectHouseNumberCount(paramList);
        for (HouseEntity houseEntity : houseEntities) {
            Map<String, Long> countMap = bindMap.get(BigInteger.valueOf(houseEntity.getId()));
            houseEntity.setHouseNumber(countMap != null ? countMap.get("count") : 0L);
            // 补充状态
            houseEntity.setStatus(houseEntity.getHouseNumber() == 0 ? "空置" : "已绑定");
            // 查询该房间是否有租户
            Integer row = houseMemberMapper.getTenantByHouseId(houseEntity.getId());
            if (row > 0) {
                houseEntity.setStatus("已租用");
            }
        }
        return houseEntities;
    }
}
