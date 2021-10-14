package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserAccountEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

/**
 * @author chq459799974
 * @description 用户账户Mapper
 * @since 2021-01-08 11:09
 **/
public interface UserAccountMapper extends BaseMapper<UserAccountEntity> {

    /**
     * @Description: 修改余额(收入支出)
     * @Param: [amount, uid]
     * @Return: int
     * @Author: chq459799974
     * @Date: 2021/1/8
     **/
    @Update("update t_user_account set balance = balance + #{amount} where uid = #{uid}")
    int updateBalance(@Param("amount") BigDecimal amount, @Param("uid") String uid);

    /**
     * @Description: 修改余额(收入支出), 加个乐观锁
     * @Param: [balance, uid, before]
     * @Return: int
     * @Author: chq459799974
     * @Date: 2021/1/8
     **/
    @Update("update t_user_account set balance = #{balance} where uid = #{uid} and balance = #{before}")
    int updateBalanceByBalance(@Param("balance") BigDecimal balance, @Param("uid") String uid, @Param("before") BigDecimal before);

}
