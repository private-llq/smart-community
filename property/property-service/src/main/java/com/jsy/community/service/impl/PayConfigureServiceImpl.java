package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPayConfigureService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PayConfigureEntity;
import com.jsy.community.mapper.PayConfigureMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @program: com.jsy.community
 * @description: 支付配置
 * @author: DKS
 * @create: 2021-09-09 09:47
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class PayConfigureServiceImpl extends ServiceImpl<PayConfigureMapper, PayConfigureEntity> implements IPayConfigureService {
	
	@Autowired
	private PayConfigureMapper payConfigureMapper;
	
	
	
	/**
	 * @Description: 查询支付证书状态
	 * @author: DKS
	 * @since: 2021/9/13 14:41
	 * @Param: [companyId]
	 * @return: com.jsy.community.entity.PayConfigureEntity
	 */
	@Override
	public PayConfigureEntity getConfig(Long companyId) {
		PayConfigureEntity payConfigureEntity = new PayConfigureEntity();
		PayConfigureEntity entity = payConfigureMapper.selectOne(new QueryWrapper<PayConfigureEntity>().eq("company_id", companyId));
		if (entity != null){
			if (StringUtils.isNotBlank(payConfigureEntity.getCertPath())){
				payConfigureEntity.setCertPathStatus(1);
			}else {
				payConfigureEntity.setCertPathStatus(0);
			}
			
			if (StringUtils.isNotBlank(payConfigureEntity.getAlipayPublicCertPath())){
				payConfigureEntity.setAlipayPublicCertPathStatus(1);
			}else {
				payConfigureEntity.setAlipayPublicCertPathStatus(0);
			}
			
			if (StringUtils.isNotBlank(payConfigureEntity.getRootCertPath())){
				payConfigureEntity.setRootCertPathStatus(1);
			}else {
				payConfigureEntity.setRootCertPathStatus(0);
			}
			payConfigureEntity.setRefundStatus(entity.getRefundStatus());
			return payConfigureEntity;
		}else {
			payConfigureEntity.setCertPathStatus(0);
			payConfigureEntity.setAlipayPublicCertPathStatus(0);
			payConfigureEntity.setRootCertPathStatus(0);
			payConfigureEntity.setRefundStatus(2);
			return payConfigureEntity;
		}
		
	}
	
	/**
	 * @Description: 更新配置
	 * @author: DKS
	 * @since: 2021/9/13 14:17
	 * @Param: [payConfigureEntity, companyId]
	 * @return: void
	 */
	@Override
	public void basicConfig(PayConfigureEntity payConfigureEntity, Long companyId) {
		PayConfigureEntity entity = payConfigureMapper.selectOne(new QueryWrapper<PayConfigureEntity>().eq("company_id", companyId));
		if (Objects.isNull(entity)){
			PayConfigureEntity configEntity = new PayConfigureEntity();
			BeanUtils.copyProperties(payConfigureEntity,configEntity);
			configEntity.setCompanyId(companyId);
			configEntity.setId(0L);
			payConfigureMapper.insert(configEntity);
		} else {
			if (StringUtils.isNotBlank(payConfigureEntity.getAppId())){
				entity.setAppId(payConfigureEntity.getAppId());
			}
			if (StringUtils.isNotBlank(payConfigureEntity.getPrivateKey())){
				entity.setPrivateKey(payConfigureEntity.getPrivateKey());
			}
			if (StringUtils.isNotBlank(payConfigureEntity.getSellerId())){
				entity.setSellerId(payConfigureEntity.getSellerId());
			}
			if (StringUtils.isNotBlank(payConfigureEntity.getSellerEmail())){
				entity.setSellerEmail(payConfigureEntity.getSellerEmail());
			}
			if (StringUtils.isNotBlank(payConfigureEntity.getSellerPid())){
				entity.setSellerPid(payConfigureEntity.getSellerPid());
			}
			if (StringUtils.isNotBlank(payConfigureEntity.getCertPath())){
				entity.setCertPath(payConfigureEntity.getCertPath());
			}
			if (StringUtils.isNotBlank(payConfigureEntity.getAlipayPublicCertPath())){
				entity.setAlipayPublicCertPath(payConfigureEntity.getAlipayPublicCertPath());
			}
			if (StringUtils.isNotBlank(payConfigureEntity.getRootCertPath())){
				entity.setRootCertPath(payConfigureEntity.getRootCertPath());
			}
			entity.setRefundStatus(payConfigureEntity.getRefundStatus());
			payConfigureMapper.updateById(entity);
		}
	}
}