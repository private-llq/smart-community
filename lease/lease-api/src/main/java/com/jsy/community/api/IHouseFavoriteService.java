package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.lease.HouseFavoriteEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseFavoriteQO;
import com.jsy.community.vo.shop.ShopLeaseVO;

import java.util.List;

/**
 * 房屋租售收藏接口提供类
 * @author YuLF
 * @since 2020-12-11 09:21
 */
public interface IHouseFavoriteService extends IService<HouseFavoriteEntity> {


    /**
     * 房屋收藏
     * @author YuLF
     * @since  2020/12/30 10:51
     * @Param  qo         请求必要参数：uid（服务端）、收藏id、收藏类型
     */
    Boolean houseFavorite(HouseFavoriteQO qo);

    /**
     * 房屋收藏删除
     * @author YuLF
     * @since  2020/12/30 11:00
     * @Param   id              收藏id
     * @Param   userId          用户ID
     */
    Boolean deleteFavorite(Long id, String userId);

    /**
     * 查询我的出租房屋收藏列表
     * @author YuLF
     * @since  2020/12/30 11:29
     * @Param  qo           参数对象
     */
    List<ShopLeaseVO> leaseFavorite(BaseQO<HouseFavoriteQO> qo);


    /**
     * 查询我的商铺收藏列表
     * @author YuLF
     * @since  2020/12/30 11:29
     * @Param  qo           参数对象
     */
    List<ShopLeaseVO> shopFavorite(BaseQO<HouseFavoriteQO> qo);
}
