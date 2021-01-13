package com.jsy.community.service.impl.test;

import cn.hutool.core.util.RandomUtil;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.api.test.ISimulatePayService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PayCompanyEntity;
import com.jsy.community.entity.test.PayData;
import com.jsy.community.entity.test.SimulateTypeEntity;
import com.jsy.community.mapper.PayCompanyMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lihao
 * @ClassName SimulatePayServiceImpl
 * @Date 2020/12/10  15:58
 * @Description TODO
 * @Version 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_proprietor)
public class SimulatePayServiceImpl implements ISimulatePayService {

	@Autowired
	private PayCompanyMapper payCompanyMapper;
	
	@Override
	public PayData getPayData(String number, Integer id) {
		if (id == 1) {
			SimulateTypeEntity simulateTypeEntity = new SimulateTypeEntity();
			simulateTypeEntity.setCompany("重庆水务公司");
			if (simulateTypeEntity.getCompany().equals("重庆水务公司")) {
				if (number.equals("1")) { // 如果是   重庆水务公司1     如果户号是  1   余额不够
					PayData data = getData(number, simulateTypeEntity);
					log.info("该户号信息：" + data.toString());
					return data;
				}
				if (number.equals("2")) { // 如果是   重庆水务公司1     如果户号是  2   余额够
					PayData data = getData(number, simulateTypeEntity);
					data.setPayBalance(new BigDecimal(56));
					data.setPayExpen(new BigDecimal(177));
					log.info("该户号信息：" + data.toString());
					return data;
				}
			}
			
			if (simulateTypeEntity.getCompany().equals("重庆电力公司")) {
				if (number.equals("1")) { // 如果是   重庆水务公司1     如果户号是  1   余额不够
					PayData data = getData(number, simulateTypeEntity);
					log.info("该户号信息：" + data.toString());
					return data;
				}
				if (number.equals("2")) { // 如果是   重庆水务公司1     如果户号是  2   余额够
					PayData data = getData(number, simulateTypeEntity);
					data.setPayBalance(new BigDecimal(56));
					data.setPayExpen(new BigDecimal(177));
					log.info("该户号信息：" + data.toString());
					return data;
				}
			}
		}
		throw new ProprietorException("您的查询不存在");
	}



	@Override
	public Map getPayDetails(String number, Long id) {
		PayCompanyEntity entity = payCompanyMapper.selectById(id);
		Map map = new HashMap();
		map.put("username","纵横世纪");
		map.put("doorNo",number);
		map.put("typeID",entity.getId());
		map.put("typeName",entity.getName());
		map.put("balance",RandomUtil.randomInt(-500,500));
		map.put("address","天王星b座1810");
		return map;
	}

	private PayData getData(String number, SimulateTypeEntity type) {
		PayData payData = new PayData();
		Integer id = Integer.parseInt(number);
		payData.setId(id);
		payData.setName(number + "李四");
		payData.setAddress(number + "重庆");
		payData.setNumber(number);
		payData.setCompany(type.getCompany());
		payData.setPayExpen(new BigDecimal(67.81));
		payData.setPayBalance(new BigDecimal(32));
		return payData;
	}
	
	
	public static void main(String[] args) {
		SimulatePayServiceImpl simulatePayService = new SimulatePayServiceImpl();
		simulatePayService.getPayData("2", 1);
	}
	
}
