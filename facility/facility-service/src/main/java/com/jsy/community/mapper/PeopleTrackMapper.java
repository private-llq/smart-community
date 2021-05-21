package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.entity.PeopleTrackEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.PeopleTrackQO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author lihao
 * @since 2021-04-26
 */
public interface PeopleTrackMapper extends BaseMapper<PeopleTrackEntity> {
	
	/**
	 * @return java.util.List<com.jsy.community.entity.PeopleTrackEntity>
	 * @Author 91李寻欢
	 * @Description 分页条件查询人员轨迹
	 * @Date 2021/4/29 9:51
	 * @Param [qo, page]
	 **/
	List<PeopleTrackEntity> listPeopleTrack(@Param("qo") BaseQO<PeopleTrackQO> qo, @Param("page") Page<PeopleTrackEntity> page);
}
