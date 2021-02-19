package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.lease.HouseRecentEntity;
import org.apache.ibatis.annotations.Delete;


/**
 * 房屋最近浏览Mapper 接口
 * @author YuLF
 * @since 2020-02-19
 */
public interface HouseRecentMapper extends BaseMapper<HouseRecentEntity> {


    /**
     * 根据用户信息删除用户最近浏览
     * @param type          最近浏览数据类型 0表示租房 1表示商铺
     * @param userId        用户id
     * @return              返回影响行数
     */
    @Delete("delete from t_house_recent where uid = #{userId} and browse_type = #{type}")
    Integer deleteByUserInfo(Integer type, String userId);
}
