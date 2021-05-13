package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.payment.UnionPayWalletBankEntity;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: Pipi
 * @Description: 银联钱包关联银行卡DAO
 * @Date: 2021/4/11 14:25
 * @Version: 1.0
 **/
public interface UnionPayWalletBankMapper extends BaseMapper<UnionPayWalletBankEntity> {
    /**
     *@Author: Pipi
     *@Description: 根据钱包ID设置默认状态
     *@Param: walletId: 钱包ID
     *@Return: java.lang.Integer
     *@Date: 2021/4/14 16:31
     **/
    Integer updateIsDefaultByWalletId(String walletId);

   /**
    *@Author: Pipi
    *@Description: 根据钱包ID和银行卡号设置默认状态
    *@Param: walletId: 钱包ID
	*@Param: bankAcctNo: 银行卡号
    *@Return: java.lang.Integer
    *@Date: 2021/4/14 16:50
    **/
    Integer updateIsDefaultByWalletIdAndBankAcctNo(@Param("walletId") String walletId, @Param("bankAcctNo") String bankAcctNo);

    /**
     *@Author: Pipi
     *@Description: 根据钱包ID和银行账号更新为软删
     *@Param: walletId: 钱包ID
	 *@Param: bankAcctNo: 银行账号
     *@Return: java.lang.Integer
     *@Date: 2021/4/16 10:42
     **/
    Integer updateDeleted(@Param("walletId") String walletId, @Param("bankAcctNo") String bankAcctNo);
}
