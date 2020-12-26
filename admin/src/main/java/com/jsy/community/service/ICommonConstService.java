package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CommonConst;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;

/**
 * <p>
 * 公共常量表 服务类
 * </p>
 *
 * @author jsy
 * @since 2020-12-25
 */
public interface ICommonConstService extends IService<CommonConst> {
	
	/**
	 * @return com.jsy.community.utils.PageInfo<com.jsy.community.entity.CommonConst>
	 * @Author lihao
	 * @Description 根据常量所属编号查询所有常量
	 * @Date 2020/12/25 16:40
	 * @Param [constId, baseQO]
	 **/
	PageInfo<CommonConst> getConst(Integer constId, BaseQO<CommonConst> baseQO);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 添加常量
	 * @Date 2020/12/25 17:29
	 * @Param [commonConst]
	 **/
	void addConst(CommonConst commonConst);
}
