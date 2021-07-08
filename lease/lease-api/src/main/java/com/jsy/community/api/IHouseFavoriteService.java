package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.lease.HouseFavoriteEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseFavoriteQO;
import com.jsy.community.vo.lease.HouseFavoriteVO;

import java.util.List;

/**
 * 房屋租售收藏接口提供类
 * @author YuLF
 * @since 2020-12-11 09:21
 */
public interface IHouseFavoriteService extends IService<HouseFavoriteEntity> {


    /**
     * 房屋收藏
     * @param qo 请求参数
     * @author YuLF
     * @since  2020/12/30 10:51
     * @return  返回成功与否
     */
    Boolean houseFavorite(HouseFavoriteQO qo);

    /**
     * 房屋收藏删除
     * @param id            收藏id
     * @param userId        用户id
     * @author YuLF
     * @since  2020/12/30 11:00
     * @return              返回影响行数 > 0
     */
    Boolean deleteFavorite(Long id, String userId);

    /**
     * 查询我的出租房屋收藏列表
     * @param qo           请求参数，带分页参数
     * @author YuLF
     * @since  2020/12/30 11:29
     * @return              返回数据列表
     */
    List<HouseFavoriteVO> leaseFavorite(BaseQO<HouseFavoriteQO> qo);


    /**
     * 查询我的商铺收藏列表
     * @author YuLF
     * @since  2020/12/30 11:29
    * @Param  qo           参数对象
     */
    List<HouseFavoriteVO > shopFavorite(BaseQO<HouseFavoriteQO> qo);


    /**
     * 通过收藏类型 收藏id  验证房屋是否存在
     * @param qo            请求参数
     * @return              返回是否存在
     */
    boolean hasHouseOrShop(HouseFavoriteQO qo);

    /**
     * @author: Pipi
     * @description: 用户取消收藏
     * @param: qo:
     * @return: java.lang.Integer
     * @date: 2021/7/8 16:07
     **/
    boolean cancelFavorite(HouseFavoriteQO qo);
}
