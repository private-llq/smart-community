package com.jsy.community.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.dto.advert.AdvertDto;
import com.jsy.community.entity.admin.AdvertEntity;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.AdvertMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.AddAdvertQO;
import com.jsy.community.qo.admin.AdvertQO;
import com.jsy.community.service.AdminException;
import com.jsy.community.service.AdvertService;
import com.jsy.community.vo.property.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.management.JMException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xrq
 * @version 1.0
 * @Description: 广告服务层实现
 * @date 2021/12/25 11:30
 */
@Service
@Slf4j
public class AdvertServiceImpl extends ServiceImpl<AdvertMapper, AdvertEntity> implements AdvertService {

    @Autowired
    private AdvertMapper advertMapper;

    /**
     * 新增广告
     * @param param 新增广告参数
     */
    @Override
    public boolean insertAdvert(AddAdvertQO param) {
        log.info("param= {}", param);
        AdvertEntity entity = new AdvertEntity();
        AdvertEntity advertEntity = getOne(new LambdaQueryWrapper<AdvertEntity>().eq(AdvertEntity::getDisplayPosition, param.getDisplayPosition())
                .eq(AdvertEntity::getSort, param.getSort()));
        if (ObjectUtil.isNotNull(advertEntity)) {
            log.error("该顺序已被 id= {} 选择", advertEntity.getId());
            throw new AdminException("该顺序已被其他广告占用，请重新选择顺序");
        }
        BeanUtils.copyProperties(param, entity);
        LocalDateTime now = LocalDateTime.now();
        entity.setAdvertId(IdUtil.simpleUUID())
                .setSort(param.getSort())
                .setCreateTime(now)
                .setUpdateTime(now);
        return save(entity);
    }

    @Override
    public IPage<AdvertDto> toPage(AdvertQO qo) {
        Page<AdvertDto> page = new Page<AdvertDto>(qo.getPage(),qo.getSize());
        IPage<AdvertDto> iPage = advertMapper.page(page, qo.getDisplayPosition());
        return iPage;
    }

    @Override
    public boolean updateAdvert(AdvertEntity entity) {
        entity.setUpdateTime(LocalDateTime.now());
        return updateById(entity);
    }

    /**
     * 排序
     */
    public Integer sort(Integer position, Integer sort) {
        List<AdvertEntity> list = list(new LambdaQueryWrapper<AdvertEntity>().eq(AdvertEntity::getDisplayPosition, position));
        if (CollectionUtils.isEmpty(list)) {
            return 1;
        }
        if (list.size() < sort) {
            return list.size()+1;
        }
        List<AdvertEntity> oldSortList = list.stream().filter(entity -> entity.getSort() >= sort).collect(Collectors.toList());
        oldSortList.forEach(entity -> {
            entity.setSort(entity.getSort()+1);
            updateById(entity);
        });
        return sort;
    }


}
