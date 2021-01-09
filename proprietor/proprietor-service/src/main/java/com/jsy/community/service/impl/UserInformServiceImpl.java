package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IUserInformService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserInformEntity;
import com.jsy.community.mapper.UserInformMapper;
import com.jsy.community.vo.InformListVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

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



    /**
     * @author YuLF
     * @since  2020/12/14 18:07
     * @param userId        用户ID
     * @return              返回总消息未读列表数据
     */
    @Transactional
    @Override
    public List<InformListVO> totalCommunityInformList(String userId) {

        //1. 查出当前用户拥有的可推送小区ID,只要这个用户在这个小区有房子就可以推送
        List<Long> communityIds = userInformMapper.queryUserAllCommunityId(userId);
        //2. 查出 推送消息 推送至所有 小区的 推送号ID
        List<Long> pushInformIds = userInformMapper.queryPushAcctIds(userId);

        //合并两个List id
        Set<Long> ids = mergeList(communityIds, pushInformIds);

        if( ids == null ){
            return null;
        }
        List<InformListVO> vos = new ArrayList<>(ids.size());
        ids.forEach( id -> {
            //1.查出当前推送消息id 用户已读数量
            Integer readCount = userInformMapper.queryReadInformById(userId, id);
            //2.查出当前推送消息id 推送消息总计数
            Integer totalCount = userInformMapper.queryPushInformTotalCount(id);
            //3.获得当前推送消息id 最新时间的第一条消息 标题、创建时间
            InformListVO vo = userInformMapper.queryLatestInform(id);
            //4.总计数 - 已读数 得到未读数
            vo.setUnread(totalCount - readCount);
            vos.add(vo);
        });

        return vos;
    }

    /**
     * 合并两个List<Long>
     * @param var1  集合1
     * @param var2  集合2
     */
    private  Set<Long> mergeList(List<Long> var1, List<Long> var2){
        Set<Long> var3 = null;
        if( var1 != null && var2 != null ){
            var3 = new HashSet<>(var1.size() + var2.size());
            var3.addAll(var1);
            var3.addAll(var2);
            return var3;
        }
        if(var1 != null){
            var3 = new HashSet<>(var1.size());
            var3.addAll(var1);
        }
        if(var2 != null){
            var3 = new HashSet<>(var2.size());
            var3.addAll(var2);
        }
        return var3;
    }




}
