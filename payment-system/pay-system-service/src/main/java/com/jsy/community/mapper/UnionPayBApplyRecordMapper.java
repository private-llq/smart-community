package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.payment.UnionPayBApplyRecordEntity;
import com.jsy.community.qo.unionpay.CredentialNotifyQO;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: Pipi
 * @Description: B端用户开户申请记录Mapper
 * @Date: 2021/5/10 15:19
 * @Version: 1.0
 **/
public interface UnionPayBApplyRecordMapper extends BaseMapper<UnionPayBApplyRecordEntity> {

    /**
     *@Author: Pipi
     *@Description: 根据注册号查询最新的注册变更记录
     *@Param: credentialNotifyQO:
     *@Return: com.jsy.community.entity.payment.UnionPayBApplyRecordEntity
     *@Date: 2021/5/11 10:54
     **/
    UnionPayBApplyRecordEntity selectOneByRegisterNo(@Param("credentialNotifyQO") CredentialNotifyQO credentialNotifyQO);
}
