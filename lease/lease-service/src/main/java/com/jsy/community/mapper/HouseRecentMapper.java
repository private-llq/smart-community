package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseLeaseConstEntity;
import com.jsy.community.entity.lease.HouseRecentEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 房屋最近浏览Mapper 接口
 * @author YuLF
 * @since 2020-02-19
 */
public interface HouseRecentMapper extends BaseMapper<HouseRecentEntity> {


}
