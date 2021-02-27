package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.PayCompanyEntity;
import com.jsy.community.entity.PayTypeEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.livingpayment.PayCompanyQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.livingpayment.PayCompanyVO;

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
	List<PayTypeEntity> getPayTypes(Long id);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 根据城市id添加缴费类型
	 * @Date 2020/12/11 13:30
	 * @Param [id, payType]
	 **/
	void addPayType(Long id, PayTypeEntity payType);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 查询支持的缴费单位
	 * @Date 2020/12/11 17:12
	 * @Param [company, type, cityId]
	 **/
	PageInfo<PayCompanyEntity> getPayCompany(BaseQO<PayCompanyEntity> baseQO, Long type, Long cityId);

	/**
	 * @Description: 查询缴费单位
	 * @author: Hu
	 * @since: 2021/2/26 17:50
	 * @Param:
	 * @return:
	 */
	List<PayCompanyVO> selectPayCompany(PayCompanyQO payCompanyQO);
}
