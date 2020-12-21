package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.sys.SysInformEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.sys.SysInformQO;

import java.util.List;

/**
 * @author YuLF
 * @since 2020-12-21 11:39
 */
public interface ISysInformService extends IService<SysInformEntity> {

    boolean add(SysInformQO sysInformQO);

    boolean update(SysInformQO sysInformQO, Long informId);

    boolean delete(Long informId);

    List<SysInformEntity> query(BaseQO<SysInformQO> baseQO);

    boolean deleteBatchByIds(List<Long> informIds);
}
