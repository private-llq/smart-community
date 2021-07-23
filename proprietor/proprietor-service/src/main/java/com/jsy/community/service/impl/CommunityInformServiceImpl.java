package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICommunityInformService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.consts.ProprietorConsts;
import com.jsy.community.entity.CommunityConfigEntity;
import com.jsy.community.entity.PushInformEntity;
import com.jsy.community.entity.UserInformEntity;
import com.jsy.community.mapper.CommunityConfigMapper;
import com.jsy.community.mapper.CommunityInformMapper;
import com.jsy.community.mapper.UserInformMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.OldPushInformQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.lease.HouseLeaseVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    
    @Resource
    private CommunityConfigMapper communityConfigMapper;

    /**
     * 列表查询社区消息
     * @param qo               查询参数对象
     * @return                 返回查询结果
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public List<PushInformEntity> queryCommunityInform(BaseQO<OldPushInformQO> qo) {
        //1.查出推送号列表基本列表数据
        QueryWrapper<PushInformEntity>  queryWrapper = new QueryWrapper<>();
        OldPushInformQO query = qo.getQuery();
        Page<PushInformEntity> objectPage = new Page<>(qo.getPage(), qo.getSize());
        queryWrapper.select("id,acct_id,create_time,push_title,push_sub_title,publish_time");
        queryWrapper.eq("push_state", 1);
        queryWrapper.eq("acct_id", query.getAcctId());
        queryWrapper.eq("deleted", 0);
        queryWrapper.last("ORDER BY create_time desc");
        //把当前推送账号id 该用户所有未读信息 标记为已读
        setReadPushInform(query.getAcctId(), query.getUid());
        return communityInformMapper.selectPage(objectPage, queryWrapper).getRecords();
    }









    /**
     * 社区主页 当前轮播消息 查询最近的  initialInformCount 条数量
     * @param initialInformCount     初始轮播消息条数
     * @return                       返回消息列表
     */
    @Override
    public List<PushInformEntity> rotationCommunityInform(Integer initialInformCount , Long communityId) {
        CommunityConfigEntity config = communityConfigMapper.selectOne(new QueryWrapper<CommunityConfigEntity>().select("show_sys_msg").eq("community_id", communityId));
        //无小区配置或配置展示系统消息
        if(config == null){
            //没有找到小区配置项 默认展示
            log.error("获取首页轮播消息 - 未查询到小区配置，请检查小区配置。小区ID：" + communityId);
            return communityInformMapper.rotationCommunityInform(initialInformCount, communityId);
        }else if(ProprietorConsts.COMMUNITY_CONFIG_SHOW_SYS_MSG.equals(config.getShowSysMsg())){
            //小区配置了展示系统消息
            return communityInformMapper.rotationCommunityInform(initialInformCount, communityId);
        }
        //小区配置了不展示系统消息
        return communityInformMapper.rotationCommunityInformSelf(initialInformCount, communityId);
    }

    /**
     * 社区推送消息详情查看
     * @param informId      推送消息ID
     * @param userId        用户ID
     * @return              返回这条推送消息的详情
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public PushInformEntity detailsCommunityInform(Long informId , String userId) {
        //根据消息id查出消息
        QueryWrapper<PushInformEntity> wrapper = new QueryWrapper<>();
        wrapper.select("push_title,create_time,browse_count,push_msg,acct_id");
        wrapper.eq("id", informId);
        wrapper.eq("deleted", 0);
        PushInformEntity informEntity = communityInformMapper.selectOne(wrapper);
        if(informEntity == null){
            throw new ProprietorException("没有找到这条消息!");
        }
        //标识用户已读该社区消息
        UserInformEntity userInformEntity = new UserInformEntity();
        userInformEntity.setId(SnowFlake.nextId());
        userInformEntity.setAcctId(informEntity.getAcctId());
        userInformEntity.setInformId(informId);
        userInformEntity.setUid(userId);
        userInformMapper.setInformReadByUser(userInformEntity);
        //该推送消息的浏览量+1
        communityInformMapper.updatePushInformBrowseCount(informEntity.getAcctId(), informId);
        return informEntity;
    }



    /**
     * 用户消息列表 左滑动 删除推送号(屏蔽)
     * @param acctId    推送号ID
     * @param userId    用户id
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void delPushInformAcct(Long acctId, String userId) {
        //删除(加入屏蔽表)推送号之前 先把 该推送号消息内容 标记为已读
        setReadPushInform(acctId, userId);
        communityInformMapper.insertClearRecord(SnowFlake.nextId(), acctId, userId);
    }


    /**
     * 通过用户传上来的 推送账号id 标记用户已读
     * @param acctIds       推送账号id列表
     * @param uid           用户id
     */
    @Override
    public void clearUnreadInform(List<Long> acctIds, String uid) {
        acctIds.forEach( acctId -> {
            //标记 当前 用户 在当前推送号 未读的信息 为已读
            setReadPushInform(acctId, uid);
        });
    }

    @Override
    public List<HouseLeaseVO> leaseLatestInform(Integer informInitializeCount) {
        int limit = informInitializeCount / 2;
        //商铺最新信息
        List<HouseLeaseVO> shopVos = communityInformMapper.selectShopLatest(limit);
        //出租房最新信息
        List<HouseLeaseVO> leaseVos = communityInformMapper.selectLeaseLatest(limit);
        List<HouseLeaseVO> vos = new ArrayList<>();
        vos.addAll(shopVos);
        vos.addAll(leaseVos);
        return vos;
    }


    /**
     * 通过 推送账号id 和 用户id 把用户在该推送账号未读的信息 标记为已读
     * @author YuLF
     * @since  2021/1/9 11:03
     * @Param  acctId   推送账号id
     * @Param  uid      用户id
     */
    private void setReadPushInform(Long acctId, String uid){
        //2.把当前推送号该用户所有未读数据标识为已读 的数据查出
        List<Long> unreadInformIds = communityInformMapper.selectUnreadInformId(acctId, uid);
        //3.未读消息ID添加至 t_user_inform 标识 消息已读
        if( unreadInformIds != null && !unreadInformIds.isEmpty() ){
            communityInformMapper.insertBatchReadInform(unreadInformIds, acctId, uid);
        }
    }


}
