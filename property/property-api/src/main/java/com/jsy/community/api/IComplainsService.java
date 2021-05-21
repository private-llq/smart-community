package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.ComplainEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.ComplainFeedbackQO;
import com.jsy.community.qo.property.PropertyComplaintsQO;
import com.jsy.community.vo.admin.AdminInfoVo;

import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 投诉建议
 * @author: Hu
 * @create: 2020-12-23 15:49
 **/
public interface IComplainsService extends IService<ComplainEntity> {
    /**
     * @Description: 查询所有投诉信息
     * @author: Hu
     * @since: 2020/12/23 17:01
     * @Param:
     * @return:
     */
    Map<String, Object> listAll(BaseQO<PropertyComplaintsQO> baseQO,AdminInfoVo userInfo);
    /**
     * @Description: 反馈内容
     * @author: Hu
     * @since: 2020/12/23 16:58
     * @Param:
     * @return:
     */
    void feedback(ComplainFeedbackQO complainFeedbackQO, AdminInfoVo userInfo);
}
