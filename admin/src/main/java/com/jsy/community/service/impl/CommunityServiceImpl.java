package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.PropertyCompanyEntity;
import com.jsy.community.mapper.CommunityMapper;
import com.jsy.community.mapper.PropertyCompanyMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CommunityQO;
import com.jsy.community.service.ICommunityService;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.admin.CommunityPropertyListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chq459799974
 * @description 社区实现类
 * @since 2020-11-19 16:57
 **/
@Service
public class CommunityServiceImpl extends ServiceImpl<CommunityMapper, CommunityEntity> implements ICommunityService {

    @Resource
    private CommunityMapper communityMapper;

    @Resource
    private PropertyCompanyMapper propertyCompanyMapper;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * @Description: 社区新增
     * @Param: [communityEntity]
     * @Return: boolean
     * @Author: DKS
     * @Date: 2021/10/18
     **/
    @Override
    public boolean addCommunity(CommunityEntity communityEntity) {
        communityEntity.setId(SnowFlake.nextId());
        if (StringUtils.isEmpty(communityEntity.getPromoter())) {
            communityEntity.setPromoter("system");
        }
        return communityMapper.insert(communityEntity) == 1;
    }

    /**
     * @param communityEntity :
     * @description: 物业端更新社区信息
     * @return: java.lang.Integer
     * @Author: DKS
     * @Date: 2021/10/18
     **/
    @Override
    public Integer updateCommunity(CommunityEntity communityEntity) {
        return communityMapper.updateById(communityEntity);
    }

    /**
     * @Description: 社区查询
     * @Param: [baseQO]
     * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.CommunityEntity>
     * @Author: DKS
     * @Date: 2021/10/18
     **/
    @Override
    public PageInfo<CommunityEntity> queryCommunity(BaseQO<CommunityQO> baseQO) {
        Page<CommunityEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        QueryWrapper<CommunityEntity> queryWrapper = new QueryWrapper<CommunityEntity>().select("*");
        CommunityQO query = baseQO.getQuery();
        if (query != null) {
            if (query.getId() != null) {
                queryWrapper.eq("id", query.getId());
            }
            if (query.getProvinceId() != null) {
                queryWrapper.eq("province_id", query.getProvinceId());
            }
            if (query.getCityId() != null) {
                queryWrapper.eq("city_id", query.getCityId());
            }
            if (query.getAreaId() != null) {
                queryWrapper.eq("area_id", query.getAreaId());
            }
            if (query.getPropertyId() != null) {
                queryWrapper.eq("property_id", query.getPropertyId());
            }
        }
        Page<CommunityEntity> communityEntityPage = communityMapper.selectPage(page, queryWrapper);
        if (CollectionUtils.isEmpty(communityEntityPage.getRecords())) {
            return new PageInfo<>();
        }
        // 补充地区地址
        for (CommunityEntity record : communityEntityPage.getRecords()) {
            String province = redisTemplate.opsForValue().get("RegionSingle:" + record.getProvinceId());
            String city = redisTemplate.opsForValue().get("RegionSingle:" + record.getCityId());
            String area = redisTemplate.opsForValue().get("RegionSingle:" + record.getAreaId());
            province = org.apache.commons.lang3.StringUtils.isNotBlank(province) ? province : "";
            city = org.apache.commons.lang3.StringUtils.isNotBlank(city) ? city : "";
            area = org.apache.commons.lang3.StringUtils.isNotBlank(area) ? area : "";
            record.setAddress(province + city + area + record.getDetailAddress());
            // 补充物业公司名称
            PropertyCompanyEntity companyEntity = propertyCompanyMapper.selectById(record.getPropertyId());
            record.setCompanyName(companyEntity.getName());
            record.setPropertyIdStr(String.valueOf(record.getPropertyId()));
        }
        PageInfo<CommunityEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(communityEntityPage, pageInfo);
        return pageInfo;
    }

    /**
     * @Description: 删除小区
     * @Param: [id]
     * @Return: boolean
     * @Author: DKS
     * @Date: 2021/10/18
     **/
    @Override
    public boolean delCommunity(Long id) {
        return communityMapper.deleteById(id) == 1;
    }

    /**
     * @Description: 社区列表查询
     * @Param: []
     * @Return: java.util.List<com.jsy.community.entity.sys.PropertyCompanyEntity>
     * @Author: DKS
     * @Date: 2021/10/19
     **/
    @Override
    public List<CommunityEntity> queryCommunityList() {
        return communityMapper.selectList(new QueryWrapper<CommunityEntity>().select("*"));
    }

    /**
     * 查询小区名字和物业公司名字以及小区id
     *
     * @return
     */
    @Override
    public List<CommunityPropertyListVO> queryCommunityAndPropertyList() {
        List<CommunityPropertyListVO> communityPropertyList =
                communityMapper.queryCommunityAndPropertyListByArea(null, null, null);
        communityPropertyList.stream().peek(r -> {
            r.setCommunityName(r.getPropertyName() + r.getCommunityName());
            r.setCommunityIdStr(String.valueOf(r.getCommunityId()));
        }).collect(Collectors.toList());
        return communityPropertyList;
    }
}
