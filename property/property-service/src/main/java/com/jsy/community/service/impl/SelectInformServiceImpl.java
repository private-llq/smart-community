package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.api.ISelectInformService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.InformIdsEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.UserInformEntity;
import com.jsy.community.mapper.InformIdsMapper;
import com.jsy.community.mapper.SelectInformMapper;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.qo.proprietor.UserInformQO;
import com.jsy.community.vo.UserInformVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-07 16:45
 **/
@DubboService(version = Const.version, group = Const.group)
public class SelectInformServiceImpl implements ISelectInformService {
    @Autowired
    private SelectInformMapper selectInformMapper;

    @Autowired
    private InformIdsMapper informIdsMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * @Description: 查询已读
     * @author: Hu
     * @since: 2020/12/9 9:44
     * @Param:
     * @return:
     */
    @Override
    public Map<String, Object> findList(UserInformQO userInformQO) {

        Page<UserInformQO> page = new Page<>(userInformQO.getPage(),userInformQO.getSize());
//        MyPageUtils.setPageAndSize(page,userInformQO);
        Map<String, Object> map = new HashMap<>();
        List<UserInformVO> list = selectInformMapper.findList(userInformQO);
        for (UserInformVO userInformVO : list) {
            System.out.println(userInformVO);
        }
        Long total = selectInformMapper.findCount(userInformQO);
        map.put("total",total);
        map.put("list",list);
        return map;
    }

    /**
     * @Description: 查询未读
     * @author: Hu
     * @since: 2020/12/9 9:44
     * @Param:
     * @return:
     */
    @Override
    public List<UserEntity> findNotList(UserInformQO userInformQO) {
        InformIdsEntity informIdsEntity = informIdsMapper.selectOne(new QueryWrapper<InformIdsEntity>().eq("community_id", userInformQO.getCommunityId()).eq("inform_id", userInformQO.getInformId()));
        System.out.println(informIdsEntity);
        String ids = informIdsEntity.getIds();
        String[] split = ids.split(",");

        List<UserInformEntity> list = selectInformMapper.selectList(new QueryWrapper<UserInformEntity>().eq("community_id", userInformQO.getCommunityId()).eq("inform_id", userInformQO.getInformId()));
        String[] yidu=new String[split.length];
        int xiabiao=0;
        for (UserInformEntity userInformEntity : list) {
            yidu[xiabiao]=userInformEntity.getUid();
            ++xiabiao;
        }
        for (int i=0;i<split.length;i++){
            for (int j=0;j< yidu.length;j++){
                if(split[i].equals(yidu[j])){
                    split[i]="";
                }
            }
        }
        System.out.println(split.toString());
        System.out.println(yidu.toString());
        List<UserEntity> uid = userMapper.selectList(new QueryWrapper<UserEntity>().in("uid", split));
        return uid;
    }
}
