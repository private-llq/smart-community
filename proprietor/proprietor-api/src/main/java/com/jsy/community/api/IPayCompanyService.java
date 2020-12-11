package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.PayCompanyEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;

/**
 * <p>
 * 缴费单位 服务类
 * </p>
 *
 * @author lihao
 * @since 2020-12-11
 */
public interface IPayCompanyService extends IService<PayCompanyEntity> {
	
	/**
	 * @return
	 * @Author lihao
	 * @Description 查询所有缴费单位
	 * @Date 2020/12/11 15:42
	 * @Param
	 **/
	PageInfo<PayCompanyEntity> getPayCompany(BaseQO<PayCompanyEntity> baseQO);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 添加缴费公司
	 * @Date 2020/12/11 15:28
	 * @Param [companyEntity]
	 **/
	void addPayCompany(PayCompanyEntity companyEntity);
}
