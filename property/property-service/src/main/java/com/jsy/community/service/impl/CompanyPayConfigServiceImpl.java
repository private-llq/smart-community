package com.jsy.community.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICompanyPayConfigService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CompanyPayConfigEntity;
import com.jsy.community.mapper.CompanyPayConfigMapper;
import com.jsy.community.utils.AESOperator;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * @program: com.jsy.community
 * @description: 物业公司支付配置
 * @author: Hu
 * @create: 2021-09-10 14:26
 **/
@DubboService(version = Const.version, group = Const.group)
public class CompanyPayConfigServiceImpl extends ServiceImpl<CompanyPayConfigMapper, CompanyPayConfigEntity> implements ICompanyPayConfigService {

    @Autowired
    private CompanyPayConfigMapper companyPayConfigMapper;



    /**
     * @Description: 查询支付配置
     * @author: Hu
     * @since: 2021/9/13 11:35
     * @Param: [propertyId]
     * @return: com.jsy.community.entity.CompanyPayConfigEntity
     */
    @Override
    public CompanyPayConfigEntity getCompanyConfig(Long propertyId) {
        return companyPayConfigMapper.selectOne(new QueryWrapper<CompanyPayConfigEntity>().eq("company_id",propertyId));
    }

    /**
     * @Description: 查询支付公私钥状态
     * @author: Hu
     * @since: 2021/9/10 16:58
     * @Param: [companyId]
     * @return: com.jsy.community.entity.CompanyPayConfigEntity
     */
    @Override
    public CompanyPayConfigEntity getConfig(Long companyId) {
        CompanyPayConfigEntity companyPayConfigEntity = new CompanyPayConfigEntity();
        CompanyPayConfigEntity entity = companyPayConfigMapper.selectOne(new QueryWrapper<CompanyPayConfigEntity>().eq("company_id", companyId));
        if (entity!=null){
            if (entity.getApiclientKeyUrl()!=null){
                companyPayConfigEntity.setApiclientKeyStatus(1);
            }else {
                companyPayConfigEntity.setApiclientKeyStatus(0);
            }

            if (entity.getApiclientCertUrl()!=null){
                companyPayConfigEntity.setApiclientCertStatus(1);
            }else {
                companyPayConfigEntity.setApiclientCertStatus(0);
            }
            companyPayConfigEntity.setRefundStatus(entity.getRefundStatus());
            return companyPayConfigEntity;
        }else {
            companyPayConfigEntity.setApiclientKeyStatus(0);
            companyPayConfigEntity.setApiclientCertStatus(0);
            companyPayConfigEntity.setRefundStatus(2);
            return companyPayConfigEntity;
        }

    }

    /**
     * @Description: 更新基本配置
     * @author: Hu
     * @since: 2021/9/10 16:33
     * @Param: [communityHardWareEntity, companyId]
     * @return: void
     */
    @Override
    public void basicConfig(CompanyPayConfigEntity communityHardWareEntity, Long companyId) {
        CompanyPayConfigEntity entity = companyPayConfigMapper.selectOne(new QueryWrapper<CompanyPayConfigEntity>().eq("company_id", companyId));
        if (Objects.isNull(entity)){
            CompanyPayConfigEntity configEntity = new CompanyPayConfigEntity();
            configEntity.setId(0L);
            configEntity.setCompanyId(companyId);
            if (!"".equals(communityHardWareEntity.getAppId())&&communityHardWareEntity.getAppId()!=null){
                entity.setAppId(AESOperator.encrypt(communityHardWareEntity.getAppId()));
            }
            if (!"".equals(communityHardWareEntity.getAppSecret())&&communityHardWareEntity.getAppSecret()!=null){
                entity.setAppSecret(AESOperator.encrypt(communityHardWareEntity.getAppSecret()));
            }
            if (!"".equals(communityHardWareEntity.getMchId())&&communityHardWareEntity.getMchId()!=null){
                entity.setMchId(AESOperator.encrypt(communityHardWareEntity.getMchId()));
            }
            if (!"".equals(communityHardWareEntity.getApiV3())&&communityHardWareEntity.getApiV3()!=null){
                entity.setApiV3(AESOperator.encrypt(communityHardWareEntity.getApiV3()));
            }
            if (!"".equals(communityHardWareEntity.getPrivateKey())&&communityHardWareEntity.getPrivateKey()!=null){
                entity.setPrivateKey(AESOperator.encrypt(communityHardWareEntity.getPrivateKey()));
            }
            if (!"".equals(communityHardWareEntity.getMchSerialNo())&&communityHardWareEntity.getMchSerialNo()!=null){
                entity.setMchSerialNo(AESOperator.encrypt(communityHardWareEntity.getMchSerialNo()));
            }
            if (!"".equals(communityHardWareEntity.getApiclientKeyUrl())&&communityHardWareEntity.getApiclientKeyUrl()!=null){
                entity.setMchSerialNo(AESOperator.encrypt(communityHardWareEntity.getApiclientKeyUrl()));
            }
            if (!"".equals(communityHardWareEntity.getApiclientCertUrl())&&communityHardWareEntity.getApiclientCertUrl()!=null){
                entity.setMchSerialNo(AESOperator.encrypt(communityHardWareEntity.getApiclientCertUrl()));
            }
            if (communityHardWareEntity.getRefundStatus()!=null&&communityHardWareEntity.getRefundStatus()!=0){
                entity.setRefundStatus(communityHardWareEntity.getRefundStatus());
            }
            companyPayConfigMapper.insert(configEntity);
        } else {
            if (!"".equals(communityHardWareEntity.getAppId())&&communityHardWareEntity.getAppId()!=null){
                entity.setAppId(AESOperator.encrypt(communityHardWareEntity.getAppId()));
            }
            if (!"".equals(communityHardWareEntity.getAppSecret())&&communityHardWareEntity.getAppSecret()!=null){
                entity.setAppSecret(AESOperator.encrypt(communityHardWareEntity.getAppSecret()));
            }
            if (!"".equals(communityHardWareEntity.getMchId())&&communityHardWareEntity.getMchId()!=null){
                entity.setMchId(AESOperator.encrypt(communityHardWareEntity.getMchId()));
            }
            if (!"".equals(communityHardWareEntity.getApiV3())&&communityHardWareEntity.getApiV3()!=null){
                entity.setApiV3(AESOperator.encrypt(communityHardWareEntity.getApiV3()));
            }
            if (!"".equals(communityHardWareEntity.getPrivateKey())&&communityHardWareEntity.getPrivateKey()!=null){
                entity.setPrivateKey(AESOperator.encrypt(communityHardWareEntity.getPrivateKey()));
            }
            if (!"".equals(communityHardWareEntity.getMchSerialNo())&&communityHardWareEntity.getMchSerialNo()!=null){
                entity.setMchSerialNo(AESOperator.encrypt(communityHardWareEntity.getMchSerialNo()));
            }
            if (!"".equals(communityHardWareEntity.getApiclientKeyUrl())&&communityHardWareEntity.getApiclientKeyUrl()!=null){
                entity.setMchSerialNo(AESOperator.encrypt(communityHardWareEntity.getApiclientKeyUrl()));
            }
            if (!"".equals(communityHardWareEntity.getApiclientCertUrl())&&communityHardWareEntity.getApiclientCertUrl()!=null){
                entity.setMchSerialNo(AESOperator.encrypt(communityHardWareEntity.getApiclientCertUrl()));
            }
            if (communityHardWareEntity.getRefundStatus()!=null&&communityHardWareEntity.getRefundStatus()!=0){
                entity.setRefundStatus(communityHardWareEntity.getRefundStatus());
            }
            companyPayConfigMapper.updateById(entity);
        }
    }
}
