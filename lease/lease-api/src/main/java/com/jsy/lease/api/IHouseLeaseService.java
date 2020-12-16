package com.jsy.lease.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.HouseLeaseEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.HouseLeaseQO;
import com.jsy.community.vo.HouseLeaseVO;

import java.util.List;

/**
 * 房屋租售接口提供类
 * @author YuLF
 * @since 2020-12-11 09:21
 */
public interface IHouseLeaseService extends IService<HouseLeaseEntity> {


    /**
     * 新增出租房屋数据
     * @param houseLeaseQO   请求参数接收对象
     * @return               返回新增是否成功
     */
    Boolean addLeaseSaleHouse(HouseLeaseQO houseLeaseQO);


    /**
     * 根据用户id和 rowGuid 删除出租房源数据
     * @param id            业务主键
     * @param userId        用户id
     */
    boolean delLeaseHouse(Long id, String userId);

    /**
     * 根据参数对象条件查询 出租房屋数据
     * @param houseLeaseQO      查询参数对象
     * @return                  返回数据集合
     */
    List<HouseLeaseVO> queryHouseLeaseByList(BaseQO<HouseLeaseQO> houseLeaseQO);
}
