package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.PushInformEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.OldPushInformQO;

import java.util.List;

/**
 * @author YuLF
 * @since 2020-12-21 11:39
 */
public interface ISysInformService extends IService<PushInformEntity> {

    /**
     * 添加系统推送消息
     * @author YuLF
     * @param sysInformQo 系统请求参数
     * @since  2021/1/13 17:37
     * @return  返回是否添加成功
     */
    boolean add(OldPushInformQO sysInformQo);

    /**
     * 根据消息id删除
     * @param informId   消息id
     * @return           返回删除成功
     */
    boolean delete(Long informId);

    List<PushInformEntity> query(BaseQO<OldPushInformQO> baseQo);

    boolean deleteBatchByIds(List<Long> informIds);
}
