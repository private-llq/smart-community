package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IHouseFavoriteService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.lease.HouseFavoriteEntity;
import com.jsy.community.mapper.HouseFavoriteMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseFavoriteQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.shop.ShopLeaseVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author YuLF
 * @since 2020-12-29 09:22
 */
@DubboService(version = Const.version, group = Const.group_lease)
public class IHouseFavoriteServiceImpl extends ServiceImpl<HouseFavoriteMapper, HouseFavoriteEntity> implements IHouseFavoriteService {

    @Resource
    private HouseFavoriteMapper houseFavoriteMapper;


    /**
     * 房屋收藏
     * @author YuLF
     * @since  2020/12/30 10:51
     * @Param  qo         请求必要参数：uid（服务端）、收藏id、收藏房屋类型（1商铺 2租房）
     */
    @Override
    public Boolean houseFavorite(HouseFavoriteQO qo) {
        HouseFavoriteEntity entity = HouseFavoriteEntity.getInstance();
        BeanUtils.copyProperties( qo, entity );
        entity.setId(SnowFlake.nextId());
        return houseFavoriteMapper.insert(entity) > 0;
    }


    /**
     * 房屋收藏删除
     * @author YuLF
     * @since  2020/12/30 11:00
     * @Param   id              收藏房屋ID
     * @Param   userId          用户ID
     */
    @Override
    public Boolean deleteFavorite(Long id, String userId) {
        UpdateWrapper<HouseFavoriteEntity> wrapper  = new UpdateWrapper<>();
        wrapper.eq("id",id);
        wrapper.eq("uid",userId);
        return houseFavoriteMapper.delete(wrapper) > 0;
    }


    /**
     * 查询房屋收藏列表
     * @author YuLF
     * @since  2020/12/30 11:29
     * @Param  qo           参数对象
     */
    @Override
    public List<ShopLeaseVO> leaseFavorite(BaseQO<HouseFavoriteQO> qo) {
        qo.setPage((qo.getPage() - 1) * qo.getSize());

        return houseFavoriteMapper.leaseFavorite(qo);
    }


    /**
     * 查询我的商铺收藏列表
     * @author YuLF
     * @since  2020/12/30 11:29
     * @Param  qo           参数对象
     */
    @Override
    public List<ShopLeaseVO> shopFavorite(BaseQO<HouseFavoriteQO> qo) {
        return null;
    }
}
