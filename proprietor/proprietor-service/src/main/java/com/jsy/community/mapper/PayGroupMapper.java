package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.PayGroupEntity;
import com.jsy.community.vo.UserGroupVO;

import java.util.List;

/**
 * <p>
 * 户号组 Mapper 接口
 * </p>
 *
 * @author jsy
 * @since 2020-12-10
 */
public interface PayGroupMapper extends BaseMapper<PayGroupEntity> {

    List<UserGroupVO> selectUserGroup(String userId);

}
