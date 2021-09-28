package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IComplainService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.ComplainEntity;
import com.jsy.community.mapper.CommunityMapper;
import com.jsy.community.mapper.ComplainMapper;
import com.jsy.community.qo.proprietor.ComplainQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.proprietor.ComplainVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @program: com.jsy.community
 * @description: 投诉建议
 * @author: Hu
 * @create: 2020-12-23 11:16
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class ComplainServiceImpl extends ServiceImpl<ComplainMapper, ComplainEntity> implements IComplainService {
    @Autowired
    private ComplainMapper complainMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CommunityMapper communityMapper;


    private String serialNumber="complain_number:";

    /**
     * @Description: 用户投诉接口
     * @author: Hu
     * @since: 2021/5/21 13:53
     * @Param: [complainEntity]
     * @return: void
     */
    @Override
    public void addComplain(ComplainEntity complainEntity) {
        String str=null;
        String s = null;
        Object number = redisTemplate.opsForValue().get(serialNumber+complainEntity.getCommunityId());
        if (number!=null){
            s = String.valueOf(number);
        }else {
            s=String.valueOf(1);
        }
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
        redisTemplate.opsForValue().set(serialNumber+complainEntity.getCommunityId(),String.valueOf(anInt),getMinute(),TimeUnit.MINUTES);
        //key:投诉编号+社区id  value:时间
        complainEntity.setSerialNumber(getSerialNumber()+str);
        complainMapper.insert(complainEntity);
    }

    @Override
    public boolean appendComplain(ComplainQO complainQO) {
        ComplainEntity complainEntity = new ComplainEntity();
        complainEntity.setCommunityId(complainEntity.getCommunityId());
        complainEntity.setStatus(0);
        complainEntity.setId(SnowFlake.nextId());
        complainEntity.setComplainTime(LocalDateTime.now());
        BeanUtils.copyProperties(complainQO,complainEntity);

        String str=null;
        String s = null;
        Object number = redisTemplate.opsForValue().get(serialNumber+complainEntity.getCommunityId());
        if (number!=null){
            s = String.valueOf(number);
        }else {
            s=String.valueOf(1);
        }
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
        redisTemplate.opsForValue().set(serialNumber+complainEntity.getCommunityId(),String.valueOf(anInt),getMinute(),TimeUnit.MINUTES);
        //key:投诉编号+社区id  value:时间
        complainEntity.setSerialNumber(getSerialNumber()+str);
        return complainMapper.insert(complainEntity)==1;
    }

    @Override
    public List<ComplainVO> selectComplain(String userId) {
        List<ComplainVO> list = new ArrayList<>();
        ComplainVO complainVO = null;
        List<ComplainEntity> selectList = complainMapper.selectList(new QueryWrapper<ComplainEntity>().eq("uid", userId));
        for(ComplainEntity entity :selectList){
            complainVO = new ComplainVO();
            BeanUtils.copyProperties(entity,complainVO);
            CommunityEntity communityEntity = communityMapper.selectById(entity.getCommunityId());
            if (communityEntity==null){
                throw new ProprietorException("你还没有反馈信息");
            }
            complainVO.setCommunityName(communityEntity.getName());
            list.add(complainVO);
        }
        return list;
    }

    /**
     * @Description: 获取当前时间到0点钟的分钟数
     * @author: Hu
     * @since: 2021/5/19 9:48
     * @Param:
     * @return:
     */
    public int getMinute() {
        int hour = LocalDateTime.now().getHour();
        int minute = LocalDateTime.now().getMinute();
        int remainHour=24-hour-1;
        int remainMinute=60-minute;
        return remainHour*60+remainMinute;
    }
    /**
     * @Description: 投诉编号生成类
     * @author: Hu
     * @since: 2021/5/19 9:50
     * @Param:
     * @return:
     */
    public String getSerialNumber() {
        String str="TS";
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd");
        String s=sdfTime.format(System.currentTimeMillis()).replaceAll("[[\\s-:punct:]]", "");
        return str+=s;
    }


    /**
     * @Description: 查询用户所有的投诉建议
     * @author: Hu
     * @since: 2021/5/21 13:52
     * @Param: [userId]
     * @return: java.util.List<com.jsy.community.entity.ComplainEntity>
     */
    @Override
    public List<ComplainEntity> selectUserIdComplain(String userId) {
        return complainMapper.selectList(new QueryWrapper<ComplainEntity>().eq("uid",userId));
    }
}
