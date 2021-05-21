package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserIMEntity;
import org.apache.ibatis.annotations.Select;

/**
 * @author chq459799974
 * @description im用户Mapper
 * @since 2021-04-21 11:44
 **/
public interface UserIMMapper extends BaseMapper<UserIMEntity> {
	
	@Select("select im_id from t_user_im where uid = #{uid}")
	String queryIMIdByUid(String uid);
	
}
