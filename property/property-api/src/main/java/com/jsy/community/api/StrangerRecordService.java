package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.StrangerRecordEntiy;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;

/**
 * @Author: Pipi
 * @Description: 陌生人脸记录服务
 * @Date: 2021/8/26 15:09
 * @Version: 1.0
 **/
public interface StrangerRecordService extends IService<StrangerRecordEntiy> {

    /**
     * @author: Pipi
     * @description: 批量新增陌生人脸记录
     * @param jsonString:
     * @return: java.lang.Integer
     * @date: 2021/8/26 15:12
     **/
    Integer batchAddStrangerRecord(String jsonString);

    /**
     * @author: Pipi
     * @description: 分页查询陌生人脸记录
     * @param baseQO: 查询条件
     * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.StrangerRecordEntiy>
     * @date: 2021/8/27 11:33
     **/
    PageInfo<StrangerRecordEntiy> pageStrangerRecord(BaseQO<StrangerRecordEntiy> baseQO);
}
