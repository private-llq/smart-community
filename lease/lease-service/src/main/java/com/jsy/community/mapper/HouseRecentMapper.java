package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.lease.HouseRecentEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;


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

    /**
     * @author: Pipi
     * @description: 删除用户最近N条记录之外的记录
     * @param: uid: 用户id
     * @param: limitNum: 指定条数
     * @return: java.lang.Integer
     * @date: 2021/6/25 15:36
     **/
    @Delete("DELETE FROM t_house_recent WHERE uid = #{uid} AND id NOT IN ( SELECT s.id FROM((SELECT * FROM t_house_recent WHERE uid = #{uid} and browse_type = 0 ORDER BY create_time DESC LIMIT 25) UNION (SELECT * FROM t_house_recent WHERE uid = #{uid} and browse_type = 1 ORDER BY create_time DESC LIMIT #{limitNum})) as s)")
    Integer deleteByGtFiftyAndUid(@Param("uid") String uid, @Param("limitNum") Integer limitNum);
}
