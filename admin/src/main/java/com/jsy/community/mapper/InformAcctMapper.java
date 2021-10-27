package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.InformAcctEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 推送消息与推送者关系表Mapper
 * @Date: 2021/7/23 15:29
 * @Version: 1.0
 **/
@Mapper
public interface InformAcctMapper extends BaseMapper<InformAcctEntity> {

    /**
     * @author: Pipi
     * @description: 批量新增推送消息与推送者关系数据
     * @param informAcctEntityList:
     * @return: java.lang.Integer
     * @date: 2021/7/23 15:31
     **/
    Integer insertBatch(List<InformAcctEntity> informAcctEntityList);
}
