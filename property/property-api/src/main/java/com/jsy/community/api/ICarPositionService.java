package com.jsy.community.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.entity.property.CarPositionEntity;
import com.jsy.community.qo.property.InsterCarPositionQO;
import com.jsy.community.qo.property.MoreInsterCarPositionQO;
import com.jsy.community.qo.property.SelectCarPositionPagingQO;
import com.jsy.community.qo.property.UpdateCarPositionQO;

import java.util.Collection;
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

    Integer selectCarPositionUseAmount(Long communityId);

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
    List<CarPositionEntity> getByIds(Collection<Long> positionIds);

    /**
     * @Description: 获取当前小区空置车位
     * @author: Hu
     * @since: 2021/8/25 15:45
     * @Param:
     * @return:
     */
    List<CarPositionEntity> getPosition(Long communityId);

    /**
     * @Description: 业主绑定车辆后修改车位状态
     * @author: Hu
     * @since: 2021/8/26 11:19
     * @Param:
     * @return:
     */
    void bindingMonthCar(CarEntity entity);
    Boolean insterCarPosition(InsterCarPositionQO qo ,Long adminCommunityId);

    Boolean moreInsterCarPosition(MoreInsterCarPositionQO qo, Long adminCommunityId);

    Boolean relieve(Long id);

    Boolean deletedCarPosition(Long id);

    /**
     * @Description: 修改车位状态
     * @author: Hu
     * @since: 2021/8/27 16:57
     * @Param:
     * @return:
     */
    void updateByPosition(Long carPositionId);

    Boolean updateCarPosition(UpdateCarPositionQO qo);

    /**
     * @Description: 查询一条详情
     * @author: Hu
     * @since: 2021/9/3 10:45
     * @Param:
     * @return:
     */
    CarPositionEntity selectOne(Long carPositionId);
    /**
     * @Description: 查詢非業主個月租車位
     * @author: arli
     * @since: 2021/9/2 16:57
     * @Param:
     * @return:
     */
    Integer selectCarPositionVacancy( Long adminCommunityId);
    
    /**
     * @Description: 根据手机号查询绑定车位的id
     * @Param: [mobile]
     * @Return:
     * @Author: DKS
     * @Date: 2021/09/07
     **/
    List<Long> queryBindCarPositionByMobile(String mobile, Long communityId);
}
