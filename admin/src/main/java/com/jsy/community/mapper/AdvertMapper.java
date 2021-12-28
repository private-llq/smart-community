package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.dto.advert.AdvertDto;
import com.jsy.community.entity.admin.AdvertEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 广告控制层
 * @author: xrq
 * @create: 2021-12-25 11:00
 **/
@Mapper
public interface AdvertMapper extends BaseMapper<AdvertEntity> {
    IPage<AdvertDto> page(Page page, @Param("display_position") Integer displayPosition);
}
