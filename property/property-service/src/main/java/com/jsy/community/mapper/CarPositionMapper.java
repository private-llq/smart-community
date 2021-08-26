package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.CarPositionEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarPositionMapper extends BaseMapper<CarPositionEntity> {

    <T> void seavefile(List<T> list);

    List<CarPositionEntity> selectCarPosition(CarPositionEntity qo);

    /**
     *@Author: DKS
     *@Description: 查询小区下所有的车位
     *@Param: communityId:
     *@Return: java.util.List<com.jsy.community.entity.CarPositionEntity>
     *@Date: 2021/8/24 13:44
     **/
    List<CarPositionEntity> getAllCarPositionByCommunity(Long communityId);

    /**
     * @Description: 查询communityIds下所有车位数量
     * @author: DKS
     * @since: 2021/8/25 14:44
     * @Param: communityIdList
     * @return: Integer
     */
    Integer selectAllCarPositionByCommunityIds(@Param("list") List<Long> communityIdList);
}
