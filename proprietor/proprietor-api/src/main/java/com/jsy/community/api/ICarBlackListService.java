package com.jsy.community.api;
import com.jsy.community.entity.proprietor.CarBlackListEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;


public interface ICarBlackListService {

    PageInfo <CarBlackListEntity> carBlackListPage(BaseQO<String> baseQO);

    Integer saveBlackList(CarBlackListEntity carBlackListEntity);

    Integer delBlackList(String uid);
}
