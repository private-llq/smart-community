package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarTrackService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarTrackEntity;
import com.jsy.community.mapper.CarTrackMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CarTrackQO;
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
@DubboService(version = Const.version, group = Const.group_property)
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
		Page<CarTrackEntity> page = new Page<>(qo.getPage(), qo.getSize());
		List<CarTrackEntity> carTrackEntityList = carTrackMapper.listCarTrack(qo,page);
		for (CarTrackEntity carTrackEntity : carTrackEntityList) {
			String carNumber = carTrackEntity.getCarNumber();
			
			// TODO: 2021/4/27  这是为了解决查询出来有乱码的问题[会在车牌后面多出几个空格]   可能是因为编码问题[待了解确认]  暂时用这种办法来解决
			if (!carNumber.contains(" ")) {
				// 车牌
				String trimCarNumber = carNumber.trim();
				carTrackEntity.setCarNumber(trimCarNumber);
			}
		}
		PageInfo<CarTrackEntity> info = new PageInfo<>();
		BeanUtils.copyProperties(page,info);
		info.setRecords(carTrackEntityList);
		return info;
	}
}
