package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.PayConfigureEntity;
import com.jsy.community.mapper.AliConfigMapper;
import com.jsy.community.service.IAliConfigService;
import com.jsy.community.utils.AESOperator;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @program: com.jsy.community
 * @description: 支付宝支付配置
 * @author: DKS
 * @create: 2021-11-10 14:06
 **/
@Slf4j
@Service
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class AliConfigServiceImpl extends ServiceImpl<AliConfigMapper, PayConfigureEntity> implements IAliConfigService {
	
	@Resource
	private AliConfigMapper payConfigureMapper;
	
	/**
	 * @Description: 查询支付证书状态
	 * @author: DKS
	 * @since: 2021/11/10 14:55
	 * @Param: []
	 * @return: com.jsy.community.entity.PayConfigureEntity
	 */
	@Override
	public PayConfigureEntity getConfig() {
		PayConfigureEntity payConfigureEntity = new PayConfigureEntity();
		PayConfigureEntity entity = payConfigureMapper.selectOne(new QueryWrapper<PayConfigureEntity>().eq("company_id", 0L));
		if (entity != null){
			if (StringUtils.isNotBlank(entity.getCertPath())){
				payConfigureEntity.setCertPathStatus(1);
			} else {
				payConfigureEntity.setCertPathStatus(0);
			}
			
			if (StringUtils.isNotBlank(entity.getAlipayPublicCertPath())){
				payConfigureEntity.setAlipayPublicCertPathStatus(1);
			} else {
				payConfigureEntity.setAlipayPublicCertPathStatus(0);
			}
			
			if (StringUtils.isNotBlank(entity.getRootCertPath())){
				payConfigureEntity.setRootCertPathStatus(1);
			} else {
				payConfigureEntity.setRootCertPathStatus(0);
			}
			payConfigureEntity.setRefundStatus(entity.getRefundStatus());
			return payConfigureEntity;
		} else {
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
	 * @since: 2021/11/10 14:48
	 * @Param: [payConfigureEntity]
	 * @return: boolean
	 */
	@Override
	public boolean basicConfig(PayConfigureEntity payConfigureEntity) {
		int row;
		PayConfigureEntity entity = payConfigureMapper.selectOne(new QueryWrapper<PayConfigureEntity>().eq("company_id", 0L));
		if (Objects.isNull(entity)){
			PayConfigureEntity configEntity = new PayConfigureEntity();
			configEntity.setCompanyId(0L);
			configEntity.setId(SnowFlake.nextId());
			if (StringUtils.isNotBlank(payConfigureEntity.getAppId())){
				configEntity.setAppId(AESOperator.encrypt(payConfigureEntity.getAppId()));
			}
			if (StringUtils.isNotBlank(payConfigureEntity.getPrivateKey())){
				configEntity.setPrivateKey(AESOperator.encrypt(payConfigureEntity.getPrivateKey()));
			}
			if (StringUtils.isNotBlank(payConfigureEntity.getSellerId())){
				configEntity.setSellerId(AESOperator.encrypt(payConfigureEntity.getSellerId()));
			}
			if (StringUtils.isNotBlank(payConfigureEntity.getSellerEmail())){
				configEntity.setSellerEmail(AESOperator.encrypt(payConfigureEntity.getSellerEmail()));
			}
			if (StringUtils.isNotBlank(payConfigureEntity.getSellerPid())){
				configEntity.setSellerPid(AESOperator.encrypt(payConfigureEntity.getSellerPid()));
			}
			if (StringUtils.isNotBlank(payConfigureEntity.getCertPath())){
				configEntity.setCertPath(AESOperator.encrypt(payConfigureEntity.getCertPath()));
			}
			if (StringUtils.isNotBlank(payConfigureEntity.getAlipayPublicCertPath())){
				configEntity.setAlipayPublicCertPath(AESOperator.encrypt(payConfigureEntity.getAlipayPublicCertPath()));
			}
			if (StringUtils.isNotBlank(payConfigureEntity.getRootCertPath())){
				configEntity.setRootCertPath(AESOperator.encrypt(payConfigureEntity.getRootCertPath()));
			}
			row = payConfigureMapper.insert(configEntity);
		} else {
			if (StringUtils.isNotBlank(payConfigureEntity.getAppId())){
				entity.setAppId(AESOperator.encrypt(payConfigureEntity.getAppId()));
			}
			if (StringUtils.isNotBlank(payConfigureEntity.getPrivateKey())){
				entity.setPrivateKey(AESOperator.encrypt(payConfigureEntity.getPrivateKey()));
			}
			if (StringUtils.isNotBlank(payConfigureEntity.getSellerId())){
				entity.setSellerId(AESOperator.encrypt(payConfigureEntity.getSellerId()));
			}
			if (StringUtils.isNotBlank(payConfigureEntity.getSellerEmail())){
				entity.setSellerEmail(AESOperator.encrypt(payConfigureEntity.getSellerEmail()));
			}
			if (StringUtils.isNotBlank(payConfigureEntity.getSellerPid())){
				entity.setSellerPid(AESOperator.encrypt(payConfigureEntity.getSellerPid()));
			}
			if (StringUtils.isNotBlank(payConfigureEntity.getCertPath())){
				entity.setCertPath(AESOperator.encrypt(payConfigureEntity.getCertPath()));
			}
			if (StringUtils.isNotBlank(payConfigureEntity.getAlipayPublicCertPath())){
				entity.setAlipayPublicCertPath(AESOperator.encrypt(payConfigureEntity.getAlipayPublicCertPath()));
			}
			if (StringUtils.isNotBlank(payConfigureEntity.getRootCertPath())){
				entity.setRootCertPath(AESOperator.encrypt(payConfigureEntity.getRootCertPath()));
			}
			if (payConfigureEntity.getRefundStatus() != null) {
				entity.setRefundStatus(payConfigureEntity.getRefundStatus());
			}
			row = payConfigureMapper.updateById(entity);
		}
		return row >= 1;
	}
	
	/**
	 * @Description: 查询小区支付配置
	 * @author: DKS
	 * @since: 2021/11/10 14:56
	 * @Param: []
	 * @return: com.jsy.community.entity.PayConfigureEntity
	 */
	@Override
	public PayConfigureEntity getCompanyConfig() {
		return payConfigureMapper.selectOne(new QueryWrapper<PayConfigureEntity>().eq("company_id", 0L));
	}
}