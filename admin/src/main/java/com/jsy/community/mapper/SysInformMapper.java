package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.PushInformEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.PushInformQO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 系统消息Mapper
 * @author YuLF
 * @since 2020-12-21 11:29
 **/
@Mapper
public interface SysInformMapper extends BaseMapper<PushInformEntity> {

    List<PushInformEntity> query(BaseQO<PushInformQO> baseQO);
}
