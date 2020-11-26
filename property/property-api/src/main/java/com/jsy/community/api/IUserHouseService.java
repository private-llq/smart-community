package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.UserHouseVo;

/**
 * <p>
 * 业主房屋认证 服务类
 * </p>
 *
 * @author jsy
 * @since 2020-11-25
 */
public interface IUserHouseService extends IService<UserHouseEntity> {
	
	/**
	 * @return com.jsy.community.utils.PageInfo<com.jsy.community.vo.UserHouseVo>
	 * @Author lihao
	 * @Description
	 * @Date 2020/11/26 15:23
	 * @Param [baseQO, communityId]
	 **/
	PageInfo<UserHouseVo> selectUserHouse(BaseQO<UserHouseEntity> baseQO, Long communityId);
}
