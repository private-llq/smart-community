package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.annotation.EsImport;
import com.jsy.community.api.IAdminCommunityInformService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PushInformEntity;
import com.jsy.community.mapper.AdminCommunityInformMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.PushInformQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.es.ElasticSearchImportProvider;
import com.jsy.community.utils.es.Operation;
import com.jsy.community.utils.es.RecordFlag;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
@DubboService(version = Const.version, group = Const.group_property)
public class AdminCommunityInformServiceImpl extends ServiceImpl<AdminCommunityInformMapper, PushInformEntity> implements IAdminCommunityInformService {

    @Resource
    private AdminCommunityInformMapper communityInformMapper;


    /**
     * 添加社区推送消息
     * @param qo    接收消息参数的对象
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean addPushInform(PushInformQO qo){
        PushInformEntity entity = PushInformEntity.getInstance();
        BeanUtils.copyProperties(qo, entity);
        entity.setId(SnowFlake.nextId());
        //当某个推送号有新消息发布时：用户之前已经删除的 推送号 又会被拉取出来 同时通知有未读消息
        //清除推送消息屏蔽表
        communityInformMapper.clearPushDel(qo.getAcctId());
        //返回值为冗余
        boolean b = communityInformMapper.insert(entity) > 0;
        //0表示推送目标为所有社区
        if(b && qo.getPushTarget() ==  0){
            ElasticSearchImportProvider.elasticOperation(entity.getId(), RecordFlag.INFORM, Operation.INSERT, qo.getPushTitle(), qo.getAcctAvatar());
        }
        return b;
    }

    /**
     *
     * 逻辑删除 推送消息
     * @param id    推送消息id
     * @return      返回删除成功
     */
    @EsImport( operation = Operation.DELETE, recordFlag = RecordFlag.INFORM)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deletePushInform(Long id) {
        //物理删除：删除该条社区消息之前 先删除 所有用户已读消息记录
        communityInformMapper.delUserReadInform(id);
        return communityInformMapper.deleteById(id) > 0;
    }

    /**
     * 列表查询社区消息
     * @param qo               查询参数对象
     * @return                 返回查询结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<PushInformEntity> queryCommunityInform(BaseQO<PushInformQO> qo) {
        //1.查出推送号列表基本列表数据
        QueryWrapper<PushInformEntity>  queryWrapper = new QueryWrapper<>();
        PushInformQO query = qo.getQuery();
        Page<PushInformEntity> objectPage = new Page<>(qo.getPage(), qo.getSize());
        queryWrapper.select("id,acct_id,create_time,push_title,push_sub_title");
        queryWrapper.eq("acct_id", query.getAcctId());
        queryWrapper.eq("deleted", 0);
        queryWrapper.last("ORDER BY create_time desc");
        //2.把当前推送号该用户所有未读数据标识为已读 的数据查出
        List<Long> unreadInformIds = communityInformMapper.selectUnreadInformId(query.getAcctId(), query.getUid());
        //3.未读消息ID添加至 t_user_inform 标识 消息已读
        if( unreadInformIds != null && !unreadInformIds.isEmpty() ){
            communityInformMapper.insertBatchReadInform(unreadInformIds, query.getAcctId(), query.getUid());
        }
        return communityInformMapper.selectPage(objectPage, queryWrapper).getRecords();
    }



}
