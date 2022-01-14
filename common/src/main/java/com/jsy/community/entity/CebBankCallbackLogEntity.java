package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2022/1/14 9:55
 * @Version: 1.0
 **/
@Data
@TableName("t_ceb_bank_callback_log")
public class CebBankCallbackLogEntity extends BaseEntity {
    /**
     * 接收的内容
     */
    private String content;
    /**
     * ok或者error
     */
    private String result;
}
