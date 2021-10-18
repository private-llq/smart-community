package com.jsy.community.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.lease.HouseLeaseEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HouseLeaseMapper extends BaseMapper<HouseLeaseEntity> {
    /**
     * 按图片id查出所有图片
     *
     * @param houseImageId 图片id
     * @return 返回List数据
     */
    @Select("select img_url from t_house_image where field_id = #{houseImageId}")
    List<String> queryHouseAllImgById(@Param("houseImageId") Long houseImageId);
}
