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
	public PageInfo<CarTrackEntity> listCarTrack(BaseQO<CarTrackQO> qo) {
		Page<CarTrackEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page,qo);
		List<CarTrackEntity> carTrackEntityList = carTrackMapper.listCarTrack(qo,page);
		for (CarTrackEntity carTrackEntity : carTrackEntityList) {
			// TODO: 2021/4/27  这是为了解决查询出来有乱码的问题[会在车牌后面多出几个空格]   可能是因为编码问题[解确认]  暂时用这种办法来解决
			carTrackEntity.setCarNumber(carTrackEntity.getCarNumber().trim());
		}
		PageInfo<CarTrackEntity> info = new PageInfo<>();
		BeanUtils.copyProperties(page,info);
		info.setRecords(carTrackEntityList);
		return info;
	}
}
