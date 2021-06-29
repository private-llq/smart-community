package com.jsy.community.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IAdminCommunityInformService;
import com.jsy.community.api.IAdminUserService;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.PushInformEntity;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.AdminCommunityInformMapper;
import com.jsy.community.mapper.CommunityMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.PushInformQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.es.ElasticsearchImportProvider;
import com.jsy.community.utils.es.Operation;
import com.jsy.community.utils.es.RecordFlag;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;


/**
 * <p>
 *  社区消息服务实现类
 * </p>
 *
 * @author YuLF
 * @since 2020-11-16
 */
@DubboService(version = Const.version, group = Const.group_property)
public class AdminCommunityInformServiceImpl extends ServiceImpl<AdminCommunityInformMapper, PushInformEntity> implements IAdminCommunityInformService {

    @Resource
    private AdminCommunityInformMapper communityInformMapper;

    @Resource
    private CommunityMapper communityMapper;

    @Autowired
    private IAdminUserService adminUserService;

    /**
     * 添加社区推送消息
     * @param qo    接收消息参数的对象
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean addPushInform(PushInformQO qo){
        PushInformEntity entity = PushInformEntity.getInstance();
        // 获取社区信息
        CommunityEntity communityEntity = communityMapper.selectById(qo.getAcctId());
        if (communityEntity == null) {
            throw new JSYException("未获取到推送账户信息");
        }
        qo.setAcctName(communityEntity.getName());
        qo.setAcctAvatar(communityEntity.getIconUrl());
        BeanUtils.copyProperties(qo, entity);
        entity.setId(SnowFlake.nextId());
        //当某个推送号有新消息发布时：用户之前已经删除的 推送号 又会被拉取出来 同时通知有未读消息
        //清除推送消息屏蔽表
        communityInformMapper.clearPushDel(qo.getAcctId());
        // 当为新发布
        if (qo.getPushState() == 1) {
            // 新发布,添加发布人信息
            entity.setPublishBy(qo.getCreateBy());
            entity.setPublishTime(LocalDateTime.now());
        }
        if (qo.getTopState() == 1) {
            // 置顶状态,将其他推送取消置顶
            communityInformMapper.unpinned(qo.getCreateBy());
        }
        //返回值为冗余
        boolean b = communityInformMapper.insert(entity) > BusinessConst.ZERO;
        //0表示推送目标为所有社区
        if(b && qo.getPushTarget().equals(BusinessConst.ZERO)){
            ElasticsearchImportProvider.elasticOperationSingle(entity.getId(), RecordFlag.INFORM, Operation.INSERT, qo.getPushTitle(), qo.getAcctAvatar());
        }
        return b;
    }

    /**
     *@Author: Pipi
     *@Description: 更新信息
     *@Param: qo:
     *@Return: java.lang.Boolean
     *@Date: 2021/4/20 16:38
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePushInform(PushInformQO qo) {
        // 查询原始数据
        PushInformEntity pushInformEntity = baseMapper.selectById(qo.getId());
        PushInformEntity entity = PushInformEntity.getInstance();
        // 获取社区信息
        CommunityEntity communityEntity = communityMapper.selectById(qo.getAcctId());
        if (communityEntity == null) {
            throw new JSYException("未获取到推送账户信息");
        }
        qo.setAcctName(communityEntity.getName());
        qo.setAcctAvatar(communityEntity.getIconUrl());
        BeanUtils.copyProperties(qo, entity);
        entity.setUpdateBy(qo.getUpdateBy());
        entity.setCreateBy(pushInformEntity.getCreateBy());
        entity.setCreateTime(pushInformEntity.getCreateTime());
        entity.setBrowseCount(pushInformEntity.getBrowseCount());
        entity.setUpdateTime(LocalDateTime.now());
        if (qo.getPushState() == 1) {
            // 如果新数据的发布状态是发布,添加发布人信息和发布时间
            // 编辑只能是草稿状态或者撤销状态发布
            // 所以状态只能发生情况为:草稿->发布、草稿->草稿、撤销->发布这三种状态
            entity.setPublishBy(qo.getUpdateBy());
            entity.setPublishTime(LocalDateTime.now());
        }
        if (qo.getTopState() == 1) {
            communityInformMapper.unpinned(qo.getUpdateBy());
        }
        return baseMapper.updateById(entity) > 0;
    }

    /**
     *
     * 逻辑删除 推送消息
     * @param id    推送消息id
     * @return      返回删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deletePushInform(Long id, String updateAdminId) {
        //物理删除：删除该条社区消息之前 先删除 所有用户已读消息记录
        communityInformMapper.delUserReadInform(id);
        ElasticsearchImportProvider.elasticOperationSingle(id, RecordFlag.INFORM, Operation.DELETE, null, null);
        // 逻辑删除消息信息,更新操作员
        var integer = communityInformMapper.updateDeleted(id, updateAdminId);
        return integer > 0;
    }

    /**
     *@Author: Pipi
     *@Description: (物业端)更新置顶状态
     *@Param: qo:
     *@Return: java.lang.Boolean
     *@Date: 2021/4/20 15:15
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateTopState(PushInformQO qo) {
        if (qo.getTopState() == 1) {
            // 如果是置顶,将其他置顶取消
            communityInformMapper.unpinned(qo.getUpdateBy());
        }
        communityInformMapper.setTopState(qo.getTopState(), qo.getId(), qo.getUpdateBy());
        return true;
    }

    /**
     *@Author: Pipi
     *@Description: 更新发布状态
     *@Param: qo:
     *@Return: java.lang.Boolean
     *@Date: 2021/4/20 15:57
     **/
    @Override
    public Boolean updatePushState(PushInformQO qo) {
        // 如果状态为发布状态,表示由草稿状态变跟为发布状态,还需要更新发布人和发布时间
        Integer result = communityInformMapper.updatePushState(qo.getPushState(), qo.getId(), qo.getUpdateBy());
        return result == 1;
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

    /**
     *@Author: Pipi
     *@Description: (物业端)查询公告列表
     *@Param: qo:
     *@Return: java.util.List<com.jsy.community.entity.PushInformEntity>
     *@Date: 2021/4/20 13:53
     **/
    @Override
    public Page<PushInformEntity> queryInformList(BaseQO<PushInformQO> qo) {
        QueryWrapper<PushInformEntity>  queryWrapper = new QueryWrapper<>();
        PushInformQO query = qo.getQuery();
        Page<PushInformEntity> objectPage = new Page<>(qo.getPage(), qo.getSize());
        queryWrapper.select("id,acct_id,push_title,inform_type,push_msg,browse_count,top_state,push_state,push_sub_title,create_by,create_time,update_by,update_time,publish_by,publish_time");
        queryWrapper.eq("acct_id", query.getAcctId());
        if (!StringUtils.isEmpty(query.getPushTitle())) {
            queryWrapper.like("push_title", query.getPushTitle());
        }
        if (!StringUtils.isEmpty(query.getPushMsg())) {
            queryWrapper.like("push_msg", query.getPushMsg());
        }
        if (query.getStartCreateTime() != null) {
            queryWrapper.ge("DATE(create_time)", query.getStartCreateTime());
        }
        if (query.getEndCreateTime() != null) {
            queryWrapper.le("DATE(create_time)", query.getEndCreateTime());
        }
        if (query.getStartUpdateTime() != null) {
            queryWrapper.ge("DATE(update_time)", query.getStartUpdateTime());
        }
        if (query.getEndUpdateTime() != null) {
            queryWrapper.le("DATE(update_time)", query.getEndUpdateTime());
        }
        if (query.getPageState() == 0) {
            // 草稿页面
            queryWrapper.eq("push_state", query.getPageState());
            queryWrapper.last("ORDER BY create_time desc");
        } else {
            // 保证发布页面不会查出草稿
            queryWrapper.ge("push_state", query.getPageState());
            if (query.getPushState() != null) {
                // 发布页面
                queryWrapper.eq("push_state", query.getPushState());
            }
            // 排序:发布的在最前,然后依次条件是发布时间,更新时间
            queryWrapper.last("ORDER BY push_state asc,publish_time desc,update_time desc");
        }
        Page<PushInformEntity> pushInformEntityPage = communityInformMapper.selectPage(objectPage, queryWrapper);
        if (CollectionUtil.isNotEmpty(pushInformEntityPage.getRecords())) {
            //补创建人和更新人和发布人姓名
            Set<String> uidSet = new HashSet<>();
            for (PushInformEntity record : pushInformEntityPage.getRecords()) {
                uidSet.add(record.getCreateBy());
                uidSet.add(record.getUpdateBy());
                uidSet.add(record.getPublishBy());
            }
            Map<String, Map<String, String>> userInfoMap = adminUserService.queryNameByUidBatch(uidSet);
            for (PushInformEntity record : pushInformEntityPage.getRecords()) {
                record.setCreateBy(userInfoMap.get(record.getCreateBy()) == null ? null : userInfoMap.get(record.getCreateBy()).get("name"));
                record.setUpdateBy(userInfoMap.get(record.getUpdateBy()) == null ? null : userInfoMap.get(record.getUpdateBy()).get("name"));
                record.setPublishBy(userInfoMap.get(record.getPublishBy()) == null ? null : userInfoMap.get(record.getPublishBy()).get("name"));
            }
        }
        return pushInformEntityPage;
    }

    /**
     *@Author: Pipi
     *@Description: (物业端)获取单条消息详情
     *@Param: id: 消息ID
     *@Return: com.jsy.community.entity.PushInformEntity
     *@Date: 2021/4/20 16:23
     **/
    @Override
    public PushInformEntity getDetail(Long id) {
        return baseMapper.selectById(id);
    }


}
