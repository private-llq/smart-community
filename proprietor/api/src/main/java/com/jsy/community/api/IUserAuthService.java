package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserAuthEntity;

import java.util.List;

public interface IUserAuthService extends IService<UserAuthEntity> {
	List<UserAuthEntity> getList(boolean a);
}
