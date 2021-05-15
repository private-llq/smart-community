package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CommonConst;

import java.util.List;

/**
 * <p>
 * 公共常量表 服务类
 * </p>
 *
 * @author jsy
 * @since 2020-12-25
 */
public interface ICommonConstService extends IService<CommonConst> {
	
	List<Long> listByType(Long i);
	
	/**
	 * @return java.util.List<com.jsy.community.entity.CommonConst>
	 * @Author lihao
	 * @Description 查询所有商铺类型
	 * @Date 2021/1/13 9:48
	 * @Param []
	 **/
	List<CommonConst> getShopType();
	
	/**
	 * @return java.util.List<com.jsy.community.entity.CommonConst>
	 * @Author lihao
	 * @Description 查询所有商铺商业
	 * @Date 2021/1/13 9:50
	 * @Param []
	 **/
	List<CommonConst> getBusiness();
	
	/**
	 * @return com.jsy.community.entity.CommonConst
	 * @Author lihao
	 * @Description 根据id查询常量
	 * @Date 2021/1/13 11:15
	 * @Param [shopTypeId]
	 **/
	CommonConst getConstById(Long shopTypeId);
	
	/**
	 * @return java.util.List<com.jsy.community.entity.CommonConst>
	 * @Author 91李寻欢
	 * @Description 获取设备作用
	 * @Date 2021/4/23 11:46
	 * @Param []
	 **/
	List<CommonConst> getFacilityTypeEffect();
	
}
