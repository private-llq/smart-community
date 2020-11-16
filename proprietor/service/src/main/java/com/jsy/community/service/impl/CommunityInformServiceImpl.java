package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.api.ICommunityInformService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityInformEntity;
import com.jsy.community.mapper.CommunityInformMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.qo.BaseQO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

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
@DubboService(version = Const.version, group = Const.group)
public class CommunityInformServiceImpl extends ServiceImpl<CommunityInformMapper, CommunityInformEntity> implements ICommunityInformService {

    @Resource
    private CommunityInformMapper communityInformMapper;

    //页面起始页查询社区消息的初始条数 暂定10
    //@Value("${jsy.community-inform.initial.count}")
    private final Integer initialInformCount = 10;

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
        //初次进入社区页面时固定查询条数
        if(query.isInitialQuery()){
            //在sql最后追加排序条件 初次查询为
            queryWrapper.last("ORDER BY index asc limit 0,"+initialInformCount);
        }
        return page(objectPage, queryWrapper);
    }

    /**
     * 添加社区消息
     * @param communityInformEntity    接收社区消息参数的实体
     * @return                          返回是否添加成功
     */
    @Override
    public Boolean addCommunityInform(CommunityInformEntity communityInformEntity) {
        return communityInformMapper.insert(communityInformEntity) > 0;
    }

    /**
     * 修改社区消息
     * @param communityEntity  参数实体
     * @return                 返回是否修改成功
     */
    @Override
    public Boolean updateCommunityInform(CommunityInformEntity communityEntity) {
        int update = communityInformMapper.update(communityEntity, new UpdateWrapper<CommunityInformEntity>().eq("id", communityEntity.getId()).eq("community_id", communityEntity.getCommunityId()).eq("deleted", 0));
        return update > 0;
    }

    /**
     *
     * 逻辑删除 社区消息
     * @param id    社区消息id
     * @return      返回删除成功
     */
    @Override
    public Boolean delCommunityInform(Long id) {
        return communityInformMapper.deleteById(id) > 0;
    }
}
