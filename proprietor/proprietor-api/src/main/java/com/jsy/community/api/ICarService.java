package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.entity.CarOrderEntity;
import com.jsy.community.entity.CarOrderRecordEntity;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.property.CarPositionEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.CarQO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 车辆 服务提供类
 * @author YuLF
 * @since 2020-11-10
 */
public interface ICarService extends IService<CarEntity> {


    /**
     * 查询所属人车辆分页方法
     * @param param   车辆分页条件查询对象
     * @return        返回当前页数据
     */
    Page<CarEntity> queryProprietorCar(BaseQO<CarEntity> param);

    /**
     * 通过车辆信息更新车辆方法
     * @param carEntity 车辆对象实体
     * @param uid       用户id
     * @return          返回修改影响行数
     */
    Integer updateProprietorCar(CarQO carEntity,String uid);

    /**
     * 通过车辆id逻辑删除车辆方法
     * @param params    参数列表
     * @return          返回修改影响行数
     */
    Integer deleteProprietorCar(Map<String, Object> params);


    /**
     * 新增车辆方法
     * @param carEntity 车辆参数对象
     * @return          返回插入影响行数
     */
    Integer addProprietorCar(CarEntity carEntity);


    /**
     * 根据车牌条件查询某一记录数
     * @param carPlate  车牌
     * @return          返回车辆是否存在布尔值
     */
    Boolean carIsExist(String carPlate);


    /**
     * 业主登记时，调用车辆登记接口登记业主的车辆
     * @param cars   业主车辆信息 列表
     * @param uid    用户id
     */
    void addProprietorCarForList(List<CarQO> cars, String uid);


    /**
     * 按用户id查出用户所有车辆信息
     * @param userId        用户id
     * @return              返回业主车辆信息
     */
    List<CarEntity> queryUserCarById(String userId);


    /**
     * 按用户id 和社区id 查询用户的车辆
     * @param communityId   社区id
     * @param userId        用户id
     * @return              返回用户车辆信息
     */
    List<CarEntity> getAllCarById(Long communityId, String userId);

    /**
     * 手动通过uid和 车辆信息进行更新车辆
     * @param c             车辆信息
     * @param uid           用户id
     */
    void update(CarQO c, String uid);

    /**
     * @Description: 新app添加车辆
     * @author: Hu
     * @since: 2021/8/21 10:08
     * @Param:
     * @return:
     */
    void addRelationCar(CarEntity carEntity);

    /**
     * @Description: 新app添加车辆
     * @author: Hu
     * @since: 2021/8/21 10:08
     * @Param:
     * @return:
     */
    void updateRelationCar(CarEntity carEntity);

    /**
     * @Description: 新app查询车辆
     * @author: Hu
     * @since: 2021/8/21 10:17
     * @Param:
     * @return:
     */
    List<CarEntity> getCars(CarEntity carEntity,String uid);

    /**
     * @Description: 新app删除车辆
     * @author: Hu
     * @since: 2021/8/21 10:39
     * @Param:
     * @return:
     */
    void delete(Long id, String userId);

    /**
     * @Description: 获取当前小区空置车位
     * @author: Hu
     * @since: 2021/8/25 15:44
     * @Param:
     * @return:
     */
    List<CarPositionEntity> getPosition(Long communityId);

    /**
     * @Description: 绑定月租车辆
     * @author: Hu
     * @since: 2021/8/26 11:12
     * @Param:
     * @return:
     */
    void bindingMonthCar(CarOrderRecordEntity entity);

    /**
     * @Description: 续费月租车辆
     * @author: Hu
     * @since: 2021/8/26 11:51
     * @Param:
     * @return:
     */
    void renewMonthCar(CarOrderRecordEntity entity);
    /**
     * @Description: 获取车位费
     * @author: Hu
     * @since: 2021/8/26 14:42
     * @Param:
     * @return:
     */
    BigDecimal payPositionFees(CarEntity carEntity);

    /**
     * @Description: 查询月租缴费订单
     * @author: Hu
     * @since: 2021/8/26 17:31
     * @Param:
     * @return:
     */
    Map<String, Object> MonthOrder(BaseQO<CarEntity> baseQO, String userId);

    /**
     * @Description: 续费月租临时订单记录表
     * @author: Hu
     * @since: 2021/8/26 14:42
     * @Param: [carEntity]
     * @return: java.math.BigDecimal
     */
    Long  renewRecord(CarEntity carEntity);


    /**
     * @Description: 绑定月租临时订单记录表
     * @author: Hu
     * @since: 2021/8/26 14:42
     * @Param: [carEntity]
     * @return: java.math.BigDecimal
     */
    Long bindingRecord(CarEntity carEntity);

    /**
     * @Description: 查询一条车辆缴费临时订单
     * @author: Hu
     * @since: 2021/8/27 13:56
     * @Param:
     * @return:
     */
    CarOrderRecordEntity findOne(Long id);

    /**
     * @Description: 解除月租车辆绑定关系
     * @author: Hu
     * @since: 2021/8/27 16:33
     * @Param:
     * @return:
     */
    void deleteMonthCar(Long id);

    /**
     * @Description: 查询一条详情
     * @author: Hu
     * @since: 2021/9/27 13:41
     * @Param:
     * @return:
     */
    CarOrderEntity getOrder(Long id);

    /**
     * @Description: 查询小区名称
     * @author: Hu
     * @since: 2021/10/26 14:40
     * @Param:
     * @return:
     */
    CommunityEntity selectCommunityName(Long communityId);

    /**
     * @Description: 查询临时车账单
     * @author: Hu
     * @since: 2021/10/26 15:33
     * @Param:
     * @return:
     */
    List<CarOrderEntity> getTemporaryOrder(Long communityId, String userId);
    /**
     * @Description: 查询临时车账单详情
     * @author: Hu
     * @since: 2021/10/26 15:33
     * @Param:
     * @return:
     */
    CarOrderEntity getTemporaryOrderById(Long id, String userId);

    /**
     * @Description: 修改临时车辆订单状态
     * @author: Hu
     * @since: 2021/10/26 16:43
     * @Param: 
     * @return: 
     */
    void updateByOrder(String id,BigDecimal total,String outTradeNo,Integer payType);
}
