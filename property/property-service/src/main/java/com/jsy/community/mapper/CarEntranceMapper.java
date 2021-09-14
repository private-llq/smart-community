package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.entity.property.CarCutOffEntity;
import com.jsy.community.qo.property.CarEntranceQO;
import com.jsy.community.vo.property.CarEntranceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CarEntranceMapper extends BaseMapper<CarEntranceVO> {
    IPage<CarEntranceVO> selectCarEntrance(@Param("page") Page page, @Param("query") CarEntranceQO query);
}
