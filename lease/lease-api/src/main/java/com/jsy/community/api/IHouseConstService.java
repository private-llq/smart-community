package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.HouseLeaseConstEntity;

import java.util.List;
import java.util.Map;

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
    Map<String,Object> getTag();


    /**
     * 通过 常量代码 和常量类型 从缓存中取 名称
     * @param code          常量标识码
     * @param type          常量类型
     * @return              返回常量名称
     */
    String getConstNameByConstTypeCode(Long code, Long type);

    /**
     * 通过 常量代码 和常量类型 从缓存中取 名称 list
     * @param codes          常量标识码
     * @param type          常量类型
     * @return              返回常量名称和常量id
     */
    Map<String, Object>  getConstByTypeCodeForList(List<Long> codes, Long type);
    
    /**
     * @return java.util.List<java.lang.String>
     * @Author lihao
     * @Description 根据常量id集合查询常量名称
     * @Date 2020/12/28 10:18
     * @Param [shopTypeIds]
     **/
    List<String> getConstNameByConstId(Long[] shopTypeIds);
    
    /**
     * @return java.util.List<java.lang.Long>
     * @Author lihao
     * @Description 根据类型查询出常量id
     * @Date 2021/1/7 10:34
     * @Param [i]
     **/
    List<Long> getConstIdByType(Integer i);
}
