package com.jsy.community.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IHouseInfoService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseInfoEntity;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.mapper.HouseInfoMapper;
import com.jsy.community.mapper.PropertyRelationMapper;
import com.jsy.community.qo.MembersQO;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PropertyRelationMapper relationMapper;



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

    /**
     * @Description: 查询房屋推送消息详情
     * @author: Hu
     * @since: 2021/10/14 14:02
     * @Param:
     * @return:
     */
    @Override
    public HouseInfoEntity getByPushInfo(Long id) {
        return houseInfoMapper.selectById(id);
    }

    /**
     * @Description: 用户确定关系后添加到成员表
     * @author: Hu
     * @since: 2021/10/14 14:05
     * @Param:
     * @return:
     */
    @Override
    @Transactional
    public void relationSave(HouseInfoEntity houseInfoEntity) {
        HouseInfoEntity infoEntity = houseInfoMapper.selectById(houseInfoEntity.getId());
        if (infoEntity != null) {
            Object object = redisTemplate.opsForValue().get("pushInFormMember:" + infoEntity.getMobile()+infoEntity.getHouseId());
            if (object!=null){
                MembersQO membersQO = JSONUtil.toBean(JSONUtil.parseObj(object), MembersQO.class);

                //添加成员表数据
                HouseMemberEntity entity = new HouseMemberEntity();
                BeanUtils.copyProperties(membersQO,entity);
                entity.setId(SnowFlake.nextId());
                entity.setHouseholderId(infoEntity.getYzUid());
                entity.setUid(houseInfoEntity.getYhUid());
                relationMapper.insert(entity);

                infoEntity.setStatus(1);
                houseInfoMapper.updateById(infoEntity);
            } else {
                throw new PropertyException("该消息已过期，请联系业主重新添加！");
            }
        }
    }
}
