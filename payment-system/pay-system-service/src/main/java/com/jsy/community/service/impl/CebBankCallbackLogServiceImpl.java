package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.CebBankCallbackLogService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CebBankCallbackLogEntity;
import com.jsy.community.mapper.CebBankCallbackLogMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2022/1/14 10:04
 * @Version: 1.0
 **/
@DubboService(version = Const.version, group = Const.group_payment)
public class CebBankCallbackLogServiceImpl extends ServiceImpl<CebBankCallbackLogMapper, CebBankCallbackLogEntity> implements CebBankCallbackLogService {
}
