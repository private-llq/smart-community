package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.StrangerRecordService;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PeopleHistoryEntity;
import com.jsy.community.entity.property.StrangerRecordEntiy;
import com.jsy.community.mapper.StrangerRecordMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.Base64Util;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.http.entity.ContentType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 陌生人脸记录服务实现
 * @Date: 2021/8/26 15:10
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class StrangerRecordServiceImpl extends ServiceImpl<StrangerRecordMapper, StrangerRecordEntiy> implements StrangerRecordService {
    @Autowired
    private StrangerRecordMapper strangerRecordMapper;

    /**
     * @param jsonString :
     * @author: Pipi
     * @description: 批量新增陌生人脸记录
     * @return: java.lang.Integer
     * @date: 2021/8/26 15:12
     **/
    @Override
    public Integer batchAddStrangerRecord(String jsonString) {
        log.info("同步陌生人脸记录");
        List<StrangerRecordEntiy> strangerRecordEntiys = JSON.parseArray(jsonString, StrangerRecordEntiy.class);
        for (StrangerRecordEntiy strangerRecordEntiy : strangerRecordEntiys) {
            MultipartFile multipartFile = Base64Util.base64StrToMultipartFile(strangerRecordEntiy.getPic().substring(strangerRecordEntiy.getPic().indexOf(",") + 1));
            String picUrl = MinioUtils.uploadByFaceMachine(multipartFile, BusinessConst.STRANGER_FACE_BUCKET_NAME);
            strangerRecordEntiy.setPic(picUrl);
        }
        return strangerRecordMapper.batchInsertStrangerRecord(strangerRecordEntiys);
    }

    /**
     * @param baseQO : 查询条件
     * @author: Pipi
     * @description: 分页查询陌生人脸记录
     * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.StrangerRecordEntiy>
     * @date: 2021/8/27 11:33
     **/
    @Override
    public PageInfo<StrangerRecordEntiy> pageStrangerRecord(BaseQO<StrangerRecordEntiy> baseQO, Long communityId) {
        Page<StrangerRecordEntiy> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        QueryWrapper<StrangerRecordEntiy> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("community_id", communityId);
        if (baseQO.getQuery() == null) {
            baseQO.setQuery(new StrangerRecordEntiy());
        }
        if (StringUtils.isNotBlank(baseQO.getQuery().getFacesluiceName())) {
            queryWrapper.like("facesluice_name", baseQO.getQuery().getFacesluiceName());
        }
        page = strangerRecordMapper.selectPage(page, queryWrapper);
        PageInfo<StrangerRecordEntiy> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(page, pageInfo);
        return pageInfo;
    }
}
