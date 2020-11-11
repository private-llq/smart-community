package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IUserAuthService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.UserAuthMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

@DubboService(version = Const.version, group = Const.group)
public class IUserAuthServiceImpl extends ServiceImpl<UserAuthMapper, UserAuthEntity> implements IUserAuthService {
	@Override
	public List<UserAuthEntity> getList(boolean a) {
		if (a) {
			throw new ProprietorException(JSYError.DUPLICATE_KEY);
		}
		return list();
	}
}
