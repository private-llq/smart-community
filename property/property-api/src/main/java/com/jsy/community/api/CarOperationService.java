package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.CarOperationLog;
import com.jsy.community.qo.property.CarOperationLogQO;
import com.jsy.community.vo.property.PageVO;

import javax.annotation.Resource;

public interface CarOperationService extends IService<CarOperationLog> {


    PageVO selectCarOperationLogPag(CarOperationLogQO qo);
}
