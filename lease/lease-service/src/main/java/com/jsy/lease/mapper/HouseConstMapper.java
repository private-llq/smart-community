package com.jsy.lease.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseLeaseConstEntity;
import com.jsy.community.entity.HouseLeaseEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.HouseLeaseQO;
import com.jsy.community.vo.HouseLeaseVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 房屋租售 Mapper 接口
 * @author YuLF
 * @since 2020-12-10
 */
public interface HouseConstMapper extends BaseMapper<HouseLeaseConstEntity> {

    /**
     * 从t_house_const获取所有房屋常量
     */
    @Select("select house_const_code,house_const_name,house_const_value,house_const_type,annotation from t_house_const")
    List<HouseLeaseConstEntity> getAllHouseConstForDatabases();

}
