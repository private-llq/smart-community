package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IUserInformService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserInformEntity;
import com.jsy.community.entity.sys.SysInformEntity;
import com.jsy.community.mapper.UserInformMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.CommunityVO;
import com.jsy.community.vo.sys.SysInformVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
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
@PropertySource(value = "classpath:common-service.properties",encoding = "UTF-8")
@DubboService(version = Const.version, group = Const.group)
public class UserInformServiceImpl extends ServiceImpl<UserInformMapper, UserInformEntity> implements IUserInformService {

    @Resource
    private UserInformMapper userInformMapper;

    @Value("${jsy.sys.inform-icon}")
    private String sysInformIcon;

    @Value("${jsy.sys.inform-name}")
    private String sysInformName;


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
        //社区消息查询
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
        //查询用户当前系统消息已读消息条数
        Integer sysReadInformCount = userInformMapper.querySysReadInform(userId);
        Integer sysUnreadInformCount = userInformMapper.querySysInformCount();
        //最新时间第一条系统消息的标题与时间
        CommunityVO sysVO = userInformMapper.querySysInform();
        //设置系统消息当前未读数量
        sysVO.setUnread(sysUnreadInformCount - sysReadInformCount );
        //设置系统消息的名称和头像访问地址
        sysVO.setAvatarUrl(sysInformIcon);
        sysVO.setName(sysInformName);
        list.add(sysVO);
        return list;
    }


    /**
     *  用户系统消息详情查看
     * @author YuLF
     * @since  2020/12/21 14:55
     */
    @Transactional
    @Override
    public SysInformEntity totalSysInformList(Long informId, String userId) {
        //1.查出该消息id的对象
        SysInformEntity vo = userInformMapper.querySysInformById(informId);
        //参数对象
        UserInformEntity userInformEntity = new UserInformEntity();
        userInformEntity.setUid(userId);
        userInformEntity.setInformStatus(1);
        userInformEntity.setInformId(informId);
        userInformEntity.setId(SnowFlake.nextId());
        userInformEntity.setSysInform(1);
        //2.在 t_user_inform 增加该数据该用户为已读
        userInformMapper.setInformReadByUser(userInformEntity);
        //3.系统消息浏览量++
        userInformMapper.incrementSysInformBrowse(informId);
        return vo;
    }


    /**
     * 验证系统消息是否存在
     * @author YuLF
     * @since  2020/12/21 16:58
     * @Param  informId     消息id
     */
    @Override
    public boolean sysInformExist(Long informId) {
        return userInformMapper.sysInformExist(informId) > 0;
    }


    /**
     * 用户系统消息主页列表查看
     * @author YuLF
     * @since  2020/12/21 17:32
     * @Param  baseQO   分页查询对象
     * @Param  userId   用户ID
     * @return          返回已读未读的系统消息列表
     */
    @Override
    public List<SysInformVO> userSysInformList(BaseQO<SysInformVO> baseQO, String userId) {
        baseQO.setPage((baseQO.getPage() - 1) * baseQO.getSize());
        //1.按分页条件查出系统消息数据
        List<SysInformVO> sysInformVOS = userInformMapper.selectSysInformPage(baseQO);

        if ( sysInformVOS == null || sysInformVOS.isEmpty() ){
            return null;
        }

        //取出当前系统消息最后一条的日期  用于查询用户已读的系统消息筛选条件
        String lastCreateTime = sysInformVOS.get(sysInformVOS.size() - 1).getCreateTime();
        //2.通过 当前系统消息最后一个的时间 和用户id 查出用户已读的系统消息列表Id
        List<Long> sysInformIds = userInformMapper.selectUserReadSysInform(userId, lastCreateTime);
        //3.标识用户已读消息
        for( SysInformVO vo : sysInformVOS ){
            if( sysInformIds.contains(vo.getId()) ){
                vo.setRead(true);
            }
        }
        return sysInformVOS;
    }
}
