package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.CompanyPayConfigEntity;
import com.jsy.community.entity.PayConfigureEntity;
import com.jsy.community.mapper.AliConfigMapper;
import com.jsy.community.mapper.WeChatConfigMapper;
import com.jsy.community.service.IWeChatConfigService;
import com.jsy.community.utils.AESOperator;
import com.jsy.community.utils.SnowFlake;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @program: com.jsy.community
 * @description: 微信支付配置
 * @author: DKS
 * @create: 2021-11-10 14:02
 **/
@Service
public class WeChatConfigServiceImpl extends ServiceImpl<WeChatConfigMapper, CompanyPayConfigEntity> implements IWeChatConfigService {

    @Resource
    private WeChatConfigMapper weChatConfigMapper;

    @Resource
    private AliConfigMapper aliConfigMapper;
    
    /**
     * @Description: 查询退款配置状态
     * @author: DKS
     * @since: 2021/11/10 14:29
     * @Param: []
     * @return: java.util.Map
     */
    @Override
    public Map getRefundConfig() {
        Map<String, Integer> map = new HashMap<>();
        CompanyPayConfigEntity entity = weChatConfigMapper.selectOne(new QueryWrapper<CompanyPayConfigEntity>().eq("company_id", 0L).eq("type",1));
        if (entity != null){
            map.put("wechat",entity.getRefundStatus());
        } else {
            map.put("wechat",2);
        }
        PayConfigureEntity one = aliConfigMapper.selectOne(new QueryWrapper<PayConfigureEntity>().eq("company_id", 0L));
        if (one != null){
            map.put("alipay",one.getRefundStatus());
        } else {
            map.put("alipay",2);
        }
        return map;
    }
    
    /**
     * @Description: 查询支付配置状态
     * @author: DKS
     * @since: 2021/11/10 14:31
     * @Param: []
     * @return: java.util.Map
     */
    @Override
    public Map getBasicConfig() {
        Map<String, Integer> map = new HashMap<>();
        CompanyPayConfigEntity entity = weChatConfigMapper.selectOne(new QueryWrapper<CompanyPayConfigEntity>().eq("company_id", 0L).eq("type",1));
        if (entity != null){
            map.put("wechat",1);
        } else {
            map.put("wechat",2);
        }
        PayConfigureEntity one = aliConfigMapper.selectOne(new QueryWrapper<PayConfigureEntity>().eq("company_id", 0L));
        if (one != null){
            map.put("alipay",1);
        } else {
            map.put("alipay",2);
        }
        return map;
    }

    /**
     * @Description: 查询微信支付配置
     * @author: DKS
     * @since: 2021/11/10 14:35
     * @Param: [type]
     * @return: com.jsy.community.entity.CompanyPayConfigEntity
     */
    @Override
    public CompanyPayConfigEntity getCompanyConfig(Integer type) {
        return weChatConfigMapper.selectOne(new QueryWrapper<CompanyPayConfigEntity>().eq("company_id", 0L).eq("type",type));
    }
    
    /**
     * @Description: 查询支付公私钥状态
     * @author: DKS
     * @since: 2021/11/10 14:27
     * @Param: []
     * @return: com.jsy.community.entity.CompanyPayConfigEntity
     */
    @Override
    public CompanyPayConfigEntity getConfig() {
        CompanyPayConfigEntity companyPayConfigEntity = new CompanyPayConfigEntity();
        CompanyPayConfigEntity entity = weChatConfigMapper.selectOne(new QueryWrapper<CompanyPayConfigEntity>().eq("company_id", 0L));
        if (entity != null){
            if (StringUtils.isNotBlank(entity.getApiclientKeyUrl())){
                companyPayConfigEntity.setApiclientKeyStatus(1);
            } else {
                companyPayConfigEntity.setApiclientKeyStatus(0);
            }

            if (StringUtils.isNotBlank(entity.getApiclientCertUrl())){
                companyPayConfigEntity.setApiclientCertStatus(1);
            } else {
                companyPayConfigEntity.setApiclientCertStatus(0);
            }
            companyPayConfigEntity.setRefundStatus(entity.getRefundStatus());
            return companyPayConfigEntity;
        } else {
            companyPayConfigEntity.setApiclientKeyStatus(0);
            companyPayConfigEntity.setApiclientCertStatus(0);
            companyPayConfigEntity.setRefundStatus(2);
            return companyPayConfigEntity;
        }
    }
    
    /**
     * @Description: 更新基本配置
     * @author: DKS
     * @since: 2021/11/10 14:23
     * @Param: [communityHardWareEntity]
     * @return: boolean
     */
    @Override
    public boolean basicConfig(CompanyPayConfigEntity communityHardWareEntity) {
        int row;
        CompanyPayConfigEntity entity = weChatConfigMapper.selectOne(new QueryWrapper<CompanyPayConfigEntity>().eq("company_id", 0L));
        if (Objects.isNull(entity)){
            CompanyPayConfigEntity configEntity = new CompanyPayConfigEntity();
            configEntity.setId(SnowFlake.nextId());
            configEntity.setCompanyId(0L);
            if (StringUtils.isNotBlank(communityHardWareEntity.getAppId())){
                configEntity.setAppId(AESOperator.encrypt(communityHardWareEntity.getAppId()));
            }
            if (StringUtils.isNotBlank(communityHardWareEntity.getAppSecret())){
                configEntity.setAppSecret(AESOperator.encrypt(communityHardWareEntity.getAppSecret()));
            }
            if (StringUtils.isNotBlank(communityHardWareEntity.getMchId())){
                configEntity.setMchId(AESOperator.encrypt(communityHardWareEntity.getMchId()));
            }
            if (StringUtils.isNotBlank(communityHardWareEntity.getApiV3())){
                configEntity.setApiV3(AESOperator.encrypt(communityHardWareEntity.getApiV3()));
            }
            if (StringUtils.isNotBlank(communityHardWareEntity.getPrivateKey())){
                configEntity.setPrivateKey(AESOperator.encrypt(communityHardWareEntity.getPrivateKey()));
            }
            if (StringUtils.isNotBlank(communityHardWareEntity.getMchSerialNo())){
                configEntity.setMchSerialNo(AESOperator.encrypt(communityHardWareEntity.getMchSerialNo()));
            }
            if (StringUtils.isNotBlank(communityHardWareEntity.getApiclientKeyUrl())){
                configEntity.setApiclientKeyUrl(AESOperator.encrypt(communityHardWareEntity.getApiclientKeyUrl()));
            }
            if (StringUtils.isNotBlank(communityHardWareEntity.getApiclientCertUrl())){
                configEntity.setApiclientCertUrl(AESOperator.encrypt(communityHardWareEntity.getApiclientCertUrl()));
            }
            if (communityHardWareEntity.getRefundStatus() != null && communityHardWareEntity.getRefundStatus() != 0){
                configEntity.setRefundStatus(communityHardWareEntity.getRefundStatus());
            }
            if (communityHardWareEntity.getType() != null){
                configEntity.setType(communityHardWareEntity.getType());
            }
            row = weChatConfigMapper.insert(configEntity);
        } else {
            if (StringUtils.isNotBlank(communityHardWareEntity.getAppId())){
                entity.setAppId(AESOperator.encrypt(communityHardWareEntity.getAppId()));
            }
            if (StringUtils.isNotBlank(communityHardWareEntity.getAppSecret())){
                entity.setAppSecret(AESOperator.encrypt(communityHardWareEntity.getAppSecret()));
            }
            if (StringUtils.isNotBlank(communityHardWareEntity.getMchId())){
                entity.setMchId(AESOperator.encrypt(communityHardWareEntity.getMchId()));
            }
            if (StringUtils.isNotBlank(communityHardWareEntity.getApiV3())){
                entity.setApiV3(AESOperator.encrypt(communityHardWareEntity.getApiV3()));
            }
            if (StringUtils.isNotBlank(communityHardWareEntity.getPrivateKey())){
                entity.setPrivateKey(AESOperator.encrypt(communityHardWareEntity.getPrivateKey()));
            }
            if (StringUtils.isNotBlank(communityHardWareEntity.getMchSerialNo())){
                entity.setMchSerialNo(AESOperator.encrypt(communityHardWareEntity.getMchSerialNo()));
            }
            if (StringUtils.isNotBlank(communityHardWareEntity.getApiclientKeyUrl())){
                entity.setApiclientKeyUrl(AESOperator.encrypt(communityHardWareEntity.getApiclientKeyUrl()));
            }
            if (StringUtils.isNotBlank(communityHardWareEntity.getApiclientCertUrl())){
                entity.setApiclientCertUrl(AESOperator.encrypt(communityHardWareEntity.getApiclientCertUrl()));
            }
            if (communityHardWareEntity.getRefundStatus() !=null && communityHardWareEntity.getRefundStatus() != 0){
                entity.setRefundStatus(communityHardWareEntity.getRefundStatus());
            }
            if (communityHardWareEntity.getType() != null){
                entity.setType(communityHardWareEntity.getType());
            }
            row = weChatConfigMapper.updateById(entity);
        }
        return row >= 1;
    }
}
