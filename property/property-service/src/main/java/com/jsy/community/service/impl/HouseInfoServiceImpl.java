package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IHouseInfoService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseInfoEntity;
import com.jsy.community.mapper.HouseInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 房间推送消息
 * @author: Hu
 * @create: 2021-10-13 16:31
 **/
@DubboService(version = Const.version, group = Const.group_property)
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class HouseInfoServiceImpl extends ServiceImpl<HouseInfoMapper, HouseInfoEntity> implements IHouseInfoService {
    @Autowired
    private HouseInfoMapper houseInfoMapper;



    /**
     * @Description: 查询当前用户所有需要推送的消息
     * @author: Hu
     * @since: 2021/10/13 17:35
     * @Param: [mobile]
     * @return: java.util.List<com.jsy.community.entity.HouseInfoEntity>
     */
    @Override
    public List<HouseInfoEntity> selectList(String mobile) {
        List<HouseInfoEntity> houseInfoEntities = houseInfoMapper.selectList(new QueryWrapper<HouseInfoEntity>().eq("mobile", mobile).lt("overdue_time", LocalDateTime.now()));
        return houseInfoEntities;
    }

    /**
     * @Description: 新增
     * @author: Hu
     * @since: 2021/10/13 16:39
     * @Param: [entity]
     * @return: void
     */
    @Override
    public void saveOne(HouseInfoEntity entity) {
         houseInfoMapper.insert(entity);
    }
}
