package com.jsy.lease.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.HouseLeaseConstEntity;

import java.util.List;

/**
 * 房屋租售接口提供类
 * @author YuLF
 * @since 2020-12-16 09:21
 */
public interface IHouseConstService extends IService<HouseLeaseConstEntity> {

    /**
     *  根据常量类型 获取属于这个类型的List数据
     * @author YuLF
     * @since  2020/12/11 11:36
     * @Param  type				常量类型
     * @return					返回这个类型对应的List
     */
    List<HouseLeaseConstEntity> getHouseConstListByType(String type);
    
    /**
     * @return java.util.List<com.jsy.community.entity.HouseLeaseConstEntity>
     * @Author lihao
     * @Description 根据发布源类型获取其标签
     * @Date 2020/12/17 10:58
     * @Param [id]
     **/
    List<HouseLeaseConstEntity> getTag(Integer id);
}
