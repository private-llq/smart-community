package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.payment.UnionPayWalletEntity;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: Pipi
 * @Description: 银联钱包DAO
 * @Date: 2021/4/11 14:26
 * @Version: 1.0
 **/
public interface UnionPayWalletMapper extends BaseMapper<UnionPayWalletEntity> {
    /**.
     *@Author: Pipi
     *@Description: 根据钱包ID更新手机号码
     *@Param: mobileNo: 手机号码
     *@Param: walletId: 钱包ID
     *@Return: java.lang.Integer
     *@Date: 2021/4/14 11:40
     **/
    Integer updateMobileNoByWalletId(@Param("mobileNo") String mobileNo, @Param("walletId") String walletId);
}
