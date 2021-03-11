package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IComplainsService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.ComplainEntity;
import com.jsy.community.mapper.ComplainsMapper;
import com.jsy.community.qo.property.ComplainFeedbackQO;
import com.jsy.community.vo.ComplainVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * @Description: 投诉建议反馈
     * @author: Hu
     * @since: 2020/12/23 17:00
     * @Param:
     * @return:
     */
    @Override
    public void feedback(ComplainFeedbackQO complainFeedbackQO) {
        ComplainEntity complainEntity = complainsMapper.selectById(complainFeedbackQO.getId());
        complainEntity.setStatus(2);
        complainEntity.setComplainTime(LocalDateTime.now());
        complainEntity.setFeedback(complainFeedbackQO.getContent());
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
    public List<ComplainVO> listAll() {
        return complainsMapper.listAll();
    }
}
