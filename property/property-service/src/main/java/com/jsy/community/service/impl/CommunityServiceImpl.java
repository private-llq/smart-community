package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICommunityService;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.mapper.CommunityMapper;
import org.springframework.stereotype.Service;

/**
 * 社区 服务实现类
 * @author YuLF
 * @since 2020-11-25
 */
@Service
public class CommunityServiceImpl extends ServiceImpl<CommunityMapper, CommunityEntity> implements ICommunityService {

}
