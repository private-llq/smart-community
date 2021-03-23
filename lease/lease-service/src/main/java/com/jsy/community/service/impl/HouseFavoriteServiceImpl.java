package com.jsy.community.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IHouseFavoriteService;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.lease.HouseFavoriteEntity;
import com.jsy.community.mapper.HouseFavoriteMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseFavoriteQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.lease.HouseFavoriteVO;
import com.jsy.community.vo.lease.HouseImageVo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author YuLF
 * @since 2020-12-29 09:22
 */
@DubboService(version = Const.version, group = Const.group_lease)
public class HouseFavoriteServiceImpl extends ServiceImpl<HouseFavoriteMapper, HouseFavoriteEntity> implements IHouseFavoriteService {

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
        return houseFavoriteMapper.deleteById(id, userId) > 0;
    }


    /**
     * 查询房屋收藏列表
     * @author YuLF
     * @since  2020/12/30 11:29
     * @Param  qo           参数对象
     */
    @Override
    public List<HouseFavoriteVO> leaseFavorite(BaseQO<HouseFavoriteQO> qo) {
        qo.setPage((qo.getPage() - 1) * qo.getSize());
        List<HouseFavoriteVO> vos = houseFavoriteMapper.leaseFavorite(qo);
        List<Long> houseIds = new ArrayList<>(vos.size());
        //拿到查询列表的所有房屋id  在通过房屋id 查出所有图片  最终设置一张图片到返回图片 用于列表显示
        vos.forEach( v -> houseIds.add(v.getHouseId()));
        if(CollectionUtil.isNotEmpty(houseIds)){
            List<HouseImageVo> imageVos = houseFavoriteMapper.getHouseLeaseImage(houseIds);
            Map<Long, HouseImageVo> collect = imageVos.stream().collect(Collectors.toMap(HouseImageVo::getHid, houseImageVo -> houseImageVo,(value1, value2) -> value1));
            vos.forEach( v -> {
                HouseImageVo houseImageVo = collect.get(v.getHouseId());
                if( Objects.nonNull(houseImageVo) ){
                    v.setHouseImage(houseImageVo.getImgUrl());
                }
            });
        }
        return vos;
    }


    /**
     * 查询我的商铺收藏列表
     * @author YuLF
     * @since  2020/12/30 11:29
     * @Param  qo           参数对象
     */
    @Override
    public List<HouseFavoriteVO> shopFavorite(BaseQO<HouseFavoriteQO> qo) {
        qo.setPage((qo.getPage() - 1) * qo.getSize());
        List<HouseFavoriteVO> vos = houseFavoriteMapper.shopFavorite(qo);
        List<Long> houseIds = new ArrayList<>(vos.size());
        vos.forEach( v -> houseIds.add(v.getHouseId()));
        if(CollectionUtil.isNotEmpty(houseIds)){
            List<HouseImageVo> imageVos = houseFavoriteMapper.getShopImage(houseIds);
            Map<Long, HouseImageVo> collect = imageVos.stream().collect(Collectors.toMap(HouseImageVo::getHid, houseImageVo -> houseImageVo,(value1, value2) -> value1));
            vos.forEach( v -> {
                HouseImageVo houseImageVo = collect.get(v.getHouseId());
                if( Objects.nonNull(houseImageVo) ){
                    v.setHouseImage(houseImageVo.getImgUrl());
                }
            });
        }
        return vos;
    }


    @Override
    public boolean hasHouseOrShop(HouseFavoriteQO qo) {
        //收藏类型：1. 商铺 、2.出租房
        if(qo.getFavoriteType().equals(BusinessConst.SHOP_FAVORITE_TYPE)){
            //商铺
            return houseFavoriteMapper.existShop(qo.getFavoriteId()) > 0;
        }
        //出租房
        return houseFavoriteMapper.existHouse(qo.getFavoriteId()) > 0;
    }
}
