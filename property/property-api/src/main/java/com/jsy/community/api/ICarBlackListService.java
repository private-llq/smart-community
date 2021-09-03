package com.jsy.community.api;
import com.jsy.community.entity.property.CarBlackListEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;


public interface ICarBlackListService {

    PageInfo <CarBlackListEntity> carBlackListPage(BaseQO<String> baseQO, Long communityId);

    Integer saveBlackList(CarBlackListEntity carBlackListEntity, Long communityId);

    Integer delBlackList(String uid);

    CarBlackListEntity carBlackListOne(String carNumber);
}
