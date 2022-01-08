package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.vo.admin.CommunityPropertyListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chq459799974
 * @description 社区Mapper
 * @since 2020-11-19 16:57
 **/
@Mapper
public interface CommunityMapper extends BaseMapper<CommunityEntity> {

    /**
     * 根据省市区查询小区，省市区可以不传查全部
     *
     * @return
     */
    List<CommunityPropertyListVO> queryCommunityAndPropertyListByArea( @Param("provinceId")Integer provinceId, @Param("cityId")Integer cityId, @Param("areaId")Integer areaId);

}
