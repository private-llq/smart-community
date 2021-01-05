package com.jsy.community.api.test;

import com.jsy.community.entity.test.PayData;

import java.util.Map;

/**
 * @ClassName：ISimulatePayService
 * @Description：TODO
 * @author：lihao
 * @date：2020/12/10 15:57
 * @version：1.0
 */
public interface ISimulatePayService {
	
	PayData getPayData(String number,Integer id);

	Map getPayDetails(String number, Long id);
}
