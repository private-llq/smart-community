package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyVoteService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.VoteEntity;
import com.jsy.community.mapper.PropertyVoteMapper;
import com.jsy.community.qo.BaseQO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 业主投票
 * @author: Hu
 * @create: 2021-09-23 10:06
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class PropertyVoteServiceImpl extends ServiceImpl<PropertyVoteMapper,VoteEntity> implements IPropertyVoteService {

    @Autowired
    private PropertyVoteMapper propertyVoteMapper;



    /**
     * @Description: 分页查询
     * @author: Hu
     * @since: 2021/9/23 10:46
     * @Param: [baseQO, adminCommunityId]
     * @return: java.util.List<com.jsy.community.entity.proprietor.VoteEntity>
     */
    @Override
    public List<VoteEntity> list(BaseQO<VoteEntity> baseQO, Long adminCommunityId) {
        return null;
    }

    /**
     * @Description: 新增
     * @author: Hu
     * @since: 2021/9/23 10:46
     * @Param: [voteEntity]
     * @return: void
     */
    @Override
    public void saveBy(VoteEntity voteEntity) {

    }

    /**
     * @Description: 查详情
     * @author: Hu
     * @since: 2021/9/23 10:46
     * @Param: [id]
     * @return: void
     */
    @Override
    public void getOne(Long id) {

    }

    /**
     * @Description: 查图表
     * @author: Hu
     * @since: 2021/9/23 10:47
     * @Param:
     * @return:
     */
    @Override
    public void getChart(Long id) {

    }
}
