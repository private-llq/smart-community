package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseLeaseConstEntity;
import com.jsy.community.entity.admin.AdminUserEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author YuLF
 * @since  2020/12/19 17:54
 */
@Mapper
public interface LeaseConstMapper extends BaseMapper<HouseLeaseConstEntity> {
	

	
}
