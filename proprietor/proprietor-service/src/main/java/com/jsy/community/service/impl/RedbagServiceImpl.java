package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jsy.community.api.IRedbagService;
import com.jsy.community.api.IUserAccountService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.PaymentEnum;
import com.jsy.community.entity.RedbagEntity;
import com.jsy.community.mapper.RedbagMapper;
import com.jsy.community.qo.RedbagQO;
import com.jsy.community.qo.proprietor.UserAccountTradeQO;
import com.jsy.community.utils.AESOperator;
import com.jsy.community.utils.MD5Util;
import com.jsy.community.utils.MyHttpUtils;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.http.client.methods.HttpPost;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author chq459799974
 * @description 红包实现类
 * @since 2021-01-18 14:29
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class RedbagServiceImpl implements IRedbagService {
	
	@Autowired
	private RedbagMapper redbagMapper;
	
	@Autowired
	private IUserAccountService userAccountService;
	
	/**
	 * @Description: 发红包
	 * @Param: [redbagQO]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/1/18
	 **/
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void sendRedbag(RedbagQO redbagQO){
		//生成红包uuid
		String uuid = UUID.randomUUID().toString().replace("-","");
		redbagQO.setUuid(uuid);
		//扣余额、写流水
		UserAccountTradeQO tradeQO = new UserAccountTradeQO();
		tradeQO.setUid(redbagQO.getUserUuid());
		tradeQO.setTradeFrom(PaymentEnum.TradeFromEnum.TRADE_FROM_REDBAG.getIndex());
		tradeQO.setTradeType(PaymentEnum.TradeTypeEnum.TRADE_TYPE_EXPEND.getIndex());
		tradeQO.setTradeAmount(redbagQO.getMoney());
		userAccountService.trade(tradeQO);
		//新增红包
		RedbagEntity redbagEntity = new RedbagEntity();
		redbagEntity.setId(SnowFlake.nextId());
		BeanUtils.copyProperties(redbagQO,redbagEntity);
		redbagEntity.setName(StringUtils.isEmpty(redbagEntity.getName()) ? "恭喜发财" : redbagEntity.getName());
		int addRedbag = redbagMapper.insert(redbagEntity);
		if(addRedbag != 1){
			throw new ProprietorException("红包添加失败");
		}
		//调用发红包接口
//		boolean b = sendRedbagByHttp(redbagQO);
//		if(!b){
//			throw new ProprietorException("红包发送失败");
//		}
	}
	
	/**
	 * http调用发红包接口
	 */
	private boolean sendRedbagByHttp(RedbagQO redbagQO){
		//加密data
		String redbagData = AESOperator.encrypt(JSON.toJSONString(redbagQO));
		//获取系统时间
		Long time = System.currentTimeMillis();
		//得到MD5签名
		String sign = MD5Util.getSign(redbagData, time);
		//组装请求body
		Map<String, Object> bodyMap = new HashMap<>();
		//添加body参数
		bodyMap.put("time",time);
		bodyMap.put("signature",sign);
		bodyMap.put("operator","");
		bodyMap.put("data",redbagData);
		//组装http请求
		HttpPost httpPost = MyHttpUtils.httpPostWithoutParams("http://xxxxxxxxxxxx", bodyMap);
		//设置header
		MyHttpUtils.setDefaultHeader(httpPost);
		//设置默认配置
		MyHttpUtils.setRequestConfig(httpPost);
		String httpResult = null;
		JSONObject result = null;
//		try{
//			//执行请求，解析结果
//			httpResult = MyHttpUtils.exec(httpPost);
//			result = JSONObject.parseObject(httpResult);
//		}catch (Exception e) {
//			log.error("发红包 - 执行或解析出错，json解析结果" + result);
//			e.printStackTrace();
//		}
//		if(result != null){
//			return result.getBooleanValue("success");
//		}
		return false;
	}
	
	/**
	* @Description: 红包领取
	 * @Param: [redbagQO]
	 * @Return: java.util.Map<java.lang.String,java.lang.Object>
	 * @Author: chq459799974
	 * @Date: 2021/1/18
	**/
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String,Object> receiveRedbag(RedbagQO redbagQO){
		if(BusinessConst.REDBAG_TYPE_PRIVATE.equals(redbagQO.getRedbagType())){ //单领红包
			//查询
			RedbagEntity entity = redbagMapper.selectOne(new QueryWrapper<RedbagEntity>().select("uuid","receive_user_uuid","money")
				.eq("uuid", redbagQO.getUuid())
				.eq("receive_user_uuid",redbagQO.getReceiveUserUuid()));
			if(entity == null){
				throw new ProprietorException("红包不存在");
			}else if(entity.getMoney().compareTo(BigDecimal.ZERO) == 0){
				throw new ProprietorException("红包已经领过了");
			}
			//收取私包
			RedbagEntity redbagEntity = new RedbagEntity();
			redbagEntity.setMoney(BigDecimal.ZERO);
			redbagEntity.setReceivedCount(1);
			int updateRedbag = redbagMapper.update(redbagEntity, new UpdateWrapper<RedbagEntity>().eq("uuid", entity.getUuid()));
			if(updateRedbag != 1){
				throw new ProprietorException("红包领过失败");
			}
			//增加账户金额
			UserAccountTradeQO tradeQO = new UserAccountTradeQO();
			tradeQO.setUid(entity.getReceiveUserUuid());
			tradeQO.setTradeFrom(PaymentEnum.TradeFromEnum.TRADE_FROM_REDBAG.getIndex());
			tradeQO.setTradeType(PaymentEnum.TradeTypeEnum.TRADE_TYPE_INCOME.getIndex());
			tradeQO.setTradeAmount(entity.getMoney());
			userAccountService.trade(tradeQO);
		}else if(BusinessConst.REDBAG_TYPE_GROUP.equals(redbagQO.getRedbagType())){ //群红包
			RedbagEntity entity = redbagMapper.selectOne(new QueryWrapper<RedbagEntity>().select("*")
				.eq("uuid", redbagQO.getUuid())
				.gt("money",0));
			if(entity == null){
				throw new ProprietorException("红包已领完");
			}
		}
		return null;
	}
	
	//退红包  入uuid   返money
	public Map<String,Object> sendBackRedbag(String uuid){
		Map<String, Object> map = new HashMap<>();
		RedbagEntity entity = redbagMapper.selectOne(new QueryWrapper<RedbagEntity>().select("*").eq("uuid", uuid));
		if(entity == null || entity.getMoney().compareTo(BigDecimal.ZERO) <= 0){
			throw new ProprietorException("红包不见了 请联系管理员");
		}
		
		return null;
	}
	
}
