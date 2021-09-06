package com.jsy.community.api;

import com.jsy.community.entity.property.CarChargeEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.CarChargeQO;
import com.jsy.community.utils.PageInfo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 收费设置服务
 */
public interface ICarChargeService {

    /**
     * 新增收费设置
     * @param carChargeEntity
     * @return
     */
    Integer SaveCarCharge(CarChargeEntity carChargeEntity, Long communityId);

    /**
     * 修改收费设置
     * @param carChargeEntity
     * @return
     */
    Integer UpdateCarCharge(CarChargeEntity carChargeEntity);

    /**
     * 根据id删除一条记录
     * @param uid
     * @return
     */
    Integer DelCarCharge(String uid);

    /**
     * 根据收费类型查询
     * @param type
     * @return
     */
    List<CarChargeEntity> selectCharge(Integer type, Long communityId);

    /**
     * 分页展示所有收费设置
     * @param baseQO
     * @return
     */
    PageInfo listCarChargePage(BaseQO<Integer> baseQO, Long communityId);

    /**
     * 临时停车收费设置
     * @param carChargeEntity
     * @param adminCommunityId
     * @return
     */
    Integer temporaryParkingSet(CarChargeEntity carChargeEntity, Long adminCommunityId);

    BigDecimal testCharge(CarChargeQO carChargeQO);

    BigDecimal charge(CarChargeQO carChargeQO);

    /**
     * @Description: 按社区查询一条月租缴费规则
     * @author: Hu
     * @since: 2021/8/26 14:46
     * @Param:
     * @return:
     */
    CarChargeEntity selectOne(Long communityId);

    List<CarChargeEntity> ListCharge(Long adminCommunityId);

    CarChargeEntity selectOneCharge(String uid, Long adminCommunityId);

    List<CarChargeEntity> ListCharge2(Long adminCommunityId);
}
