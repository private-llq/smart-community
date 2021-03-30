package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IComplainsService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.ComplainEntity;
import com.jsy.community.mapper.AdminUserMapper;
import com.jsy.community.mapper.ComplainsMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.ComplainFeedbackQO;
import com.jsy.community.qo.property.PropertyComplaintsQO;
import com.jsy.community.vo.ComplainVO;
import com.jsy.community.vo.admin.AdminInfoVo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 投诉建议
 * @author: Hu
 * @create: 2020-12-23 15:50
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class ComplainsServiceImpl extends ServiceImpl<ComplainsMapper, ComplainEntity> implements IComplainsService {
    @Autowired
    private ComplainsMapper complainsMapper;

    @Autowired
    private AdminUserMapper adminUserMapper;

    /**
     * @Description: 投诉建议反馈
     * @author: Hu
     * @since: 2020/12/23 17:00
     * @Param:
     * @return:
     */
    @Override
    public void feedback(ComplainFeedbackQO complainFeedbackQO, AdminInfoVo userInfo) {
        ComplainEntity complainEntity = complainsMapper.selectById(complainFeedbackQO.getId());
        complainEntity.setStatus(1);
        complainEntity.setFeedbackBy(userInfo.getUid());
        complainEntity.setFeedbackTime(LocalDateTime.now());
        complainEntity.setFeedbackContent(complainFeedbackQO.getBody());
        complainsMapper.updateById(complainEntity);
    }
    /**
     * @Description: 查询所有投诉信息
     * @author: Hu
     * @since: 2020/12/23 17:01
     * @Param:
     * @return:
     */
    @Override
    public Map<String, Object> listAll(BaseQO<PropertyComplaintsQO> baseQO) {
        if (baseQO.getSize()==null||baseQO.getSize()==0){
            baseQO.setSize(10L);
        }
        List<ComplainVO> complainVOS = complainsMapper.listAll(baseQO.getPage(), baseQO.getSize(), baseQO.getQuery());
        Long totel = complainsMapper.findTotel(baseQO.getQuery());
        Map<String, Object> map = new HashMap<>();
        map.put("totel",totel);
        map.put("list",complainVOS);
        return map;
    }
}
