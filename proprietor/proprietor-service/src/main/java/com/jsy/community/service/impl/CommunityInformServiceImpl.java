package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICommunityInformService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PushInformEntity;
import com.jsy.community.entity.UserInformEntity;
import com.jsy.community.mapper.CommunityInformMapper;
import com.jsy.community.mapper.UserInformMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.PushInformQO;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 * <p>
 *  社区消息服务实现类
 * </p>
 *
 * @author YuLF
 * @since 2020-11-16
 */
@Service
@DubboService(version = Const.version, group = Const.group)
public class CommunityInformServiceImpl extends ServiceImpl<CommunityInformMapper, PushInformEntity> implements ICommunityInformService {

    @Resource
    private CommunityInformMapper communityInformMapper;

    @Resource
    private UserInformMapper userInformMapper;

    /**
     * 列表查询社区消息
     * @param qo               查询参数对象
     * @return                 返回查询结果
     */
    @Transactional
    @Override
    public List<PushInformEntity> queryCommunityInform(BaseQO<PushInformQO> qo) {
        //1.查出推送号列表基本列表数据
        QueryWrapper<PushInformEntity>  queryWrapper = new QueryWrapper<>();
        PushInformQO query = qo.getQuery();
        Page<PushInformEntity> objectPage = new Page<>(qo.getPage(), qo.getSize());
        queryWrapper.select("id,acct_id,create_time,push_title,push_sub_title");
        queryWrapper.eq("acct_id", query.getAcctId());
        queryWrapper.last("ORDER BY create_time desc");
        //2.把当前推送号该用户所有未读数据标识为已读 的数据查出
        List<Long> unreadInformIds = communityInformMapper.selectUnreadInformId(query.getAcctId(), query.getUid());
        //3.未读消息ID添加至 t_user_inform 标识 消息已读
        if( unreadInformIds != null && !unreadInformIds.isEmpty() ){
            communityInformMapper.insertBatchReadInform(unreadInformIds, query.getAcctId(), query.getUid());
        }
        return communityInformMapper.selectPage(objectPage, queryWrapper).getRecords();
    }



    private String getDateTimeAsString(LocalDateTime localDateTime) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      return localDateTime.format(formatter);
    }






    /**
     * 社区主页 当前轮播消息 查询最近的  initialInformCount 条数量
     * @param initialInformCount     初始轮播消息条数
     * @return                       返回消息列表
     */
    @Override
    public List<PushInformEntity> rotationCommunityInform(Integer initialInformCount , Long communityId) {
        return communityInformMapper.rotationCommunityInform(initialInformCount, communityId);
    }

    /**
     * 社区推送消息详情查看
     * @param acctId        推送账号id、可能是社区ID 可能是系统消息ID 可能是其他第三方推送号ID
     * @param informId      推送消息ID
     * @param userId        用户ID
     * @return              返回这条推送消息的详情
     */
    @Transactional
    @Override
    public PushInformEntity detailsCommunityInform(Long acctId, Long informId , String userId) {
        //根据社区id和消息id查出消息
        QueryWrapper<PushInformEntity> wrapper = new QueryWrapper<>();
        wrapper.select("push_title,create_time,browse_count,push_msg");
        wrapper.eq("id", informId);
        wrapper.eq("acct_id",acctId);
        PushInformEntity informEntity = communityInformMapper.selectOne(wrapper);
        //标识用户已读该社区消息
        UserInformEntity userInformEntity = new UserInformEntity();
        userInformEntity.setId(SnowFlake.nextId());
        userInformEntity.setAcctId(acctId);
        userInformEntity.setInformId(informId);
        userInformEntity.setUid(userId);
        userInformMapper.setInformReadByUser(userInformEntity);
        //该推送消息的浏览量+1
        communityInformMapper.updatePushInformBrowseCount(acctId, informId);
        return informEntity;
    }


}
