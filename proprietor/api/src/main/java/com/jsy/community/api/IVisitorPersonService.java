package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.VisitorPerson;

import java.util.List;

/**
 * <p>
 * 访客随行人员 服务类
 * </p>
 *
 * @author jsy
 * @since 2020-11-12
 */
public interface IVisitorPersonService extends IService<VisitorPerson> {
	
	/**
	* @Description: 根据关联的访客表ID 列表查询
	 * @Param: [visitorid]
	 * @Return: java.util.List<com.jsy.community.entity.VisitorPerson>
	 * @Author: chq459799974
	 * @Date: 2020/11/12
	**/
	 List<VisitorPerson> queryPersonList(Long visitorid);
	 
}
