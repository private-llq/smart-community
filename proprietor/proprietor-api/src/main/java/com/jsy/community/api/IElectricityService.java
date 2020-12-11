package com.jsy.community.api;

import com.jsy.community.qo.proprietor.ElectricityQO;

/**
 * @program: com.jsy.community
 * @description: 电费service
 * @author: Hu
 * @create: 2020-12-11 09:30
 **/
public interface IElectricityService {

    void add(ElectricityQO electricityQO);
}
