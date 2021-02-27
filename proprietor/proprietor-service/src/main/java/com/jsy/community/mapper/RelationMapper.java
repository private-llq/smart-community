package com.jsy.community.mapper;

import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.qo.RelationCarsQo;
import com.jsy.community.qo.RelationQo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 家属 Mapper 接口
 * </p>
 *
 * @author
 * @since 2020-12-3
 */
public interface RelationMapper {
    /**
     * 添加家属车辆
     * @param cars
     */
    void addCars(@Param("cars") List<RelationCarsQo> cars);
    /**
     * 通过业主id查询家属信息
     * @param id
     */
    List<HouseMemberEntity> selectID(@Param("id") String id,@Param("houseId") Long houseId);

    /**
     * @Description: 修改家属信息
     * @author: Hu
     * @since: 2020/12/19 10:08
     * @Param:
     * @return:
     */
    void updateUserRelationDetails(RelationQo relationQo);

    /**
     * @Description: 批量修改车辆信息
     * @author: Hu
     * @since: 2020/12/19 10:08
     * @Param:
     * @return:
     */
    void updateUserRelationCar(RelationCarsQo relationCarsQo);
}
