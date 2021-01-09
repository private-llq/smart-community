package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.PushInformEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.PushInformQO;

import java.util.List;

/**
 * @author YuLF
 * @since 2020-12-21 11:39
 */
public interface ISysInformService extends IService<PushInformEntity> {

    boolean add(PushInformQO sysInformQO);

    boolean delete(Long informId);

    List<PushInformEntity> query(BaseQO<PushInformQO> baseQO);

    boolean deleteBatchByIds(List<Long> informIds);
}
