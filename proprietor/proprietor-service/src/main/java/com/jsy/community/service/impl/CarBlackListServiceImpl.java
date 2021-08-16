package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.api.ICarBlackListService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.CarBlackListEntity;
import com.jsy.community.mapper.CarBlackListMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@DubboService(version = Const.version, group = Const.group)
public class CarBlackListServiceImpl implements ICarBlackListService {

    @Autowired
    public CarBlackListMapper carBlackListMapper;


    @Override
    public PageInfo<CarBlackListEntity> carBlackListPage(BaseQO<String> baseQO) {
        Page<CarBlackListEntity> Page = new Page<>(baseQO.getPage(),baseQO.getSize());
        PageInfo<CarBlackListEntity> pageInfo = new PageInfo<>();
        Page<CarBlackListEntity> selectPage = carBlackListMapper.selectPage(Page, new QueryWrapper<CarBlackListEntity>().eq(StringUtils.isNoneBlank(baseQO.getQuery()),"car_number", baseQO.getQuery()));
        pageInfo.setTotal(selectPage.getTotal());
        pageInfo.setCurrent(selectPage.getCurrent());
        pageInfo.setSize(selectPage.getSize());
        pageInfo.setRecords(selectPage.getRecords());
        return pageInfo;
    }

    /**
     * 移除黑名单
     * @param uid
     * @return
     */
    @Override
    @Transactional
    public Integer delBlackList(String uid){
        int delete = carBlackListMapper.delete(new QueryWrapper<CarBlackListEntity>().eq("uid", uid));
        return delete;
    }

    /**
     * 添加进入黑名单
     * @param carBlackListEntity
     * @return
     */
    @Override
    @Transactional
    public Integer saveBlackList(CarBlackListEntity carBlackListEntity) {
        carBlackListEntity.setUid(UserUtils.randomUUID());
        carBlackListEntity.setAddTime(LocalDateTime.now());
        int insert = carBlackListMapper.insert(carBlackListEntity);
        return insert;
    }


}
