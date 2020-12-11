package com.jsy.community.api.test;

import com.jsy.community.entity.test.PayData;
import com.jsy.community.entity.test.SimulateTypeEntity;

import java.util.List;

/**
 * @ClassName：ISimulatePayService
 * @Description：TODO
 * @author：lihao
 * @date：2020/12/10 15:57
 * @version：1.0
 */
public interface ISimulatePayService {
	List<SimulateTypeEntity> getCompany(Integer type);
	
	PayData getPayData(String number,Integer id);
}
