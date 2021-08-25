package com.jsy.community.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.CarPositionEntity;
import com.jsy.community.qo.property.SelectCarPositionPagingQO;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 车位 服务类
 * </p>
 *
 * @author Arli
 * @since 2021-08-03
 */
public interface ICarPositionService extends IService<CarPositionEntity> {


    List<CarPositionEntity> selectCarPostionBystatustatus();

    Page<CarPositionEntity> selectCarPositionPaging(SelectCarPositionPagingQO qo, Long adminCommunityId);


    <T> void seavefile(List<T> list);

    List<CarPositionEntity> selectCarPosition(CarPositionEntity qo);

    List<CarPositionEntity> getAll(Long adminCommunityId);

    /**
     * @Description: 根据id查询车位
     * @author: Hu
     * @since: 2021/8/25 11:12
     * @Param:
     * @return:
     */
    List<CarPositionEntity> getByIds(LinkedList<Long> positionIds);
}
