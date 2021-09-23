package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CarOrderEntity;
import com.jsy.community.entity.property.CarQREntity;

public interface ICaQRService extends IService<CarQREntity> {
/**
 * @Description: 生成二维码 并将地址保存到数据库
 * @Param: [path, communityId]
 * @Return: java.lang.Boolean
 * @Author: Tian
 * @Date: 2021/9/23-10:08
 **/
    Boolean addQRCode(String path,Long communityId);

    /**
     * @Description: 查询当前社区是否已经生成二维码
     * @Param: [communityId]
     * @Return: com.jsy.community.entity.property.CarQREntity
     * @Author: Tian
     * @Date: 2021/9/23-10:07
     **/
    CarQREntity findOne(Long communityId);

    boolean updateQRCode(CarQREntity carQREntity);
}
