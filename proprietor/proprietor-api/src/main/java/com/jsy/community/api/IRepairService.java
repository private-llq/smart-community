package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.RepairEntity;
import com.jsy.community.vo.repair.RepairVO;

import java.util.List;

/**
 * <p>
 * 房屋报修 服务类
 * </p>
 *
 * @author lihao
 * @since 2020-12-08
 */
public interface IRepairService extends IService<RepairEntity> {
	
	/**
	 * @return java.util.List<com.jsy.community.entity.RepairEntity>
	 * @Author lihao
	 * @Description 测试
	 * @Date 2020/12/8 11:35
	 * @Param []
	 **/
	List<RepairEntity> testList();
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 添加房屋报修信息
	 * @Date 2020/12/8 12:23
	 * @Param [repairEntity]
	 **/
	void addRepair(RepairEntity repairEntity, String uid);
	
	/**
	 * @return com.jsy.community.entity.RepairEntity
	 * @Author lihao
	 * @Description 房屋报修查询
	 * @Date 2020/12/8 12:30
	 * @Param [id]
	 **/
	List<RepairEntity> getRepair(String id);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 房屋取消报修
	 * @Date 2020/12/8 12:41
	 * @Param [id, userId]
	 **/
	void cancelRepair(Long id, String userId);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 完成报修
	 * @Date 2020/12/8 14:23
	 * @Param [id, userId]
	 **/
	void completeRepair(Long id, Long userId);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description
	 * @Date 2020/12/9 10:46
	 * @Param [id, appraise, uid, status]
	 **/
	void appraiseRepair(Long id, String appraise, String uid, Integer status);
	
	/**
	 * @return com.jsy.community.vo.CommonResult
	 * @Author lihao
	 * @Description 报修详情
	 * @Date 2020/12/8 15:11
	 * @Param [id, userId]
	 **/
	RepairVO repairDetails(Long id, String userId);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description
	 * @Date 2020/12/8 15:26
	 * @Param [userId]
	 **/
	void deleteAppraise(Long id);
}