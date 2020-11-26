package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IUserHouseService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.mapper.UserHouseMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * 业主房屋认证 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2020-11-25
 */
@DubboService(version = Const.version, group = Const.group)
public class UserHouseServiceImpl extends ServiceImpl<UserHouseMapper, UserHouseEntity> implements IUserHouseService {

	@Autowired
	private UserHouseMapper userHouseMapper;

	public Boolean saveUserHouse(Long uid, Long communityId, Long houseId){
		UserHouseEntity houseEntity = new UserHouseEntity();
		houseEntity.setUid(uid);
		houseEntity.setCommunityId(communityId);
		houseEntity.setHouseId(houseId);
		int res = userHouseMapper.insert(houseEntity);
		return res>0;
	}

}
