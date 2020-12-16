package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IAdminCommunityInformService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityInformEntity;
import com.jsy.community.mapper.AdminCommunityInformMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.CommunityInformQO;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


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
public class AdminCommunityInformServiceImpl extends ServiceImpl<AdminCommunityInformMapper, CommunityInformEntity> implements IAdminCommunityInformService {

    @Resource
    private AdminCommunityInformMapper communityInformMapper;

    /**
     * 查询社区消息
     * @param communityEntity  参数实体
     * @return                 返回查询结果
     */
    @Override
    public Page<CommunityInformEntity> queryCommunityInform(BaseQO<CommunityInformEntity> communityEntity) {
        QueryWrapper<CommunityInformEntity>  queryWrapper = new QueryWrapper<>();
        CommunityInformEntity query = communityEntity.getQuery();
        Page<CommunityInformEntity> objectPage = new Page<>(communityEntity.getPage(), communityEntity.getSize());
        queryWrapper.select("id,create_time,community_id,state,title,sub_title");
        queryWrapper.eq("enabled",1);
        queryWrapper.eq("community_id", query.getCommunityId());
        queryWrapper.last("ORDER BY create_time desc");
        return communityInformMapper.selectPage(objectPage, queryWrapper);
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
        communityInformMapper.delUserReadInform(id);
        return communityInformMapper.deleteById(id) > 0;
    }


}
