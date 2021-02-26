package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPayGroupService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PayFamilyEntity;
import com.jsy.community.entity.PayGroupEntity;
import com.jsy.community.mapper.PayFamilyMapper;
import com.jsy.community.mapper.PayGroupMapper;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 户号组 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2020-12-10
 */
@DubboService(version = Const.version, group = Const.group_proprietor)
public class PayGroupServiceImpl extends ServiceImpl<PayGroupMapper, PayGroupEntity> implements IPayGroupService {

    @Autowired
    private PayGroupMapper payGroupMapper;
    @Autowired
    private PayFamilyMapper payFamilyMapper;
    @Override
    @Transactional
    public void delete(String name,String userId) {
        PayGroupEntity payGroupEntity = payGroupMapper.selectOne(new QueryWrapper<PayGroupEntity>()
                .eq("uid", userId)
                .eq("name", name)
        );

        if (payGroupEntity!=null){
            payFamilyMapper.delete(new QueryWrapper<PayFamilyEntity>()
                    .eq("uid",userId)
                    .eq("group_id",payGroupEntity.getId())
            );
            payGroupMapper.deleteById(payGroupEntity.getId());
        }
    }

    @Override
    public void updateGroup(Long id, String name, String userId) {
        PayGroupEntity entity = payGroupMapper.selectById(id);
        entity.setName(name);
        payGroupMapper.updateById(entity);
    }

    @Override
    public void insertGroup(String name, String userId) {
        PayGroupEntity payGroupEntity = new PayGroupEntity();
        payGroupEntity.setUid(userId);
        payGroupEntity.setName(name);
        payGroupEntity.setType(5);
        payGroupEntity.setId(SnowFlake.nextId());
        payGroupMapper.insert(payGroupEntity);
    }
}
