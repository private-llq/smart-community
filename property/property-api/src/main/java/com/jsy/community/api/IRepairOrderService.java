package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CommonConst;
import com.jsy.community.entity.RepairOrderEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.RepairOrderQO;
import com.jsy.community.utils.PageInfo;

import java.util.List;

/**
 * <p>
 * 报修订单信息 服务类
 * </p>
 *
 * @author lihao
 * @since 2020-12-08
 */
public interface IRepairOrderService extends IService<RepairOrderEntity> {
	
	/**
	 * @return java.util.List<com.jsy.community.entity.RepairOrderEntity>
	 * @Author lihao
	 * @Description 查询所有报修申请
	 * @Date 2020/12/9 15:51
	 * @Param [communityId]
	 **/
	PageInfo<RepairOrderEntity> listRepairOrder(BaseQO<RepairOrderQO> repairOrderQO);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 处理报修申请
	 * @Date 2020/12/9 16:10
	 * @Param [id]
	 **/
	void dealOrder(Long id);
	
	/**
	 * @return com.jsy.community.entity.UserEntity
	 * @Author lihao
	 * @Description 查询业主信息
	 * @Date 2020/12/9 16:25
	 * @Param [id]
	 **/
	UserEntity getUser(Long id);
	
	/**
	 * @return java.lang.String
	 * @Author lihao
	 * @Description 查看图片信息
	 * @Date 2020/12/9 16:42
	 * @Param [id]
	 **/
	String getOrderImg(Long id);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 完成处理
	 * @Date 2020/12/9 16:51
	 * @Param [id]
	 **/
	void successOrder(Long id);
	
	/**
	 * @return java.util.List<com.jsy.community.entity.CommonConst>
	 * @Author lihao
	 * @Description 报修事项查询
	 * @Date 2021/3/16 17:47
	 * @Param [typeId]
	 **/
	List<CommonConst> listRepairType(Integer typeId);
	
	/**
	 * @return com.jsy.community.entity.RepairOrderEntity
	 * @Author lihao
	 * @Description 根据报修id查询报修信息
	 * @Date 2021/3/18 15:03
	 * @Param [id]
	 **/
	RepairOrderEntity getRepairById(Long id);
}
