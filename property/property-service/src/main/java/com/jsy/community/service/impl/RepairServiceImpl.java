package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IRepairService;
import com.jsy.community.entity.RepairEntity;
import com.jsy.community.mapper.RepairMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 房屋报修 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2020-12-08
 */
@Service
public class RepairServiceImpl extends ServiceImpl<RepairMapper, RepairEntity> implements IRepairService {

}
