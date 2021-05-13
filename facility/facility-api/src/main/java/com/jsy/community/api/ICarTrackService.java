package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CarTrackEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CarTrackQO;
import com.jsy.community.utils.PageInfo;

import java.util.List;

/**
 * <p>
 * 车辆轨迹 服务类
 * </p>
 *
 * @author lihao
 * @since 2021-04-24
 */
public interface ICarTrackService extends IService<CarTrackEntity> {
	
	List<CarTrackEntity> test();
	
	void insertCarTrack(CarTrackEntity carTrackEntity);
	
	PageInfo<CarTrackEntity> listCarTrack(BaseQO<CarTrackQO> carTrackEntityBaseQO);
}
