package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarService;
import com.jsy.community.api.IProprietorService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.mapper.ProprietorMapper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 业主 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2020-11-25
 */
@Service
public class ProprietorServiceImpl extends ServiceImpl<ProprietorMapper, UserEntity> implements IProprietorService {

    @Autowired
    private ProprietorMapper proprietorMapper;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICarService iCarService;

    @Transactional
    @Override
    public void del(Long id) {
        //删除业主车辆信息
        Map<String, Object> map = new HashMap<>(1);
        map.put("uid", id);
        iCarService.deleteProprietorCar(map);
        //删除业主关联的家庭成员

        //删除业主关联的房屋


        //删除业主信息
        proprietorMapper.deleteById(id);


    }
}
