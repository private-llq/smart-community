package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICommunityInformService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityInformEntity;
import com.jsy.community.entity.UserInformEntity;
import com.jsy.community.mapper.CommunityInformMapper;
import com.jsy.community.mapper.UserInformMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.CommunityInformQO;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
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
public class CommunityInformServiceImpl extends ServiceImpl<CommunityInformMapper, CommunityInformEntity> implements ICommunityInformService {

    @Resource
    private CommunityInformMapper communityInformMapper;

    @Resource
    private UserInformMapper userInformMapper;

    /**
     * 查询社区消息
     * @param communityEntity  参数实体
     * @return                 返回查询结果
     */
    @Override
    public List<CommunityInformEntity> queryCommunityInform(BaseQO<CommunityInformEntity> communityEntity) {

        QueryWrapper<CommunityInformEntity>  queryWrapper = new QueryWrapper<>();
        CommunityInformEntity query = communityEntity.getQuery();
        Page<CommunityInformEntity> objectPage = new Page<>(communityEntity.getPage(), communityEntity.getSize());
        queryWrapper.select("id,create_time,community_id,state,title,sub_title");
        queryWrapper.eq("enabled",1);
        queryWrapper.eq("community_id", query.getCommunityId());
        queryWrapper.last("ORDER BY create_time desc");
        Page<CommunityInformEntity> communityInformEntityPage = communityInformMapper.selectPage(objectPage, queryWrapper);

        //使用 communityInformEntityPage.getRecords().get(records.size() - 1).getCreateTime() 最后一个元素的时间 去作为条件查询当前社区用户已读信息，避免查询量过大的问题
        List<CommunityInformEntity> records = communityInformEntityPage.getRecords();

        if ( records == null || records.isEmpty() ){
            return null;
        }

        LocalDateTime createTime =records.get(records.size() - 1).getCreateTime();
        //集合中最后一个 元素的时间字符串
        String dateTimeAsString = getDateTimeAsString(createTime);

        //使用当前用户查询该用户在当前社区已读信息
        List<Long> integerList = userInformMapper.queryUserReadCommunityInform(query.getCommunityId(), query.getUid(), dateTimeAsString);

        //标识用户已读的数据
        for(CommunityInformEntity communityInformEntity : records){
            if(integerList.contains(communityInformEntity.getId())){
                communityInformEntity.setRead(true);
            }
        }
        return records;
    }

    private String getDateTimeAsString(LocalDateTime localDateTime) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      return localDateTime.format(formatter);
    }


    /**
     * 添加社区消息
     * @param communityInformEntity    接收社区消息参数的实体
     * @return                          返回是否添加成功
     */
    @Override
    public Boolean addCommunityInform(CommunityInformEntity communityInformEntity){
            communityInformEntity.setId(SnowFlake.nextId());
            return communityInformMapper.insert(communityInformEntity) > 0;
    }

    /**
     * 修改社区消息
     * @param communityInformQO 参数实体
     * @return                 返回是否修改成功
     */
    @Override
    public Boolean updateCommunityInform(CommunityInformQO communityInformQO) {
        return communityInformMapper.updateCommunityInform(communityInformQO) > 0;
    }

    /**
     *
     * 逻辑删除 社区消息
     * @param id    社区消息id
     * @return      返回删除成功
     */
    @Transactional
    @Override
    public Boolean delCommunityInform(Long id) {
        //物理删除：删除该条社区消息之前 先删除 所有用户已读消息记录
        userInformMapper.delUserReadInform(id);
        return communityInformMapper.deleteById(id) > 0;
    }


    /**
     * 社区主页 当前轮播消息 查询最近的  initialInformCount 条数量
     * @param initialInformCount     初始轮播消息条数
     * @return                       返回消息列表
     */
    @Override
    public List<CommunityInformEntity> rotationCommunityInform(Integer initialInformCount , Long communityId) {
        return communityInformMapper.rotationCommunityInform(initialInformCount, communityId);
    }

    /**
     *  用户社区消息详情查看
     */
    @Transactional
    @Override
    public CommunityInformEntity detailsCommunityInform(Long communityId, Long informId , String userId) {
        //根据社区id和消息id查出消息
        QueryWrapper<CommunityInformEntity> wrapper = new QueryWrapper<>();
        wrapper.select("title,create_time,update_time,browse_count,content,state");
        wrapper.eq("community_id", communityId);
        wrapper.eq("id", informId);
        CommunityInformEntity communityInformEntity = communityInformMapper.selectOne(wrapper);
        //标识用户已读该社区消息
        UserInformEntity userInformEntity = new UserInformEntity();
        userInformEntity.setId(SnowFlake.nextId());
        userInformEntity.setCommunityId(communityId);
        userInformEntity.setInformId(informId);
        userInformEntity.setInformStatus(1);
        userInformEntity.setUid(userId);
        userInformEntity.setSysInform(0);
        userInformMapper.setInformReadByUser(userInformEntity);
        //社区该消息的浏览量+1
        communityInformMapper.updateCommunityInformBrowseCount(communityId, informId);
        return communityInformEntity;
    }

    /**
     * 验证社区消息是否存在
     * @author YuLF
     * @since  2020/12/21 17:02
     */
    @Override
    public boolean informExist(Long communityId, Long informId) {
        return communityInformMapper.informExist(communityId, informId);
    }


}
