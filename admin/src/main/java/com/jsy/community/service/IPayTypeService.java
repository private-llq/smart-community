package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.PayTypeEntity;

import java.util.List;

/**
 * <p>
 * 缴费类型 服务类
 * </p>
 *
 * @author lihao
 * @since 2020-12-11
 */
public interface IPayTypeService extends IService<PayTypeEntity> {
	
	/**
	 * @return java.util.List<com.jsy.community.entity.PayTypeEntity>
	 * @Author lihao
	 * @Description 根据城市id查询所有缴费类型 -- 测试环境
	 * @Date 2020/12/11 14:43
	 * @Param [id]
	 **/
	List<PayTypeEntity> getPayType(Long id);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 根据城市id添加缴费类型
	 * @Date 2020/12/11 13:30
	 * @Param [id, payType]
	 **/
	void addPayType(Long id, PayTypeEntity payType);
}
