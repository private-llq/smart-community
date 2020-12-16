package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.CommunityEntity;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author chq459799974
 * @description 社区Mapper
 * @since 2020-11-19 16:57
 **/
public interface CommunityMapper extends BaseMapper<CommunityEntity> {

    /**
     * 根据姓名和城市id查询社区信息
     * @author YuLF
     * @since  2020/12/9 11:37
     * @Param  communityEntity
     * @return 返回社区信息
     */
    List<CommunityEntity> getCommunityByName(CommunityEntity communityEntity);
    
    /**
    * @Description: 小区定位
     * @Param: [ids, location]
     * @Return: com.jsy.community.entity.CommunityEntity
     * @Author: chq459799974
     * @Date: 2020/11/25
    **/
    CommunityEntity locateCommunity(@Param("ids")List<Long> ids, @Param("location")Map<String,Double> location);
    
    /**
    * @Description: 根据社区id批量查询社区名
     * @Param: [list]
     * @Return: java.util.Map<java.lang.Long,java.util.Map<java.lang.Long,java.lang.String>>
     * @Author: chq459799974
     * @Date: 2020/12/16
    **/
    @MapKey("id")
    Map<Long,Map<Long,String>> queryCommunityNameByIdBatch(Collection<Long> list);
    
}
