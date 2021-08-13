package com.jsy.community.api;

import com.jsy.community.entity.proprietor.CarLaneEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface ICarLaneService {

    Integer SaveCarLane(CarLaneEntity CarLaneEntity);

    Integer UpdateCarLane(CarLaneEntity CarLaneEntity);

    Integer DelCarLane(String uid);

    PageInfo FindByLaneNamePage(BaseQO<String> baseQO);

    PageInfo FindByLaneNamePage2(BaseQO<String> baseQO);


}