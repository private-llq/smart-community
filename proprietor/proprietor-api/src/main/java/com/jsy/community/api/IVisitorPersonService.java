package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.VisitorPersonEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.VisitorPersonQO;
import com.jsy.community.utils.PageInfo;

/**
 * <p>
 * 访客随行人员 服务类
 * </p>
 *
 * @author qq459799974
 * @since 2020-11-12
 */
public interface IVisitorPersonService extends IService<VisitorPersonEntity> {
	
	/**
	 * @Description: 添加随行人员
	 * @Param: [visitorPersonEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/10
	 **/
	boolean addVisitorPerson(VisitorPersonEntity visitorPersonEntity);
	
	/**
	 * @Description: 修改随行人员
	 * @Param: [visitorPersonQO]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	boolean updateVisitorPersonById(VisitorPersonQO visitorPersonQO);
	
	/**
	 * @Description: 删除随行人员
	 * @Param: [id]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	boolean deleteVisitorPersonById(Long id);
	
	/**
	* @Description: 随行人员 分页查询
	 * @Param: [baseQO]
	 * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.VisitorPersonEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/10
	**/
	PageInfo<VisitorPersonEntity> queryVisitorPersonPage(BaseQO<String> baseQO);
	
}
