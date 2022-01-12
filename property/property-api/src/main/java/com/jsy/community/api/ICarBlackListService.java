package com.jsy.community.api;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.property.CarBlackListEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;


public interface ICarBlackListService extends IService<CarBlackListEntity> {

    PageInfo <CarBlackListEntity> carBlackListPage(BaseQO<String> baseQO, Long communityId);

    Integer saveBlackList(CarBlackListEntity carBlackListEntity, Long communityId);

    Integer delBlackList(String uid);

    CarBlackListEntity carBlackListOne(String carNumber,Long communityId);
}
