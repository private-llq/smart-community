package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.PeopleTrackEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.PeopleTrackQO;
import com.jsy.community.utils.PageInfo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lihao
 * @since 2021-04-26
 */
public interface IPeopleTrackService extends IService<PeopleTrackEntity> {
	
	void insertPeopleTrack(PeopleTrackEntity peopleTrackEntity);
	
	/**
	 * @return com.jsy.community.utils.PageInfo<com.jsy.community.entity.PeopleTrackEntity>
	 * @Author 91李寻欢
	 * @Description 分页查询车辆轨迹
	 * @Date 2021/4/29 9:47
	 * @Param [peopleQo]
	 **/
	PageInfo<PeopleTrackEntity> listPeopleTrack(BaseQO<PeopleTrackQO> peopleQo);
}
