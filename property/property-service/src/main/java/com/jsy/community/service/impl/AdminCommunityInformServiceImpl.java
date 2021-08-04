package com.jsy.community.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IAdminCommunityInformService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.PushInformEntity;
import com.jsy.community.entity.InformAcctEntity;
import com.jsy.community.mapper.AdminCommunityInformMapper;
import com.jsy.community.mapper.CommunityMapper;
import com.jsy.community.mapper.InformAcctMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.OldPushInformQO;
import com.jsy.community.qo.proprietor.PushInformQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.es.ElasticsearchImportProvider;
import com.jsy.community.utils.es.Operation;
import com.jsy.community.utils.es.RecordFlag;
import com.jsy.community.vo.property.PushInfromVO;
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
    private InformAcctMapper informAcctMapper;

    /**
     * 添加社区推送消息
     * @param qo    接收消息参数的对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addPushInform(PushInformQO qo){
        // 组装消息数据
        PushInformEntity entity = PushInformEntity.getInstance();
        entity.setPushTitle(qo.getPushTitle());
        entity.setPushMsg(qo.getPushMsg());
        entity.setPushTarget(qo.getPushTarget());
        entity.setPushState(1);
        entity.setPushTag(qo.getPushTag());
        entity.setInformType("站内");
        entity.setCreateBy(qo.getUid());
        entity.setBrowseCount(0L);
        entity.setPublishBy(qo.getUid());
        entity.setPublishTime(LocalDateTime.now());
        entity.setCreateTime(LocalDateTime.now());
        entity.setId(SnowFlake.nextId());
        int insert = communityInformMapper.insert(entity);
        if (insert > 0) {
            // 消息新增成功时
            insetInformAcct(qo, entity.getId());
            return true;
        } else {
            return false;
        }
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
        entity.setId(qo.getId());
        entity.setPushTitle(qo.getPushTitle());
        entity.setPushMsg(qo.getPushMsg());
        entity.setPushTarget(qo.getPushTarget());
        entity.setPushState(pushInformEntity.getPushState());
        entity.setPushTag(qo.getPushTag());
        entity.setInformType(pushInformEntity.getInformType());
        entity.setCreateBy(qo.getUid());
        entity.setBrowseCount(pushInformEntity.getBrowseCount());
        entity.setPublishBy(qo.getUid());
        entity.setPublishTime(LocalDateTime.now());
        entity.setCreateTime(pushInformEntity.getCreateTime());
        int updateResult = communityInformMapper.updateById(entity);
        if (updateResult == 1) {
            // 消息更新成功时
            // 先清空原有的关系数据
            QueryWrapper<InformAcctEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("inform_id", qo.getId());
            informAcctMapper.delete(wrapper);
            // 再从新新增关系数据
            insetInformAcct(qo, entity.getId());
            return true;
        } else {
            return false;
        }
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
    public Boolean updateTopState(OldPushInformQO qo) {
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
    public Boolean updatePushState(OldPushInformQO qo) {
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
    public List<PushInformEntity> queryCommunityInform(BaseQO<OldPushInformQO> qo) {
        //1.查出推送号列表基本列表数据
        QueryWrapper<PushInformEntity>  queryWrapper = new QueryWrapper<>();
        OldPushInformQO query = qo.getQuery();
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
    public PageInfo<PushInfromVO> queryInformList(BaseQO<PushInformQO> qo) {
        PushInformQO query = qo.getQuery();
        PageInfo<PushInfromVO> pageInfo = new PageInfo<>();
        if (CollectionUtil.isEmpty(query.getCommunityIds())) {
            return pageInfo;
        }
        Page<PushInformEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, qo);
        // 先查社区下的消息ID列表
        QueryWrapper<InformAcctEntity> informAcctQueryWrapper = new QueryWrapper<>();
        informAcctQueryWrapper.in("acct_id", query.getCommunityIds());
        List<InformAcctEntity> informAcctEntities = informAcctMapper.selectList(informAcctQueryWrapper);
        if (CollectionUtil.isEmpty(informAcctEntities)) {
            // 如果结果为空,表示没有推送消息
            return pageInfo;
        }
        List<String> infromIds = new ArrayList<>();
        Map<String, List<String>> infromIdsAcctMap = new HashMap<>();
        for (InformAcctEntity informAcctEntity : informAcctEntities) {
            infromIds.add(informAcctEntity.getInformId());
            if (infromIdsAcctMap.containsKey(informAcctEntity.getInformId())) {
                infromIdsAcctMap.get(informAcctEntity.getInformId()).add(informAcctEntity.getAcctName());
            } else {
                List<String> acctNames = new ArrayList<>();
                acctNames.add(informAcctEntity.getAcctName());
                infromIdsAcctMap.put(informAcctEntity.getInformId(), acctNames);
            }
        }
        QueryWrapper<PushInformEntity>  queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id, push_title, push_msg, create_time");
        queryWrapper.in("id", infromIds);
        if (!StringUtils.isEmpty(query.getPushTitle())) {
            queryWrapper.like("push_title", query.getPushTitle());
        }
        queryWrapper.orderByDesc("create_time");
        Page<PushInformEntity> informEntityPage = communityInformMapper.selectPage(page, queryWrapper);
        List<PushInfromVO> pushInfromVOS = new ArrayList<>();
        for (PushInformEntity record : informEntityPage.getRecords()) {
            PushInfromVO pushInfromVO = new PushInfromVO();
            BeanUtils.copyProperties(record, pushInfromVO);
            pushInfromVO.setId(String.valueOf(record.getId()));
            pushInfromVO.setAcctName(infromIdsAcctMap.get(record.getIdStr()));
            pushInfromVOS.add(pushInfromVO);
        }
        pageInfo.setRecords(pushInfromVOS);
        pageInfo.setCurrent(informEntityPage.getCurrent());
        pageInfo.setSize(informEntityPage.getSize());
        pageInfo.setTotal(informEntityPage.getTotal());
        return pageInfo;
    }

    /**
     *@Author: Pipi
     *@Description: (物业端)获取单条消息详情
     *@Param: id: 消息ID
     *@Return: com.jsy.community.entity.PushInformEntity
     *@Date: 2021/4/20 16:23
     **/
    @Override
    public PushInfromVO getDetail(Long id) {
        PushInformEntity pushInformEntity = communityInformMapper.selectById(id);
        PushInfromVO pushInfromVO = new PushInfromVO();
        if (pushInformEntity == null) {
            return pushInfromVO;
        }
        BeanUtils.copyProperties(pushInformEntity, pushInfromVO);
        // 查询消息的社区列表
        QueryWrapper<InformAcctEntity> informAcctQueryWrapper = new QueryWrapper<>();
        informAcctQueryWrapper.eq("inform_id", id);
        List<InformAcctEntity> informAcctEntities = informAcctMapper.selectList(informAcctQueryWrapper);
        if (CollectionUtil.isNotEmpty(informAcctEntities)) {
            for (InformAcctEntity informAcctEntity : informAcctEntities) {
                if (CollectionUtil.isNotEmpty(pushInfromVO.getAcctName())) {
                    pushInfromVO.getAcctName().add(informAcctEntity.getAcctName());
                } else {
                    List<String> acctName = new ArrayList<>();
                    acctName.add(informAcctEntity.getAcctName());
                    pushInfromVO.setAcctName(acctName);
                }
            }
        }
        return pushInfromVO;
    }

    /**
     * @author: Pipi
     * @description: 新增推送消息与推送者关系数据
     * @param qo: 推送消息qo
     * @param informId: 推送消息id
     * @return: void
     * @date: 2021/8/3 10:24
     **/
    protected void insetInformAcct(PushInformQO qo, Long informId) {
        List<InformAcctEntity> informAcctEntities = new ArrayList<>();
        // 获取社区信息
        QueryWrapper<CommunityEntity> communityEntityQueryWrapper = new QueryWrapper<>();
        communityEntityQueryWrapper.in("id", qo.getCommunityIds());
        List<CommunityEntity> communityEntityList = communityMapper.selectList(communityEntityQueryWrapper);
        if (CollectionUtil.isNotEmpty(communityEntityList)) {
            for (CommunityEntity communityEntity : communityEntityList) {
                // 组装消息与推送号的关系数据
                InformAcctEntity informAcctEntity = new InformAcctEntity();
                informAcctEntity.setId(String.valueOf(SnowFlake.nextId()));
                informAcctEntity.setInformId(String.valueOf(informId));
                informAcctEntity.setAcctId(String.valueOf(communityEntity.getId()));
                informAcctEntity.setAcctName(communityEntity.getName());
                informAcctEntity.setAcctAvatar(communityEntity.getIconUrl());
                informAcctEntities.add(informAcctEntity);
                //当某个推送号有新消息发布时：用户之前已经删除的 推送号 又会被拉取出来 同时通知有未读消息
                //清除推送消息屏蔽表
                communityInformMapper.clearPushDel(communityEntity.getId());
            }
        }
        if (CollectionUtil.isNotEmpty(informAcctEntities)) {
            // 新增消息与推送号的关系数据
            informAcctMapper.insertBatch(informAcctEntities);
        }
    }
}
