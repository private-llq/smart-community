package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.CarEntranceQO;
import com.jsy.community.vo.property.CarEntranceVO;

import java.util.Map;

public interface ICarEntranceService extends IService<CarEntranceVO> {
    /**
     * @Description: 查询入场登记
     * @Param: [baseQO]
     * @Return: java.util.Map<java.lang.String,java.lang.Object>
     * @Author: Tian
     * @Date: 2021/9/14-15:55
     **/
    Map<String, Object> selectCarEntrance(BaseQO<CarEntranceQO> baseQO, Long communityId);
}
