package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarTrackService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarTrackEntity;
import com.jsy.community.mapper.CarTrackMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CarTrackQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * <p>
 * 车辆轨迹 服务实现类
 * </p>
 *
 * @author lihao
 * @since 2021-04-24
 */
@DubboService(version = Const.version, group = Const.group_facility)
@Slf4j
public class CarTrackServiceImpl extends ServiceImpl<CarTrackMapper, CarTrackEntity> implements ICarTrackService {

	@Autowired
	private CarTrackMapper carTrackMapper;
	
	@Override
	public List<CarTrackEntity> test() {
		List<CarTrackEntity> carTrackEntityList = carTrackMapper.selectList(null);
		return carTrackEntityList;
	}
	
	@Override
	public void insertCarTrack(CarTrackEntity carTrackEntity) {
		carTrackEntity.setId(SnowFlake.nextId());
		carTrackMapper.insert(carTrackEntity);
	}
	
	@Override
	public PageInfo<CarTrackEntity> listCarTrack(BaseQO<CarTrackQO> qo) {
		Page<CarTrackEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page,qo);
		List<CarTrackEntity> carTrackEntityList = carTrackMapper.listCarTrack(qo,page);
		for (CarTrackEntity carTrackEntity : carTrackEntityList) {
			carTrackEntity.setCarNumber(carTrackEntity.getCarNumber().trim());
		}
		PageInfo<CarTrackEntity> info = new PageInfo<>();
		BeanUtils.copyProperties(page,info);
		info.setRecords(carTrackEntityList);
		return info;
	}
}
