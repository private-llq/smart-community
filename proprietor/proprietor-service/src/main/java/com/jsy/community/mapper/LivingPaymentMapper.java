package com.jsy.community.mapper;

import com.jsy.community.vo.DefaultHouseOwnerVO;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 生活缴费mapper层
 * @author: Hu
 * @create: 2020-12-11 09:31
 **/
public interface LivingPaymentMapper {

    List<DefaultHouseOwnerVO> selectList(String userId);
}
