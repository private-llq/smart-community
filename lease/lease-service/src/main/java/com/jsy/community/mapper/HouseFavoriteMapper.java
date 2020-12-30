package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.lease.HouseFavoriteEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseFavoriteQO;
import com.jsy.community.vo.shop.ShopLeaseVO;

import java.util.List;

/**
 * 房屋租售 Mapper 接口
 * @author YuLF
 * @since 2020-12-29
 */
public interface HouseFavoriteMapper extends BaseMapper<HouseFavoriteEntity> {


    /**
     * 查询房屋收藏列表
     * @author YuLF
     * @since  2020/12/30 11:29
     * @Param  qo           参数对象
     */
    List<ShopLeaseVO> leaseFavorite(BaseQO<HouseFavoriteQO> qo);
}
