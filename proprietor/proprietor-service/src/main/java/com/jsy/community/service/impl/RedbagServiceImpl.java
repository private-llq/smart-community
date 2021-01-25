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
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.http.client.methods.HttpPost;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author chq459799974
 * @description 红包实现类
 * @since 2021-01-18 14:29
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_proprietor)
public class RedbagServiceImpl implements IRedbagService {
	
	private static final Random random = new Random();
	
	private static final String protocolType = "http://";
	private static final String host = "192.168.12.37";
	private static final String port = "52002";
	
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
		BeanUtils.copyProperties(redbagQO,redbagEntity);
		redbagEntity.setId(SnowFlake.nextId());
		redbagEntity.setName(StringUtils.isEmpty(redbagEntity.getName()) ? "恭喜发财" : redbagEntity.getName());
		int addRedbag = redbagMapper.insert(redbagEntity);
		if(addRedbag != 1){
			throw new ProprietorException("红包添加失败");
		}
		//调用发红包接口
		boolean b = sendRedbagByHttp(redbagQO);
		if(!b){
			throw new ProprietorException("调用远程服务，红包发送失败");
		}
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
		log.info("开始领取红包：" + redbagQO.getUuid() + "领取人：" + redbagQO.getReceiveUserUuid());
		Map<String, Object> returnMap = new HashMap<>();
		if(BusinessConst.BUSINESS_TYPE_PRIVATE_REDBAG.equals(redbagQO.getBusinessType())
		   || BusinessConst.BUSINESS_TYPE_TRANSFER.equals(redbagQO.getBusinessType())){ //私包 和 转账
			//查询
			RedbagEntity entity = redbagMapper.selectOne(new QueryWrapper<RedbagEntity>().select("*")
				.eq("uuid", redbagQO.getUuid())
				.eq("receive_user_uuid",redbagQO.getReceiveUserUuid())
				.eq("business_type",redbagQO.getBusinessType()));
			if(entity == null){
				throw new ProprietorException("没有该红包/转账记录");
			}else if(BusinessConst.REDBAG_STATUS_FINISHED.equals(entity.getStatus())){
				throw new ProprietorException("已经领过了");
			}else if(BusinessConst.REDBAG_STATUS_BACK.equals(entity.getStatus())){
				throw new ProprietorException("无法领取已退回的红包/转账");
			}
			//收取私包
			RedbagEntity redbagEntity = new RedbagEntity();
			redbagEntity.setMoney(BigDecimal.ZERO);
			redbagEntity.setStatus(BusinessConst.REDBAG_STATUS_FINISHED);
			redbagEntity.setReceivedCount(1);
			int updateRedbag = redbagMapper.update(redbagEntity, new UpdateWrapper<RedbagEntity>().eq("uuid", entity.getUuid()));
			if(updateRedbag != 1){
				throw new ProprietorException("领取失败");
			}
			//红包入账
			redbagToUserAccount(entity.getReceiveUserUuid(),entity.getMoney());
			//返回
//			returnMap.put("uuid",entity.getUuid());//红包id
//			returnMap.put("receiveUserUuid",entity.getReceiveUserUuid());//领取人
			returnMap.put("money",entity.getMoney());//领取金额
			//调用领取接口
			redbagQO.setMoney(entity.getMoney());
			boolean b = sendRedbagByHttp(redbagQO);
			if(!b){
				throw new ProprietorException("调用远程服务，红包/转账领取失败");
			}
		}else if(BusinessConst.BUSINESS_TYPE_GROUP_REDBAG.equals(redbagQO.getBusinessType())){ //群红包
			RedbagEntity entity = redbagMapper.selectOne(new QueryWrapper<RedbagEntity>().select("*")
				.eq("uuid", redbagQO.getUuid())
				.gt("money",0)
				.eq("business_type",redbagQO.getBusinessType()));
			if(entity == null){
				throw new ProprietorException("红包已领完");
			}
			//红包里剩余的钱
			BigDecimal remainMoney = entity.getMoney();
			//红包剩余量
			Integer remainSize = entity.getNumber() - entity.getReceivedCount();
			if(remainSize == 0){
				throw new ProprietorException("红包数量不够了 请联系管理员");
			}
			//随机出的手气红包
			BigDecimal randomMoney = getRandomMoney(remainSize, remainMoney);
			//领取红包
			RedbagEntity redbagEntity = new RedbagEntity();
			redbagEntity.setMoney(remainMoney.subtract(randomMoney));
			redbagEntity.setStatus(remainSize == 1 ? BusinessConst.REDBAG_STATUS_FINISHED : BusinessConst.REDBAG_STATUS_RECEIVING);
			redbagEntity.setReceivedCount(entity.getReceivedCount() + 1);
			int updateRedbag = redbagMapper.update(redbagEntity, new UpdateWrapper<RedbagEntity>().eq("uuid", entity.getUuid()));
			//红包入账
			redbagToUserAccount(redbagQO.getReceiveUserUuid(),randomMoney);
			//返回
//			returnMap.put("uuid",entity.getUuid());//红包id
//			returnMap.put("receiveUserUuid",redbagQO.getReceiveUserUuid());//领取人
			returnMap.put("money",randomMoney);//领取金额
			redbagQO.setMoney(randomMoney);
			//调用领取接口
			boolean b = sendRedbagByHttp(redbagQO);
			if(!b){
				throw new ProprietorException("调用远程服务，红包领取失败");
			}
		}
		return returnMap;
	}
	
	/**
	* @Description: 红包退回
	 * @Param: [uuid]
	 * @Return: java.util.Map<java.lang.String,java.lang.Object>
	 * @Author: chq459799974
	 * @Date: 2021/1/19
	**/
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String,Object> sendBackRedbag(String uuid){
		log.info("开始退回红包：" + uuid);
		Map<String, Object> returnMap = new HashMap<>();
		//查询红包
		RedbagEntity entity = redbagMapper.selectOne(new QueryWrapper<RedbagEntity>().select("*").eq("uuid", uuid));
		if(entity == null){
			throw new ProprietorException("红包不见了 请联系管理员");
		}else if(BusinessConst.REDBAG_STATUS_FINISHED.equals(entity.getStatus())){
			throw new ProprietorException("红包已被领完 无法退回");
		}else if(BusinessConst.REDBAG_STATUS_BACK.equals(entity.getStatus())){
			throw new ProprietorException("红包已经退回 请不要重复退回");
		}
		//红包剩余金额
		BigDecimal money = entity.getMoney();
		//清空红包金额
		RedbagEntity redbagEntity = new RedbagEntity();
		redbagEntity.setMoney(BigDecimal.ZERO);
		redbagEntity.setStatus(BusinessConst.REDBAG_STATUS_BACK);
		int updateRedbag = redbagMapper.update(redbagEntity, new UpdateWrapper<RedbagEntity>().eq("uuid", entity.getUuid()));
		if(updateRedbag != 1){
			throw new ProprietorException("红包退回失败");
		}
		//退回账户
		//增加账户金额
		UserAccountTradeQO tradeQO = new UserAccountTradeQO();
		tradeQO.setUid(entity.getUserUuid());
		tradeQO.setTradeFrom(PaymentEnum.TradeFromEnum.TRADE_FROM_REDBAG_BACK.getIndex());
		tradeQO.setTradeType(PaymentEnum.TradeTypeEnum.TRADE_TYPE_INCOME.getIndex());
		tradeQO.setTradeAmount(money);
		userAccountService.trade(tradeQO);
		//返回
//		returnMap.put("uuid",entity.getUuid());//红包id
//		returnMap.put("userUuid",entity.getUserUuid());//退回到人
		returnMap.put("money",entity.getMoney());//退回金额
		//调用退回接口
		RedbagQO redbagQO = new RedbagQO();
		redbagQO.setUuid(entity.getUuid());
		redbagQO.setUserUuid(entity.getUserUuid());
		redbagQO.setMoney(entity.getMoney());
		redbagQO.setBehavior(BusinessConst.BEHAVIOR_BACK);
		boolean b = sendRedbagByHttp(redbagQO);
		if(!b){
			throw new ProprietorException("调用远程服务，红包退回失败");
		}
		return returnMap;
	}
	
	/**
	 * http调用红包/转账接口
	 */
	private boolean sendRedbagByHttp(RedbagQO redbagQO){
		String url = "";
		if(BusinessConst.BEHAVIOR_SEND.equals(redbagQO.getBehavior())){
			if(BusinessConst.BUSINESS_TYPE_PRIVATE_REDBAG.equals(redbagQO.getBusinessType())){  //私包
				url = protocolType + host + ":" + port + "/imRedEnvelope/sendRedPacket";
			}else if(BusinessConst.BUSINESS_TYPE_GROUP_REDBAG.equals(redbagQO.getBusinessType())){  //群红包
				url = protocolType + host + ":" + port + "/imRedEnvelope/sendGroup";
			}else if(BusinessConst.BUSINESS_TYPE_TRANSFER.equals(redbagQO.getBusinessType())){  //转账
				url = protocolType + host + ":" + port + "/imTransferAccounts/sendTransferAccount";
			}else{
				return false;
			}
		}else if(BusinessConst.BEHAVIOR_RECEIVE.equals(redbagQO.getBehavior())){
			if(BusinessConst.BUSINESS_TYPE_PRIVATE_REDBAG.equals(redbagQO.getBusinessType())){  //私包
				url = protocolType + host + ":" + port + "/imRedEnvelope/robRedPacket";
			}else if(BusinessConst.BUSINESS_TYPE_GROUP_REDBAG.equals(redbagQO.getBusinessType())){  //群红包
				url = protocolType + host + ":" + port + "/imRedEnvelope/receiveGroup";
			}else if(BusinessConst.BUSINESS_TYPE_TRANSFER.equals(redbagQO.getBusinessType())){  //转账
				url = protocolType + host + ":" + port + "/imTransferAccounts/robTransferAccount";
			}else{
				return false;
			}
		}else if(BusinessConst.BEHAVIOR_BACK.equals(redbagQO.getBehavior())){  //退回
			url = protocolType + host + ":" + port + "/imRedEnvelope/refundRedPacket";
		}
		//加密data
//		String redbagData = AESOperator.encrypt(JSON.toJSONString(redbagQO));
		//获取系统时间
//		Long time = System.currentTimeMillis();
		//得到MD5签名
//		String sign = MD5Util.getSign(redbagData, time);
		//组装请求body
		Map<String, Object> bodyMap = new HashMap<>();
		//添加body参数
//		bodyMap.put("time",time);
//		bodyMap.put("signature",sign);
//		bodyMap.put("operator","");
		bodyMap.put("data",JSON.toJSONString(redbagQO));
		//组装http请求
		HttpPost httpPost = MyHttpUtils.httpPostWithoutParams(url, bodyMap);
		//设置header
		MyHttpUtils.setDefaultHeader(httpPost);
		//设置默认配置
		MyHttpUtils.setRequestConfig(httpPost);
		JSONObject result = null;
		String httpResult = null;
		try{
			//执行请求，解析结果
			httpResult = MyHttpUtils.exec(httpPost);
//			result = JSONObject.parseObject(httpResult);
		}catch (Exception e) {
			log.error("红包远程服务 - 执行或解析出错，json解析结果" + result);
			e.printStackTrace();
			return false;
		}
		System.out.println("返回结果：" + httpResult);
		return redbagQO.getUuid().equals(httpResult);
//		if(result != null){
//			return result.getIntValue("code") == 154;
//		}
//		return false;
	}
	
	/**
	 * 群红包-拼手气  remainSize 剩余的红包数量, remainMoney 剩余的钱
	 */
	private static BigDecimal getRandomMoney(Integer remainSize, BigDecimal remainMoney) {
		if (remainSize == 1) {
			return remainMoney;
		}
//		Random r = new Random();
		BigDecimal min   = new BigDecimal("0.01");
		BigDecimal max   = remainMoney.divide(new BigDecimal(remainSize), 2, RoundingMode.HALF_EVEN).multiply(new BigDecimal("2"));
		BigDecimal money = max.multiply(new BigDecimal(random.nextDouble())).setScale(2,RoundingMode.HALF_EVEN);
		money = money.compareTo(min) == -1 ? min : money;
		return money;
	}
	
	/**
	 * 红包入账
	 */
	private void redbagToUserAccount(String uuid, BigDecimal money){
		UserAccountTradeQO tradeQO = new UserAccountTradeQO();
		tradeQO.setUid(uuid);
		tradeQO.setTradeFrom(PaymentEnum.TradeFromEnum.TRADE_FROM_REDBAG.getIndex());
		tradeQO.setTradeType(PaymentEnum.TradeTypeEnum.TRADE_TYPE_INCOME.getIndex());
		tradeQO.setTradeAmount(money);
		userAccountService.trade(tradeQO);
	}
	
	/**
	 * 定时退回过期红包 10分钟1次
	 */
	@Scheduled(cron = "0 */10 * * * ?")
	public void sendBackExpiredRedbag(){
		log.info(Thread.currentThread().getId() + " 过期红包扫描定时任务执行"+ LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute());
		List<String> expiredRedbags = redbagMapper.queryExpiredRedbag();
		for(String uuid : expiredRedbags){
			try{
				sendBackRedbag(uuid);
			}catch (ProprietorException e){
				log.error(e.getMessage());
				continue;
			}
		}
		log.info(Thread.currentThread().getId() + " 过期红包扫描定时任务执行完成"+ LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute());
	}
}
