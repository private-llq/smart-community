package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICommonConstService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommonConst;
import com.jsy.community.mapper.CommonConstMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * <p>
 * 公共常量表 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2020-12-25
 */
@Slf4j
@DubboService(version = Const.version, group = Const.group_proprietor)
public class CommonConstServiceImpl extends ServiceImpl<CommonConstMapper, CommonConst> implements ICommonConstService {
	
	@Resource
	private CommonConstMapper commonConstMapper;
	
}
