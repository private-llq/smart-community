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

    /**
     * 新增广告位
     * @param param 新增参数
     * @return 是否成功
     */
    @Override
    public boolean insertPosition(AddAdvertPositionQO param) {
        log.info("执行新增广告位，param= {}", param);
        AdvertPositionEntity entity = new AdvertPositionEntity();
        BeanUtils.copyProperties(param, entity);
        LocalDateTime now = LocalDateTime.now();
        entity.setSort(sort(param.getPid(), param.getSort()))
                .setFullName(getFullName(param.getPid(), param.getPositionName()))
                .setCreateTime(now)
                .setUpdateTime(now);
        return save(entity);
    }

    /**
     * 广告位展示排序 TODO 用户选择期望的顺序，如果有冲突，原有顺序均往后移一位
     * @param pid
     * @param sort
     * @return 实际顺序
     */
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

    /**
     * 拿到全路径名 TODO 暂且用不到，因产品要求，目前广告位没有二级分类
     * @param pid 位置id
     * @param name 名称
     * @return 全路径名称
     */
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
