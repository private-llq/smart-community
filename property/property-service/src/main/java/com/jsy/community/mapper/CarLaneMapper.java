package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.entity.proprietor.CarLaneEntity;
import com.jsy.community.qo.BaseQO;
import org.apache.ibatis.annotations.Param;


public interface CarLaneMapper extends BaseMapper<CarLaneEntity> {


    IPage<CarLaneEntity> findAllByPage(Page<CarLaneEntity> page, @Param("baseQO") BaseQO<String> baseQO);

}
