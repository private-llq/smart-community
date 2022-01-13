package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.entity.PropertyCompanyEntity;
import com.jsy.community.mapper.CommunityMapper;
import com.jsy.community.mapper.HouseMapper;
import com.jsy.community.mapper.HouseMemberMapper;
import com.jsy.community.mapper.PropertyCompanyMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.HouseMemberQO;
import com.jsy.community.service.IHouseMemberService;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 住户信息 服务实现类
 * </p>
 *
 * @author DKS
 * @since 2021-10-22
 */
@Service
public class HouseMemberServiceImpl extends ServiceImpl<HouseMemberMapper, HouseMemberEntity> implements IHouseMemberService {

    @Resource
    private HouseMapper houseMapper;
    
    @Resource
    private HouseMemberMapper houseMemberMapper;
    
    @Resource
    private PropertyCompanyMapper propertyCompanyMapper;
    
    @Resource
    private CommunityMapper communityMapper;

    /**
     * @Description: 【住户】条件查询
     * @Param: [baseQO]
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseMemberEntity>
     * @Author: DKS
     * @Date: 2021/10/22
     **/
    @Override
    public PageInfo<HouseMemberEntity> queryHouseMember(BaseQO<HouseMemberQO> baseQO) {
        HouseMemberQO query = baseQO.getQuery();
        Page<HouseMemberEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        QueryWrapper<HouseMemberEntity> queryWrapper = new QueryWrapper<>();
        //是否查小区
        if (query.getCommunityId() != null) {
            queryWrapper.eq("community_id", query.getCommunityId());
        }
        //是否查姓名/手机/房号
        if (StringUtils.isNotBlank(query.getNameOrMobileOrDoor())) {
            List<Long> houseIdList = houseMapper.getHouseIdByDoor(query.getNameOrMobileOrDoor());
            queryWrapper.like("name", query.getNameOrMobileOrDoor());
            queryWrapper.or().like("mobile", query.getNameOrMobileOrDoor());
            if (houseIdList.size() > 0) {
                queryWrapper.or().in("house_id", houseIdList);
            }
        }
        queryWrapper.orderByDesc("create_time");
        Page<HouseMemberEntity> pageData = houseMemberMapper.selectPage(page, queryWrapper);
        if (CollectionUtils.isEmpty(pageData.getRecords())) {
            return new PageInfo<>();
        }
        for (HouseMemberEntity houseMemberEntity : pageData.getRecords()) {
            // 补充物业公司名称
            CommunityEntity communityEntity = communityMapper.selectById(houseMemberEntity.getCommunityId());
            if (communityEntity != null) {
                PropertyCompanyEntity companyEntity = propertyCompanyMapper.selectById(communityEntity.getPropertyId());
                houseMemberEntity.setCompanyName(companyEntity.getName());
                // 补充社区名称
                houseMemberEntity.setCommunityName(communityEntity.getName());
            }
            // 补充身份名称
            houseMemberEntity.setRelationName(houseMemberEntity.getRelation() == 1 ? "业主" : houseMemberEntity.getRelation() == 6 ? "亲属" : houseMemberEntity.getRelation() == 7 ? "租户" : "临时");
            // 补充房屋信息
            HouseEntity houseEntity = houseMapper.selectById(houseMemberEntity.getHouseId());
            if (houseEntity != null) {
                houseMemberEntity.setHouseSite(houseEntity.getBuilding() + "-" + houseEntity.getUnit() + "-" + houseEntity.getDoor());
            }
            // 补充标签
            if (houseMemberEntity.getTally() != null) {
                houseMemberEntity.setTallyName(houseMemberEntity.getTally() == 1 ? "独居" : houseMemberEntity.getTally() == 2 ? "孤寡" :
                    houseMemberEntity.getTally() == 3 ? "残疾" : houseMemberEntity.getTally() == 4 ? "留守" : "");
            }
        }
        PageInfo<HouseMemberEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageData, pageInfo);
        return pageInfo;
    }
    
    /**
     * @Description: 查询住户
     * @Param: HouseEntity
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseEntity>
     * @Author: DKS
     * @Date: 2021/10/22 16:38
     **/
    @Override
    public List<HouseMemberEntity> queryExportHouseMemberExcel(HouseMemberQO houseMemberQO) {
        List<HouseMemberEntity> houseMemberEntities;
        QueryWrapper<HouseMemberEntity> queryWrapper = new QueryWrapper<>();
        //是否查小区
        if (houseMemberQO.getCommunityId() != null) {
            queryWrapper.eq("community_id", houseMemberQO.getCommunityId());
        }
        //是否查姓名/手机/房号
        if (StringUtils.isNotBlank(houseMemberQO.getNameOrMobileOrDoor())) {
            List<Long> houseIdList = houseMapper.getHouseIdByDoor(houseMemberQO.getNameOrMobileOrDoor());
            queryWrapper.like("name", houseMemberQO.getNameOrMobileOrDoor());
            queryWrapper.or().like("mobile", houseMemberQO.getNameOrMobileOrDoor());
            if (houseIdList.size() > 0) {
                queryWrapper.or().in("house_id", houseIdList);
            }
        }
        queryWrapper.orderByDesc("create_time");
        houseMemberEntities = houseMemberMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(houseMemberEntities)) {
            return new ArrayList<>();
        }
        for (HouseMemberEntity houseMemberEntity : houseMemberEntities) {
            // 补充物业公司名称
            CommunityEntity communityEntity = communityMapper.selectById(houseMemberEntity.getCommunityId());
            PropertyCompanyEntity companyEntity = propertyCompanyMapper.selectById(communityEntity.getPropertyId());
            houseMemberEntity.setCompanyName(companyEntity.getName());
            // 补充社区名称
            houseMemberEntity.setCommunityName(communityEntity.getName());
            // 补充身份名称
            houseMemberEntity.setRelationName(houseMemberEntity.getRelation() == 1 ? "业主" : houseMemberEntity.getRelation() == 6 ? "亲属" : houseMemberEntity.getRelation() == 7 ? "租户" : "临时");
            // 补充房屋信息
            HouseEntity houseEntity = houseMapper.selectById(houseMemberEntity.getHouseId());
            if (houseEntity != null) {
                houseMemberEntity.setHouseSite(houseEntity.getBuilding() + "-" + houseEntity.getUnit() + "-" + houseEntity.getDoor());
            }
            // 补充标签
            if (houseMemberEntity.getTally() != null) {
                houseMemberEntity.setTallyName(houseMemberEntity.getTally() == 1 ? "独居" : houseMemberEntity.getTally() == 2 ? "孤寡" :
                    houseMemberEntity.getTally() == 3 ? "残疾" : houseMemberEntity.getTally() == 4 ? "留守" : "");
            }
        }
        return houseMemberEntities;
    }
}
