package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.CommunityRfSycRecordEntity;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 门禁卡同步记录Mapper
 * @Date: 2021/11/3 16:29
 * @Version: 1.0
 **/
public interface CommunityRfSycRecordMapper extends BaseMapper<CommunityRfSycRecordEntity> {
    /**
     * @author: Pipi
     * @description: 批量新增门禁卡同步记录
     * @param rfSycRecordEntities:
     * @return: java.lang.Integer
     * @date: 2021/11/8 10:42
     **/
    Integer batchInsertRecord(List<CommunityRfSycRecordEntity> rfSycRecordEntities);
}
