package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.StrangerRecordEntiy;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 陌生人脸记录Mapper
 * @Date: 2021/8/26 15:06
 * @Version: 1.0
 **/
public interface StrangerRecordMapper extends BaseMapper<StrangerRecordEntiy> {

    /**
     * @author: Pipi
     * @description: 批量新增陌生人脸记录
     * @param strangerRecordEntiys:
     * @return: java.lang.Integer
     * @date: 2021/8/26 15:29
     **/
    Integer batchInsertStrangerRecord(List<StrangerRecordEntiy> strangerRecordEntiys);
}