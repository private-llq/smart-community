package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.entity.CarTrackEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CarTrackQO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 车辆轨迹 Mapper 接口
 * </p>
 *
 * @author lihao
 * @since 2021-04-24
 */
public interface CarTrackMapper extends BaseMapper<CarTrackEntity> {
	
	/**
	 * @return void
	 * @Author 91李寻欢
	 * @Description 分页条件查询
	 * @Date 2021/4/25 9:59
	 * @Param [qo, page]
	 **/
	List<CarTrackEntity> listCarTrack(@Param("qo") BaseQO<CarTrackQO> qo, @Param("page") Page<CarTrackEntity> page);
}
