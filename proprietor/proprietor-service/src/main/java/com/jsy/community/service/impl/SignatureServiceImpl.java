package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.api.ISignatureService;
import com.jsy.community.constant.Const;
import com.jsy.community.dto.signature.SignResult;
import com.jsy.community.dto.signature.SignatureUserDTO;
import com.jsy.community.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import java.util.Map;


/**
 * @author chq459799974
 * @description 签章相关 实现类
 * @since 2021-02-23 17:38
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_proprietor)
public class SignatureServiceImpl implements ISignatureService {
	
	private static final String protocolType = "http://";
//		private static final String host = "192.168.12.37";
	private static final String host = "222.178.212.29";
	private static final String port = "13000";
	
	//POST 新增用户信息
	@Override
	public boolean insertUser(SignatureUserDTO signatureUserDTO){
		return sendRedbagByHttp(1, signatureUserDTO);
	}
	
	//POST 批量新增用户信息(物业端批量导入时)
	@Override
	public boolean batchInsertUser(SignatureUserDTO signatureUserDTO){
		return sendRedbagByHttp(2,signatureUserDTO);
	}
	
	//PUT 实名认证后修改签章用户信息
	@Override
	public boolean realNameUpdateUser(SignatureUserDTO signatureUserDTO){
		return sendRedbagByHttp(3,signatureUserDTO);
	}
	
	//PUT 修改用户普通信息
	@Override
	public boolean updateUser(SignatureUserDTO signatureUserDTO){
		return sendRedbagByHttp(4,signatureUserDTO);
	}
	
	/**
	 * 数据加密
	 */
	public SignResult<String> getParamEntity(SignatureUserDTO signatureUserDTO) {
		String encrypt = AESUtil.encrypt(JSON.toJSONString(signatureUserDTO), "?b@R~@Js6yH`aFal=LAHg?l~K|ExYJd;", "1E}@+?f-voEy;_?r");
		SignResult<String> success = SignResult.success(encrypt);
		Map map = JSON.parseObject(JSON.toJSONString(success), Map.class);
		map.put("secret", "巴拉啦小魔仙");
		map.put("time", success.getTime());
		String signStr = MD5Util.signStr(map);
		String md5Str = MD5Util.getMd5Str(signStr);
		success.setSign(md5Str);
		return success;
	}
	
	/**
	 * http调用签章接口
	 */
	private boolean sendRedbagByHttp(int type, SignatureUserDTO signatureUserDTO){
		HttpPost httpPost = null;
		HttpPut httpPut = null;
		String url = "";
		long id = SnowFlake.nextId();
		//远程服务调用id
		switch(type){
			case 1:
				url = protocolType + host + ":" + port + "/LongRange/insertUser";
				log.info("ID：" + id + "签章服务 - 准备调用：" + SignatureBehaveEnum.BEHAVE_INSERT_USER.getName());
				log.info("用户：" + signatureUserDTO.getUuid());
				break;
			case 2:
				url = protocolType + host + ":" + port + "/LongRange/batchInsertUser";
				log.info("ID：" + id + "签章服务 - 准备调用：" + SignatureBehaveEnum.BEHAVE_BATCH_INSERT_USER.getName());
				log.info("用户：" + signatureUserDTO.getUuid());
				break;
			case 3:
				url = protocolType + host + ":" + port + "/LongRange/RealNameUpdateUser";
				log.info("ID：" + id + "签章服务 - 准备调用：" + SignatureBehaveEnum.BEHAVE_REALNAME_UPDATE_USER.getName());
				log.info("用户：" + signatureUserDTO.getUuid());
				break;
			case 4:
				url = protocolType + host + ":" + port + "/LongRange/updateUser";
				log.info("ID：" + id + "签章服务 - 准备调用：" + SignatureBehaveEnum.BEHAVE_UPDATE_USER.getName());
				log.info("用户：" + signatureUserDTO.getUuid());
				break;
			default:
		}
		//获取加密对象
//		OpenParam openParam = AESUtil.returnOpenParam(redbagQO);
		//组装请求body
//		Map<String, Object> bodyMap = JSONObject.parseObject(JSON.toJSONString(openParam), Map.class);

//		if(type == 1 || type == 2){
//			httpPost = MyHttpUtils.httpPostWithoutParams(url, MapBeanUtil.object2Map(signatureUserDTO));
//		}else if(type == 3 || type == 4){
//			httpPut = MyHttpUtils.httpPutWithoutParams(url, MapBeanUtil.object2Map(signatureUserDTO));
//		}
		
		httpPost = MyHttpUtils.httpPostWithoutParams(url, MapBeanUtil.object2Map(getParamEntity(signatureUserDTO)));
		
		//设置默认header
		MyHttpUtils.setDefaultHeader(httpPost != null ? httpPost : httpPut);
		//设置默认配置
		MyHttpUtils.setRequestConfig(httpPost != null ? httpPost : httpPut);
		JSONObject result;
		String httpResult = null;
		try{
			//执行请求，解析结果
			httpResult = (String)MyHttpUtils.exec(httpPost != null ? httpPost : httpPut,MyHttpUtils.ANALYZE_TYPE_STR);
			result = JSONObject.parseObject(httpResult);
			if(result == null || result.getIntValue("code") != 0){
				log.error("ID：" + id + "签章用户远程服务 - 调用返回code非0：\n" + httpResult);
				log.info("用户：" + signatureUserDTO.getUuid());
				return false;
			}
		}catch (Exception e) {
			log.error("ID：" + id + "签章用户远程服务 - 调用或解析出错，调用返回：\n" + httpResult);
			log.info("用户：" + signatureUserDTO.getUuid());
			return false;
		}
		log.info("ID：" + id + "签章用户远程服务 - 调用成功：\n" + httpResult);
		log.info("用户：" + signatureUserDTO.getUuid());
		return true;
	}
	
}

/**
 * @Description: 签章接口枚举
 * @Author: chq459799974
 * @Date: 2020/2/24
 **/
enum SignatureBehaveEnum {
	BEHAVE_INSERT_USER("新增用户", 1),
	BEHAVE_BATCH_INSERT_USER("批量新增用户", 2),
	BEHAVE_REALNAME_UPDATE_USER("更新用户实名认证信息",3),
	BEHAVE_UPDATE_USER("更新用户普通信息",4);
	private String name;
	private Integer code;
	SignatureBehaveEnum(String name, Integer code) {
		this.name = name;
		this.code = code;
	}
	public String getName() {
		return name;
	}
}
