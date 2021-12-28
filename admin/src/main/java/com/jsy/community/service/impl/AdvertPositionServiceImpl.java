package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.admin.AdvertPositionEntity;
import com.jsy.community.mapper.AdvertPositionMapper;
import com.jsy.community.qo.admin.AddAdvertPositionQO;
import com.jsy.community.service.AdvertPositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xrq
 * @version 1.0
 * @Description: 广告位置服务层实现
 * @date 2021/12/25 16:05
 */
@Service
@Slf4j
public class AdvertPositionServiceImpl extends ServiceImpl<AdvertPositionMapper, AdvertPositionEntity> implements AdvertPositionService {

    @Override
    public boolean insertPosition(AddAdvertPositionQO param) {
        log.info("param= {}", param);
        AdvertPositionEntity entity = new AdvertPositionEntity();
        BeanUtils.copyProperties(param, entity);
        LocalDateTime now = LocalDateTime.now();
        entity.setSort(sort(param.getPid(), param.getSort()))
                .setFullName(getFullName(param.getPid(), param.getPositionName()))
                .setCreateTime(now)
                .setUpdateTime(now);
        return save(entity);
    }

    private Integer sort(Integer pid, Integer sort) {
        List<AdvertPositionEntity> list = list(new LambdaQueryWrapper<AdvertPositionEntity>().eq(AdvertPositionEntity::getPid, pid));
        if (CollectionUtils.isEmpty(list)) {
            return 1;
        }
        if (list.size() < sort) {
            return list.size()+1;
        }
        List<AdvertPositionEntity> oldSortList = list.stream().filter(entity -> entity.getSort() >= sort).collect(Collectors.toList());
        oldSortList.forEach(entity -> {
            entity.setSort(entity.getSort()+1);
            updateById(entity);
        });
        return sort;
    }

    private String getFullName(Integer pid, String name) {
        StringBuilder stb = new StringBuilder(name);
        while (0 != pid) {
            AdvertPositionEntity parent = getById(pid);
            name = stb.insert(0, parent.getPositionName()+"-").toString();
            pid = parent.getPid();
        }
        return name;
    }
}
