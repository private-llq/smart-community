package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IComplainService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.ComplainEntity;
import com.jsy.community.entity.PropertyComplaintsEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.mapper.ComplainMapper;
import com.jsy.community.mapper.PropertyComplainMapper;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.qo.proprietor.PropertyComplainQO;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 投诉建议
 * @author: Hu
 * @create: 2020-12-23 11:16
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class ComplainServiceImpl extends ServiceImpl<ComplainMapper, ComplainEntity> implements IComplainService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ComplainMapper complainMapper;
    @Autowired
    private PropertyComplainMapper propertyComplainMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @Description: 物业投诉
     * @author: Hu
     * @since: 2021/3/17 14:44
     * @Param:
     * @return:
     */
    @Override
    public void propertyComplain(PropertyComplainQO propertyComplainQO) {
        String str=null;
        PropertyComplaintsEntity entity = new PropertyComplaintsEntity();
        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid", propertyComplainQO.getUid()));
        if (userEntity!=null){
            entity.setName(userEntity.getRealName());
            entity.setMobile(userEntity.getMobile());
        }
        entity.setId(SnowFlake.nextId());
        entity.setContent(propertyComplainQO.getContent());
        entity.setStatus(0);
        entity.setImages(propertyComplainQO.getImages());
        entity.setLocation(propertyComplainQO.getLocation());
        entity.setType(propertyComplainQO.getType());
        entity.setUid(propertyComplainQO.getUid());
        entity.setComplainTime(LocalDateTime.now());
        Object complain_serial_number = redisTemplate.opsForValue().get("complain_serial_number");
        String s = String.valueOf(complain_serial_number);
        if (s.length()<5){
            if (s.length()==1) {
                str="000"+s;
            }else {
                if (s.length()==2){
                    str="00"+s;
                }else{
                    if (s.length()==3){
                        str="0"+s;
                    }else{
                        if (s.length()==4){
                            str=s;
                        }
                    }
                }
            }
        }else {
            str=s;
        }
        int anInt = Integer.parseInt(s);
        ++anInt;
        redisTemplate.opsForValue().set("complain_serial_number",anInt+"");
        entity.setSerialNumber(getSerialNumber()+str);
        System.out.println(getSerialNumber() + str);
        propertyComplainMapper.insert(entity);
    }

    public String getSerialNumber() {
        String str="TS";
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd");
        String s=sdfTime.format(new Date().getTime()).replaceAll("[[\\s-:punct:]]", "");
        return str+=s;
    }

    /**
     * @Description: 用户投诉接口
     * @author: Hu
     * @since: 2021/2/23 17:35
     * @Param:
     * @return:
     */
    @Override
    public void addComplain(ComplainEntity complainEntity) {
        complainMapper.insert(complainEntity);
    }

    /**
     * @Description: 查询用户所有的投诉建议
     * @author: Hu
     * @since: 2020/12/23 11:30
     * @Param:
     * @return:
     */
    @Override
    public List<ComplainEntity> selectUserIdComplain(String userId) {
        return complainMapper.selectList(new QueryWrapper<ComplainEntity>().eq("uid",userId));
    }
}
