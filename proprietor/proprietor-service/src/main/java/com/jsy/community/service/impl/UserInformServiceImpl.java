package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IUserInformService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserInformEntity;
import com.jsy.community.mapper.UserInformMapper;
import com.jsy.community.qo.CommunityQO;
import com.jsy.community.vo.CommunityVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 通知消息接口实现类
 * </p>
 *
 * @author chq459799974
 * @since 2020-12-4
 */
@DubboService(version = Const.version, group = Const.group)
public class UserInformServiceImpl extends ServiceImpl<UserInformMapper, UserInformEntity> implements IUserInformService {

    @Resource
    private UserInformMapper userInformMapper;


    /**
     * @author YuLF
     * @since  2020/12/14 18:07
     * @param userId        用户ID
     * @param page          当前页
     * @param size          每次显示的条数
     * @return              返回总消息未读列表数据
     */
    @Transactional
    @Override
    public List<CommunityVO> totalCommunityInformList(String userId, Long page, Long size) {
        //1.查出 用户 所有小区 ids
        List<Long> communityIds = userInformMapper.queryUserAllCommunityId(userId, (page-1)*size, size);
        List<CommunityVO> list = new ArrayList<>(communityIds.size());
        for( Long communityId : communityIds ){
            //1.根据社区ID和用户id 查出用户在当前社区已读条数
            Integer readInformCount = userInformMapper.queryReadInformById(userId, communityId);
            //2.查出当前社区 消息总计数
            Integer communityInformCount = userInformMapper.queryCommunityInformTotalCount(communityId);
            //这个社区信息 名称、头像、id、社区最新消息第一条的title、create_time
            CommunityVO communityVO = userInformMapper.queryCommunityInform(communityId);
            //3.当前社区消息总计数 - 用户已读消息计数用户未读消息数量
            communityVO.setUnread(communityInformCount - readInformCount);
            list.add(communityVO);
        }
        //TODO: 查询系统消息
        return list;
    }
}
