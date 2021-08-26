package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IProprietorMarketLabelService;
import com.jsy.community.api.IProprietorMarketService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.ProprietorMarketEntity;
import com.jsy.community.entity.proprietor.ProprietorMarketLabelEntity;
import com.jsy.community.mapper.ProprietorMarketLabelMapper;
import com.jsy.community.mapper.ProprietorMarketMapper;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@DubboService(version = Const.version, group = Const.group_proprietor)
public class ProprietorMarketLabelServiceImpl extends ServiceImpl<ProprietorMarketLabelMapper, ProprietorMarketLabelEntity> implements IProprietorMarketLabelService {
   @Autowired
   private ProprietorMarketLabelMapper labelMapper;

    /**
     * @Description: 新增社区集市商品标签
     * @Param: [labelEntity]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-9:43
     **/
    @Override
    public boolean addMarketLabel(ProprietorMarketLabelEntity labelEntity) {
        labelEntity.setId(SnowFlake.nextId());
        labelEntity.setLabelId(UUID.randomUUID().toString());
        return labelMapper.insert(labelEntity) == 1;
    }

    /**
     * @Description: 修改社区集市商品标签
     * @Param: [labelEntity]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-9:50
     **/
    @Override
    public boolean updateMarketLabel(ProprietorMarketLabelEntity labelEntity) {
        return labelMapper.update(labelEntity,new UpdateWrapper<ProprietorMarketLabelEntity>().eq("id",labelEntity.getId())) == 1;
    }

    @Override
    public boolean deleteMarketLabel(Long id) {

        return labelMapper.delete(new QueryWrapper<ProprietorMarketLabelEntity>().eq("id",id)) == 1;
    }

    @Override
    public List<ProprietorMarketLabelEntity> selectMarketLabel(Long communityId) {
        return labelMapper.selectList(new QueryWrapper<ProprietorMarketLabelEntity>().eq("community_id",communityId));
    }
}
