package com.jsy.community.mapper;

import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.qo.proprietor.RelationCarsQO;
import com.jsy.community.qo.proprietor.RelationQO;
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
    void addCars(@Param("cars") List<RelationCarsQO> cars);
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
    void updateUserRelationDetails(RelationQO relationQo);

    /**
     * @Description: 批量修改车辆信息
     * @author: Hu
     * @since: 2020/12/19 10:08
     * @Param:
     * @return:
     */
    void updateUserRelationCar(RelationCarsQO relationCarsQo);

    /**
     * @Description: 新增一条
     * @author: Hu
     * @since: 2021/3/1 10:55
     * @Param:
     * @return:
     */
    void insertOne(RelationCarsQO relationCarsQo);
}
