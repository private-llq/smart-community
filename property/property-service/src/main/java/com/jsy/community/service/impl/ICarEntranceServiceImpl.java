package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarEntranceService;
import com.jsy.community.constant.Const;
import com.jsy.community.mapper.CarEntranceMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.CarEntranceQO;
import com.jsy.community.vo.property.CarEntranceVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DubboService(version = Const.version, group = Const.group_property)
public class ICarEntranceServiceImpl extends ServiceImpl<CarEntranceMapper,CarEntranceVO> implements ICarEntranceService {
    @Autowired
    private CarEntranceMapper carEntranceMapper;

    @Override
    public Map<String, Object> selectCarEntrance(BaseQO<CarEntranceQO> baseQO, Long communityId) {
        Page<CarEntranceVO> page = new Page<>(baseQO.getPage(), baseQO.getSize());
        CarEntranceQO query = baseQO.getQuery();
        query.setCommunityId(communityId);
        IPage<CarEntranceVO> iPage =  carEntranceMapper.selectCarEntrance(page,query);
        List<CarEntranceVO> records = iPage.getRecords();

        long total = iPage.getTotal();
        Map<String, Object> map = new HashMap<>();
        map.put("total",total);
        map.put("list",records);
        return map;
    }
}
