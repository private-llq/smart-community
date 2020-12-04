package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserInformEntity;
import com.jsy.community.vo.UserInformVO;

import java.util.List;

public interface UserInformMapper extends BaseMapper<UserInformEntity> {
    List<UserInformVO> findList(Long informId);

    Long findCount(Long informId);
}
