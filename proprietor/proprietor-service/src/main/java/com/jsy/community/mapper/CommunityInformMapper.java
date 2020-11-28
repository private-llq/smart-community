package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.CommunityInformEntity;
import com.jsy.community.qo.proprietor.CommunityInformQO;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author YuLF
 * @since 2020-11-16
 */
public interface CommunityInformMapper extends BaseMapper<CommunityInformEntity> {

    Integer updateCommunityInform(CommunityInformQO communityInformQO);
}
